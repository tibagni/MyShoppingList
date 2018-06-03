package com.tiagobagni.myshoppinglist.extensions

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