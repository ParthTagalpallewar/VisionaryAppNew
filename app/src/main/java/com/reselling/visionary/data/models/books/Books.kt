package com.reselling.visionary.data.models.books

import android.os.Parcelable
import com.reselling.visionary.utils.Constants.Companion.BOOK_IMAGE_URL
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Books(
        val id:String,
        val bookName:String,
        val bookDesc:String,
        val bookSelling:String,
        val bookOriginal:String = "00",
        val bookCatagory:String,
        val userPhone:String,
        val userId:String,
        val bookImage: String,
        val city:String,
        val location:String,
        val address:String,
        val district:String,
        var selected:Boolean = false
):Parcelable{
    val imageUrl get() = BOOK_IMAGE_URL+bookImage
    val originalMark get() = "₹$bookOriginal"
    val sellingMark get() = "₹$bookSelling"
}
