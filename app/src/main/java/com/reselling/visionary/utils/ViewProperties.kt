package com.reselling.visionary.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pd.chocobar.ChocoBar
import com.reselling.visionary.R

fun BottomNavigationView.visiblity(boolean: Boolean) {
    visibility = if (boolean)
        View.VISIBLE
    else
        View.GONE
}

inline fun EditText.search(crossinline work: (query: String) -> Unit) {

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            work.invoke(s.toString())
        }

    })
}

@SuppressLint("ResourceAsColor")
inline fun SearchView.searchQuery(crossinline work: (query: String) -> Unit) {


    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            if (query != null) {
                work.invoke(query)
            }
            this@searchQuery.clearFocus()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }

    })

}


fun Context.toast(msg: String) {

    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

}

fun Context.setUpLocationAlertDialogue() {
    AlertDialog.Builder(this).apply {
        setTitle("GPS setting")
        setMessage("Turn on gps")
        setPositiveButton("Turn On") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            Log.e("adfjaljd", "setUpLocationAlertDialogue: intent", )
            context.startActivity(intent)
        }
        setNegativeButton("Cancel") { dialoge, _ ->
            dialoge.cancel()
        }
        show()
    }
}


fun Activity.changeStatusBarColor(color: Int = R.color.color_primary) {
    this.window.statusBarColor = ContextCompat.getColor(this, color)
}


fun View.visible(visible: Boolean = true) {
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}


@SuppressLint("ResourceAsColor")
inline fun View.snackBar(
    msg: String
) {
    try {
        (this as AppCompatActivity).hideKeyboard()
    } catch (e: Exception) {
    }


    val chocoBar = ChocoBar.builder()
    chocoBar.setView(this)
    chocoBar.apply {
        setBackgroundColor(Color.parseColor("#de6e71"))
        setTextSize(16F)
        setTextColor(Color.parseColor("#FFFFFF"))
        setText(msg)
        setView(this@snackBar)
        setMaxLines(4)
        setActionTextColor(Color.parseColor("#FFFFFF"))
        setActionTextSize(18F)
        setActionTextTypefaceStyle(Typeface.BOLD)
        setDuration(ChocoBar.LENGTH_LONG)
    }

    chocoBar.build().show()


}


@SuppressLint("ResourceAsColor")
inline fun View.snackBar(
    msg: String,
    actionName: String,
    crossinline action: ((ChocoBar.Builder) -> Unit)
) {
    try {
        (this as AppCompatActivity).hideKeyboard()
    } catch (e: Exception) {
    }


    val chocoBar = ChocoBar.builder()
    chocoBar.setView(this)
    chocoBar.apply {
        setBackgroundColor(Color.parseColor("#de6e71"))
        setTextSize(16F)
        setTextColor(Color.parseColor("#FFFFFF"))
        setText(msg)
        setView(this@snackBar)
        setMaxLines(4)
        setDuration(3)
        setActionTextColor(Color.parseColor("#FFFFFF"))
        setActionTextSize(18F)
        setActionTextTypefaceStyle(Typeface.BOLD)
        setActionText(actionName)
        setDuration(ChocoBar.LENGTH_INDEFINITE)
        setActionClickListener {
            action.invoke(chocoBar)
        }
    }

    chocoBar.build().show()


}

fun LottieAnimationView.setUpAnimation(): LottieAnimationView {
    this.apply {
        visibility = View.VISIBLE
        playAnimation()
        progress = 0F
        repeatMode = LottieDrawable.RESTART

    }
    return this
}


fun Activity.hideKeyboard() {
    try {
        val imm: InputMethodManager =
            this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = this.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    } catch (e: Exception) {
    }
}


