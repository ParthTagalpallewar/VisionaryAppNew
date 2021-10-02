package com.reselling.visionary.data.network.networkResponseType


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

//private const val TAG = "MySafeApiRequest"

abstract class MySafeApiRequest {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): Resource<T> {


        return withContext(Dispatchers.IO) {
            try {
                val apiRequest = call.invoke()

                if ((apiRequest.isSuccessful) and (apiRequest.code() == 200)) {
                    return@withContext Resource.Success(apiRequest.body()!!)

                } else {


                    var errorMessage = ""

                    try {
                        val error = apiRequest.errorBody()?.string()
                        val jsonObject = JSONObject(error!!)
                        val errorStr = jsonObject.get("error")

                        errorMessage = errorStr.toString()
                    } catch (e: JSONException) {
                        //Log.e("xyz$TAG", e.toString())
                    }


/*
                    Log.e(
                        TAG,
                        "\n Failure \n" +
                                "\n code " + apiRequest.code()
                                + "\n Body ${apiRequest.body()}"
                                + "\n Message Error $errorMessage"
                                + "\nErrorBody ${apiRequest.errorBody().toString()}"
                                + "\n Headers ${apiRequest.headers()}"
                                + "\n raw ${apiRequest.raw()}"
                                + "\n Message ${apiRequest.message()}"
                                + "\n Body $apiRequest \n\n "
                    )*/

                    return@withContext Resource.Failure(apiRequest.code(), errorMessage)
                }

            } catch (throwable: Throwable) {

                when (throwable) {
                    is HttpException ->
                        return@withContext Resource.Failure(
                            throwable.code(), throwable.localizedMessage
                                ?: "Sever Error Occured"
                        )

                    is UnknownHostException ->
                        return@withContext Resource.NoInterException

                    is SocketTimeoutException ->
                        return@withContext Resource.NoInterException

                    else -> {
                       // Log.e(TAG, throwable.localizedMessage?.toString() ?: "", throwable)
                        return@withContext Resource.Failure(
                            405, throwable.localizedMessage
                                ?: "Internal Error Occured"
                        )
                    }

                }

            }
        }

    }

}

