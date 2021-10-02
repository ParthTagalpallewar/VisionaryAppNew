package com.reselling.visionary.ui.gpsLocation

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.models.userModel.User
import com.reselling.visionary.data.models.userModel.UserResponseModel
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.repository.AuthRepository
import com.reselling.visionary.data.repository.LocationRepository
import com.reselling.visionary.utils.Constants
import com.reselling.visionary.utils.Constants.Companion.LocationProviderConstant
import com.reselling.visionary.utils.getLocation
import com.reselling.visionary.utils.saveConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject


private const val TAG = "GpsFragmentViewModel"

@HiltViewModel
class GpsFragmentViewModel @Inject constructor(
        private val locationRepository: LocationRepository,
        private val preferencesManager: PreferencesManager,
        private val authRepository: AuthRepository,
        private val locationManager: LocationManager
) : ViewModel(), LocationListener {

    init {
        viewModelScope.launch {
            val constantsResponse = authRepository.getConstants()

            when (constantsResponse) {
                is Resource.Success -> {
                    val constants: com.reselling.visionary.data.models.dataModels.Constants = constantsResponse.value.constants
                    saveConstants(constants.enablePhoneVerification, constants.getProvider)

                }
            }
        }
    }

    var doesUpdateNeeded = true

    @SuppressLint("MissingPermission")
    fun makeLocationCall() {
        doesUpdateNeeded = true
        when {

            !locationManager.isProviderEnabled(LocationProviderConstant) -> {
                viewModelScope.launch {
                    _gpsLocationFragmentChannel.send(GpsLocationEvents.RequestGpsEnable)

                }
            }

            //when both req are granted
            locationRepository.bothPermissionResult() -> {


                viewModelScope.launch {
                    //send loading channel
                    _gpsLocationFragmentChannel.send(GpsLocationEvents.LoadingEvent)

//                    Log.e("Resquest Location", "req")


                    //request location
                    locationManager.requestLocationUpdates(
                            Constants.LocationProviderConstant,
                            0,
                            100000f,
                            this@GpsFragmentViewModel
                    )
                }
            }

            else -> {
                viewModelScope.launch {

                    _gpsLocationFragmentChannel.send(
                            GpsLocationEvents.RequestPermission(
                                    locationRepository.arrayLocationPermissions
                            )
                    )

                }

            }
        }


    }

    override fun onLocationChanged(location: Location) {
//        Log.e("LOCATION CHANGED", "Does Update needed $doesUpdateNeeded \n\n\n\n\n")

        viewModelScope.launch {

            //got user
            val user = authRepository.getUser()

            try {

                //convert location into User
                val userWithAddress: User = locationRepository.convertLocation(user, location)!!

                _gpsLocationFragmentChannel.send(GpsLocationEvents.LoadingEvent)
                //add user in network and send to fragment and fragment call handelResponseOfLocationUpdate
                _gpsLocationFragmentChannel.send(
                        GpsLocationEvents.ObserveLiveDataUpdateResponse(
                                locationRepository.updateUserWithLocationInNetwork(userWithAddress)
                        )
                )


            } catch (e: Exception) {
                locationManager.removeUpdates(this@GpsFragmentViewModel)
                when (e) {
                    is NullPointerException -> {
                        log("null pointer convert location" + e.toString())

                        val errorString = "Not Finding Your Location, Please Get Manually"
                        _gpsLocationFragmentChannel.send(
                                GpsLocationEvents.ShowInvalidInputMessage(
                                        errorString
                                )
                        )
                    }

                    is IllegalArgumentException -> {
                        log("IllegalArgumentException pointer convert location" + e.toString())

                        val errorString = "Not Finding Your Location, Please Get Manually"
                        _gpsLocationFragmentChannel.send(
                                GpsLocationEvents.ShowInvalidInputMessage(
                                        errorString
                                )
                        )
                    }

                    is IOException -> {
                        _gpsLocationFragmentChannel.send(GpsLocationEvents.InternetProblem)
                    }
                }

            }


        }
    }

    fun handelPermissionResult() = viewModelScope.launch {
        //if both req are accepted
        when {
            locationRepository.bothPermissionResult() -> {
                makeLocationCall()
            }
            !locationManager.isProviderEnabled(LocationProviderConstant) -> {
                viewModelScope.launch {
                    _gpsLocationFragmentChannel.send(GpsLocationEvents.RequestGpsEnable)
                }


            }
            else -> {
                _gpsLocationFragmentChannel.send(GpsLocationEvents.ReReqPermission)
            }
        }
    }

    fun handelResponseOnUpdatedLocation(resorce: Resource<UserResponseModel>?) =
            viewModelScope.launch {
                when (resorce) {
                    is Resource.NoInterException ->
                        _gpsLocationFragmentChannel.send(GpsLocationEvents.InternetProblem)
                    is Resource.Failure -> {
                        showInvalidInputMessage("Error ${resorce.errorCode} - ${resorce.errorBody}")

                    }

                    is Resource.Success -> {
                        withContext(Dispatchers.IO) {
                            //adding user to database and pref manager
                            resorce.value.apply {
                                authRepository.updateUser(user).also {
                                    val location = user.location.getLocation()
                                    preferencesManager.updateUserLoc("${location[0]},${location[1]}")
                                    //send to main Fragment
                                    _gpsLocationFragmentChannel.send(GpsLocationEvents.NavigateToMainFragment)
                                }
                            }

                        }

                    }


//                    else -> Log.wtf("LoginFragment", "Going to else")
                }
            }

    private val _gpsLocationFragmentChannel = Channel<GpsLocationEvents>()
    val gpsEvent = _gpsLocationFragmentChannel.receiveAsFlow()


    private fun log(mgs: String) {
        Log.e(TAG, mgs)
    }

    sealed class GpsLocationEvents {
        data class ShowInvalidInputMessage(val msg: String) : GpsLocationEvents()
        object LoadingEvent : GpsLocationEvents()
        object NavigateToMainFragment : GpsLocationEvents()
        data class RequestPermission(val permissions: Array<String>) : GpsLocationEvents()
        data class ObserveLiveDataUpdateResponse(val response: Resource<UserResponseModel>) :
                GpsLocationEvents()

        object ReReqPermission : GpsLocationEvents()
        object InternetProblem : GpsLocationEvents()
        object RequestGpsEnable : GpsLocationEvents()

    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        _gpsLocationFragmentChannel.send(GpsLocationEvents.ShowInvalidInputMessage(text))
    }

    override fun onProviderDisabled(provider: String) {
        when {
            !locationManager.isProviderEnabled(LocationProviderConstant) -> {
                viewModelScope.launch {
                    _gpsLocationFragmentChannel.send(GpsLocationEvents.RequestGpsEnable)
                }
            }
        }
    }

    override fun onProviderEnabled(provider: String) {
        super.onProviderEnabled(provider)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        when {
            !locationManager.isProviderEnabled(LocationProviderConstant) -> {
                viewModelScope.launch {
                    _gpsLocationFragmentChannel.send(GpsLocationEvents.RequestGpsEnable)
                }
            }
        }
    }


}
/*
* GpsFragment -> MakeLocationCall
* if(have permission) -> make call
*     get location in on location change callback and passing that location in fragment
*           if(success)
*               -> get user -> convert latLong -> and update user in network and room and datastore
*           else -> show error
* else -> req permission
* */