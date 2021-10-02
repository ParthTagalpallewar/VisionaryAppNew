package com.reselling.visionary.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.reselling.visionary.R
import com.reselling.visionary.ui.home.homeBookDetails.MY_HOME_BOOK_DETAILS
import com.reselling.visionary.ui.home.homeBookDetails.MY_HOME_SELLER_DETAILS
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun String.getUniqueName():String{

    val calendar = Calendar.getInstance()
    val format = SimpleDateFormat("yyyyMMddHHmmss")
    val time = format.format(calendar.time)
    return "${this}$time.jpg"

}

fun ByteArray.toImageRequestBody(): RequestBody {
    return RequestBody.create(
        "image/jpeg".toMediaTypeOrNull(),
        this
    )
}

fun String.toMultipartReq() = this.toRequestBody("text/plain".toMediaTypeOrNull())




fun Fragment.getTabTitle(position: Int): String? {
    return when (position) {
        MY_HOME_BOOK_DETAILS -> "BOOK DETAILS"
        MY_HOME_SELLER_DETAILS -> "SELLER DETAILS"
        else -> null
    }
}

fun Context.sendUserToWhatsApp(phoneNumber: String) {
    val stringW = "https://api.whatsapp.com/send?phone=+${phoneNumber}&text=  Hello, \n"
    val i = Intent.parseUri(stringW, Intent.URI_ALLOW_UNSAFE)
    startActivity(i)
}

fun Context.sendUserToMail(emailId:String) {

    val TO = arrayOf(emailId)
    val CC = arrayOf("")

    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.data = Uri.parse("mailto:")
    emailIntent.type = "text/plain"
    emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
    emailIntent.putExtra(Intent.EXTRA_CC, CC)

    try {
        startActivity(Intent.createChooser(emailIntent, "Send mail..."))


    } catch (ex: ActivityNotFoundException) {
        Log.i("Email Excaption", ex.localizedMessage!!)

    }
}

fun Context.sendUserToContact(phoneNumber: String) {
    Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
        startActivity(this)
    }
}