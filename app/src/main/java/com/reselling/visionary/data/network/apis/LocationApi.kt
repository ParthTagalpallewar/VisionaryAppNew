package com.reselling.visionary.data.network.apis

import com.reselling.visionary.data.models.userModel.UserResponseModel
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LocationApi {

    //update User
    @FormUrlEncoded
    @POST("updateUserLocation")
    suspend fun updateUserLocation(
            @Field("id") id: String,
            @Field("country") country: String,
            @Field("state") state: String,
            @Field("city") city: String,
            @Field("location") location: String,
            @Field("address") address: String,
            @Field("district") district: String

    ) : Response<UserResponseModel>
}