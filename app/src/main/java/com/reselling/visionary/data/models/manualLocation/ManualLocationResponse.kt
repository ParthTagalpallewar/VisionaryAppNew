package com.reselling.visionary.data.models.manualLocation


data class ManualLocationResponse (

        val Message:String,
        val Status:String,
        val PostOffice:ArrayList<ManualLocation>
)