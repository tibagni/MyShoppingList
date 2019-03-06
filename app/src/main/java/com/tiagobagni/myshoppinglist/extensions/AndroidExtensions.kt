package com.tiagobagni.myshoppinglist.extensions

import android.app.Activity
import android.content.Context
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.View
import org.jetbrains.anko.internals.AnkoInternals
import android.view.inputmethod.InputMethodManager


inline fun <reified T : Activity> Fragment.startActivityForResult(
    requestCode: Int,
    vararg params: Pair<String, Any>
) {
    val ctx = context
    ctx?.let {
        val intent = AnkoInternals.createIntent(context!!, T::class.java, params)
        startActivityForResult(intent, requestCode)
    }
}

inline fun View.showCustomSnack(message: String, duration: Int, f: View.() -> Unit): Snackbar {
    val snack = Snackbar.make(this, message, duration)
    snack.view.f()
    snack.show()

    return snack
}

inline fun Fragment.getColor(id: Int): Int {
    return ContextCompat.getColor(context!!, id)
}

inline fun View.hideSoftKeyboard() {
    if (this != null) {
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}