package com.tiagobagni.myshoppinglist.extensions

import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

operator fun Int.compareTo(otherInt: Int?): Int {
    if (otherInt == null) {
        return -1
    }

    return compareTo(otherInt)
}

operator fun Int.plus(otherInt: Int?): Int {
    if (otherInt == null) {
        return this
    }

    return plus(otherInt)
}

fun String.toDoubleOrZero(): Double {
    return toDoubleOrNull() ?: 0.0
}

fun Double.toCurrencyFormat(): String {
    val currencyFormatter = NumberFormat.getCurrencyInstance()
    return currencyFormatter.format(this)
}

fun Long.toFormattedDate() = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(this))!!
