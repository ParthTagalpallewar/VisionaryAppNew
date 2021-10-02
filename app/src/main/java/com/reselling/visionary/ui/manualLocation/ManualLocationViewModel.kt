package com.reselling.visionary.ui.manualLocation


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.models.manualLocation.ManualLocation
import com.reselling.visionary.data.models.userModel.User
import com.reselling.visionary.data.models.userModel.UserResponseModel
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.repository.AuthRepository
import com.reselling.visionary.data.repository.LocationRepository
import com.reselling.visionary.data.repository.ManualLocationRepository
import com.reselling.visionary.utils.getLocation
import com.reselling.visionary.utils.internetExceptionString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ManualLocationViewModel @Inject constructor(
        private val state: SavedStateHandle,
        private val manualLocRepo: ManualLocationRepository,
        private val authRepository: AuthRepository,
        private val locationRepository: LocationRepository,
        private val preferencesManager: PreferencesManager

) : ViewModel() {


    var pinCode = state.get<String>("pincode") ?: ""
        set(value) {
            field = value
            state.set("phoneNumber", value)
        }

    fun getCitiesByPincode() {
        when {
            pinCode.isBlank() -> {
                showInvalidInputMessage("Please Enter PinCode First")
            }
            pinCode.length != 6 -> {
                showInvalidInputMessage("Please Enter Valid PinCode")
            }
            else -> {
                viewModelScope.launch {

                    _manualLocationChannel.send(ManualLocationEvents.LoadingEvent)
                    _manualLocationChannel.send(ManualLocationEvents.ObserveManualCityResponse(manualLocRepo.getLocationUsingPinCode(pinCode)))

                }

            }
        }
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
//        Log.wtf("LoginFragment", text)
        _manualLocationChannel.send(ManualLocationEvents.ShowInvalidInputMessage(text))
    }

    fun handleCitesResponse(citiesResponse: Resource<java.util.ArrayList<ManualLocation>>) = viewModelScope.launch {
        when (citiesResponse) {
            is Resource.NoInterException ->
                showInvalidInputMessage(internetExceptionString)

            is Resource.Failure -> {
                showInvalidInputMessage("Error ${citiesResponse.errorCode} - ${citiesResponse.errorBody}")
            }

            is Resource.Success -> {
                withContext(Dispatchers.IO) {
                    _manualLocationChannel.send(ManualLocationEvents.ShowCitiesInRecyclerView(citiesResponse.value))
                }
            }

//            else -> Log.wtf("LoginFragment", "Going to else")
        }
    }

    fun handelRecyclerCityItemClick(location: ManualLocation) {


        viewModelScope.launch {

            _manualLocationChannel.send(ManualLocationEvents.LoadingEvent)

            val user = authRepository.getUser()

            try {
                val userWithAddress: User = locationRepository.convertLocation(user, location)!!

                //add user in nerwork and then add user in database
                _manualLocationChannel.send(ManualLocationEvents.ObserveUpdateLocationResponse(locationRepository.updateUserWithLocationInNetwork(userWithAddress)))


            } catch (e: Exception) {

                when (e) {
                    is IOException -> {
                        _manualLocationChannel.send(ManualLocationEvents.InternetProblem)
                    }

                    else -> {
                        val errorString = "Not Finding Your Location, Please Get Using Gps"
                        showInvalidInputMessage(errorString)
                    }
                }
            }


        }

    }

    fun handelResponseOnUpdatedLocation(resorce: Resource<UserResponseModel>?) = viewModelScope.launch {
        when (resorce) {
            is Resource.NoInterException ->
                _manualLocationChannel.send(ManualLocationEvents.InternetProblem)
            is Resource.Failure -> {
                showInvalidInputMessage("Error ${resorce.errorCode} - ${resorce.errorBody}")

            }

            is Resource.Success -> {
                withContext(Dispatchers.IO) {
                    //adding user to database
                    resorce.value.apply {
                        //room db
                        authRepository.updateUser(user).also {
                            val location = user.location.getLocation()
                            //pref manager
                            preferencesManager.updateUserLoc("${location[0]},${location[1]}")
                            _manualLocationChannel.send(ManualLocationEvents.NavigateToMainFragment)
                        }
                    }

                }

            }


//            else -> Log.wtf("LoginFragment", "Going to else")
        }
    }


    private val _manualLocationChannel = Channel<ManualLocationEvents>()
    val manualLocationEvents = _manualLocationChannel.receiveAsFlow()

    sealed class ManualLocationEvents {
        data class ShowInvalidInputMessage(val msg: String) : ManualLocationEvents()
        data class ObserveManualCityResponse(val cityList: Resource<ArrayList<ManualLocation>>) : ManualLocationEvents()
        data class ObserveUpdateLocationResponse(val updateResponse: Resource<UserResponseModel>) : ManualLocationEvents()
        object InternetProblem : ManualLocationEvents()
        object LoadingEvent : ManualLocationEvents()
        object NavigateToMainFragment : ManualLocationEvents()
        data class ShowCitiesInRecyclerView(val cities: ArrayList<ManualLocation>) : ManualLocationEvents()

    }

}