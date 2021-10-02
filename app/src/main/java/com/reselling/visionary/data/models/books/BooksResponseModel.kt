package com.reselling.visionary.data.models.books

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BooksResponseModel(
        val result: String,
        val books: ArrayList<Books>
):Parcelable