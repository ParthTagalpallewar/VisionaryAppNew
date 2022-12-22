package com.reselling.visionary.data.network.apis

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserOperationsApi {

    @FormUrlEncoded
    @POST("updatePassword")
    suspend fun  updatePassword(
        @Field("id") id: String,
        @Field("newPassword") newPass: String
    ) : Response<UserResponseModel>

  @FormUrlEncoded
    @POST("forgotPasswordManual")
    suspend fun  forgotPasswordManual(
        @Field("phone") phone: String,
        @Field("email") email: String
    ) : Response<UserResponseModel>


}