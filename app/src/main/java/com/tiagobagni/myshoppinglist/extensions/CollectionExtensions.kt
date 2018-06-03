package com.tiagobagni.myshoppinglist.extensions

fun Set<Int>.splitRanges(): Set<Pair<Int, Int>> {
    val ranges = mutableSetOf<Pair<Int, Int>>()
    var rangeStart = -1
    var currentRange = -1

    for (i in this) {
        if (rangeStart == -1) {
            rangeStart = i
            currentRange = i
        } else {
            if (i == (currentRange + 1)) {
                currentRange = i
            } else {
                ranges.add(rangeStart to currentRange)
                rangeStart = i
                currentRange = i
            }
        }
    }
    ranges.add(rangeStart to currentRange)
    return ranges
}