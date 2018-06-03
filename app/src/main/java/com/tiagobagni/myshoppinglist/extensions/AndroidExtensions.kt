package com.tiagobagni.myshoppinglist.extensions

import android.app.Activity
import android.support.v4.app.Fragment
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