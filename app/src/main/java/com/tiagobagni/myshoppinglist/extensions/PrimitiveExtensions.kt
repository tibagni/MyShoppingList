package com.tiagobagni.myshoppinglist.extensions

import java.text.NumberFormat

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