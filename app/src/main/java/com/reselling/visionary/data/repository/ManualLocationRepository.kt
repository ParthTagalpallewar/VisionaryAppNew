package com.reselling.visionary.data.repository

import com.reselling.visionary.data.models.manualLocation.ManualLocation
import com.reselling.visionary.data.network.apis.ManualLocationApi
import com.reselling.visionary.data.network.networkResponseType.LocationSafeApiRequest
import com.reselling.visionary.data.network.networkResponseType.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ManualLocationRepository @Inject constructor(
       private  val locationApi: ManualLocationApi
):LocationSafeApiRequest() {
    suspend fun getLocationUsingPinCode(pinCode:String) : Resource<ArrayList<ManualLocation>> {
        return withContext(Dispatchers.IO){
            apiRequest {
                locationApi.getAllCitiesByPin(pinCode)
            }
        }
    }


}