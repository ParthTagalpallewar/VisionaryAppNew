package com.reselling.visionary.utils

import android.location.LocationManager

class Constants {

    companion object {
        var ENABLE_PHONE_VERIFICATION: Boolean = true
        var LocationProviderConstant = LocationManager.GPS_PROVIDER

        const val VISIONARY_BASE_URL = "https://digitdeveloper.online/visionary/welcome/"
        const val BOOK_IMAGE_URL = "https://digitdeveloper.online/visionary/uploads/"
        const val MANUAL_LOCATION_API_BASE_URL = "https://api.postalpincode.in/pincode/"

    }


}

fun saveConstants(boolean: Boolean, provider: String) {
    Constants.ENABLE_PHONE_VERIFICATION = boolean

    Constants.LocationProviderConstant = provider
}

//val VISIONARY_BASE_URL = "https://digitdeveloper.online/visionary/welcome/"

//val VISIONARY_BASE_URL = "http://192.168.43.146/book_selling_app_api/Welcome/"Aval
