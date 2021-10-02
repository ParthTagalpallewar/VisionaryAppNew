package com.reselling.visionary.data.models.dataModels

import android.location.LocationManager
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Constants (
        val id:String ,
        val loc_provider:String,
        val enable_phone_verification:String
): Parcelable{
        val enablePhoneVerification:Boolean get() = enable_phone_verification == "YES"

        val getProvider:String get() =  when(loc_provider){
                "NETWORK" -> LocationManager.NETWORK_PROVIDER

                else -> LocationManager.GPS_PROVIDER
        }
}