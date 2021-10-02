package com.reselling.visionary.utils

import android.location.Address
import com.reselling.visionary.data.models.userModel.User


operator fun Address.plus(user: User): User {
    val unknown: String = "Unknown"
    this.also {


        return user.copy(
            country = it.countryName ?: unknown,
            city = it.locality ?: unknown,
            state = it.adminArea ?: unknown,
            location = "${it.latitude}, ${it.longitude}",
            address = "${it.featureName} , ${it.subLocality} ",
            district = it.subAdminArea
        )
    }
}

operator fun User.plus(address: Address): User {
    val unknown: String = "Unknown"
    val homeAddress = if ((address.subAdminArea == null) or (address.subAdminArea == "null")) {
        "${address.featureName} , ${address.subLocality} "
    } else {
        "${address.featureName} , ${address.subAdminArea} "
    }
    address.also {
        return this.copy(
            country = it.countryName ?: unknown,
            city = it.locality ?: unknown,
            state = it.adminArea ?: unknown,
            location = "${it.latitude}, ${it.longitude}",
            address = "$homeAddress",
            district = it.subAdminArea
        )
    }
}

fun String.getLocation() = this.split(",")
