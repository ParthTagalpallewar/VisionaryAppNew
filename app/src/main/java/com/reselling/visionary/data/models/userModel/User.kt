package com.reselling.visionary.data.models.userModel

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


const val UserDefaultDatabaseId: Int = 0



@Entity(tableName = "user-table")
@Parcelize
data class User(
        val id: String,
        val name: String,
        val email: String,
        val phone: String,
        val password: String,
        val country: String?,
        val state: String?,
        val city: String?,
        val location: String?,
        val address: String?,
        val district: String?,

        @PrimaryKey(autoGenerate = false)
        val Uid: Int = UserDefaultDatabaseId
): Parcelable{
        val getUserAddress get() = address ?: "Location Not Selected"
}