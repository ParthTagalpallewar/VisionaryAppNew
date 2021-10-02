package com.reselling.visionary.data.models.category

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Parcelable
import androidx.core.content.ContextCompat
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryModel(
        val id: String,
        val name: String,
        val color: Int
) : Parcelable {

    fun convertColor(context: Context): PorterDuffColorFilter {

        val convertedColor = ContextCompat.getColor(context, color)
        return PorterDuffColorFilter(convertedColor, PorterDuff.Mode.SRC_ATOP)
    }
}