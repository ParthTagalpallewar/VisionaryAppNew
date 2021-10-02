package com.reselling.visionary.data.models.manualLocation

val unknown: String = "Unknown";

data class ManualLocation(

        val Name: String = unknown,
        val District: String = unknown,
        val Block: String = unknown,
        val State: String = unknown,
        val Country: String = unknown

)