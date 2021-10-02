package com.reselling.visionary.data.network.networkResponseType


import android.util.Log
import com.reselling.visionary.data.models.manualLocation.ManualLocation
import com.reselling.visionary.data.models.manualLocation.ManualLocationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

//private const val TAG = "LocationSafeApiRequest"

abstract class LocationSafeApiRequest {

    suspend fun apiRequest(call: suspend () -> Response<ArrayList<ManualLocationResponse>>): Resource<ArrayList<ManualLocation>> {

        return withContext(Dispatchers.IO) {
            try {
                val apiRequest = call.invoke()

                if ((apiRequest.isSuccessful) and (apiRequest.code() == 200)) {

                    val reqBody: ArrayList<ManualLocationResponse> = apiRequest.body()!!
                    when {
                        reqBody[0].Status == "Success" -> {
                            return@withContext Resource.Success(apiRequest.body()!![0].PostOffice)
                        }
                        else -> {
                            return@withContext Resource.Failure(401, apiRequest.body()!![0].Message)
                        }
                    }
                } else {


                    var errorMessage = ""

                    try {
                        val error = apiRequest.errorBody()?.string()
                        val jsonObject = JSONObject(error!!)
                        val errorStr = jsonObject.get("error")

                        errorMessage = errorStr.toString()
                    } catch (e: JSONException) {
                       // Log.e("xyz$TAG", e.toString())
                    }



                  /*  Log.e(
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
                                ?: "Some Internal Error happen"
                        )

                    is UnknownHostException ->
                        return@withContext Resource.NoInterException

                    is SocketTimeoutException ->
                        return@withContext Resource.NoInterException

                    else -> {
                        //Log.e(TAG, throwable.localizedMessage?.toString() ?: "", throwable)
                        return@withContext Resource.Failure(
                                405, throwable.localizedMessage
                                ?: "Internal Error happen"
                        )
                    }

                }

            }
        }

    }

}

