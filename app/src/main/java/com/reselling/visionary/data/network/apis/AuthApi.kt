package com.reselling.visionary.data.network.apis

import com.reselling.visionary.data.models.userModel.User
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

        @FormUrlEncoded
    @POST("loginUser")
    suspend fun login(
            @Field("phone") phone:String,
            @Field("password") password:String,
    ):Response<User>

    //Auth
    @FormUrlEncoded
    @POST("signUpUser")
    suspend fun userSignUp(
            @Field("phone") phone: String,
            @Field("password") password: String,
            @Field("name") name: String,
            @Field("email") email: String
    ) : Response<User>



}