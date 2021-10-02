package com.reselling.visionary.data.network.apis


import com.reselling.visionary.data.models.dataModels.ConstantsResponse
import retrofit2.Response
import retrofit2.http.GET


interface DataApi {


    @GET("getConstants")
    suspend fun getConstants(): Response<ConstantsResponse>
}