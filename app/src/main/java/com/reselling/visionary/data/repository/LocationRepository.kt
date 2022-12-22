package com.reselling.visionary.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.reselling.visionary.data.models.manualLocation.ManualLocation
import com.reselling.visionary.data.models.userModel.User
import com.reselling.visionary.data.network.apis.LocationApi
import com.reselling.visionary.data.network.networkResponseType.MySafeApiRequest
import com.reselling.visionary.utils.plus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class LocationRepository @Inject constructor(
        private val geoCoder: Geocoder,
        @ApplicationContext private val context: Context,
        private val locationApi: LocationApi
) : MySafeApiRequest() {

    val courseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
    val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    val arrayLocationPermissions: Array<String> = arrayOf(courseLocation, fineLocation)
    val arrayCoursePermissions: Array<String> = arrayOf(courseLocation)
    val arrayFinePermissions: Array<String> = arrayOf(fineLocation)



    //return true -> permission is no granted
    fun getResultOfCourseLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
                context,
                courseLocation
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getResultOfFineLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
                context,
                fineLocation
        ) == PackageManager.PERMISSION_GRANTED
    }
    //    return ture if both permission are granted
    fun bothPermissionResult(): Boolean {
        return (getResultOfFineLocationPermission() and getResultOfCourseLocationPermission())
    }

    suspend fun convertLocation(user: User, result: Location): User? {
        try {

            return withContext(Dispatchers.IO) {
                val addresses: List<Address> = geoCoder.getFromLocation(result.latitude, result.longitude, 1)

                if (addresses.isNotEmpty()) {
                    addresses[0] + user
                } else {
                    null
                }
            }


        } catch (e: Exception) {
            throw e
        }
    }

    fun convertLocation(user: User, result: ManualLocation): User? {
        try {

            //getting LatLong form Name Of city and district got form api
            val addresses: List<Address> =
                    geoCoder.getFromLocationName(result.Name + "," + result.District, 1)

            return if (addresses.isNotEmpty()) {
                user + addresses[0]
            } else {
                null
            }


        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateUserWithLocationInNetwork(user: User) = apiRequest {
        val unknown = "Unknown"
        locationApi.updateUserLocation(
                id = user.id,
                country = user.country ?: unknown,
                city = user.city ?: unknown,
                state = user.state ?: unknown,
                location = user.location ?: unknown,
                address = user.address ?: unknown,
                district = user.district ?: unknown
        )


    }


}