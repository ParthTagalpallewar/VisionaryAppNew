package com.reselling.visionary.data.network.apis

import com.reselling.visionary.data.models.manualLocation.ManualLocationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ManualLocationApi {

    @GET("{pin}")
    suspend fun getAllCitiesByPin(
            @Path("pin")pin: String
    ) : Response<ArrayList<ManualLocationResponse>>

}