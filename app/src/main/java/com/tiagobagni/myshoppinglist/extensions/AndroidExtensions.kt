package com.tiagobagni.myshoppinglist.extensions

import android.app.Activity
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import org.jetbrains.anko.internals.AnkoInternals

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

inline fun View.showCustomSnack(message: String, duration: Int, f : View.() -> Unit): Snackbar {
    val snack = Snackbar.make(this, message, duration)
    snack.view.f()
    snack.show()

    return snack
}

inline fun Fragment.getColor(id: Int) : Int {
    return ContextCompat.getColor(context!!, id)
}