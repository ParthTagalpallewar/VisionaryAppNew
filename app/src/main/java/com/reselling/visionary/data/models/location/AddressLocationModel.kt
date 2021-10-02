package com.reselling.visionary.data.models.location

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddressLocationModel(
        var country: String,
        var state: String,
        var city: String,
        var location: String,
        var address: String,
        var district: String
) : Parcelable