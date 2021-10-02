package com.reselling.visionary.data.network.networkResponseType

sealed class Resource<out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure (val errorCode: Int, val errorBody: String) : Resource<Nothing>()
    object NoInterException:Resource<Nothing>()
    object Loading : Resource<Nothing>()
}