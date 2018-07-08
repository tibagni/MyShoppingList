package com.tiagobagni.selectionmode

import android.os.Bundle
import com.tiagobagni.myshoppinglist.extensions.splitRanges

class SelectionModeHelper(private val callback: ChoiceModeHelperCallback) {
    val selectedItems = mutableSetOf<Int>()
    internal var onSelectionCleared: ((Set<Pair<Int, Int>>) -> Unit)? = null

    companion object {
        private const val STATE_ITEMS = "items"
    }

    val selectedCount: Int
        get() = selectedItems.size

    fun isInSelectionMode() = selectedItems.isNotEmpty()
    fun isItemSelected(item: Int) = selectedItems.contains(item)
    fun toggleItemSelection(item: Int): Boolean {
        val isSelected = if (selectedItems.contains(item)) {
            removeItem(item)
            false
        } else {
            addItem(item)
            true
        }

        callback.onSelectionChanged()
        return isSelected
    }

    private fun addItem(item: Int) {
        val wasEmpty = selectedItems.isEmpty()
        selectedItems += item

        if (wasEmpty) enterSelectionMode()
    }

    private fun removeItem(item: Int) {
        selectedItems.remove(item)

        if (selectedItems.isEmpty()) exitSelectionMode()
    }

    private fun enterSelectionMode() {
        callback.onEnterSelectionMode()
    }

    private fun exitSelectionMode() {
        selectedItems.clear()
        callback.onExitSelectionMode()
    }

    fun cancelSelectionMode() {
        val clearedItems = selectedItems.toSet()
        exitSelectionMode()

        if (clearedItems.isNotEmpty()) {
            val ranges = clearedItems.toSortedSet().splitRanges()
            onSelectionCleared?.invoke(ranges)
        }
    }

    fun saveState(bundle: Bundle) {
        bundle.putIntArray(STATE_ITEMS, selectedItems.toIntArray())
    }

    fun restoreFromState(bundle: Bundle) {
        val array = bundle.getIntArray(STATE_ITEMS)
        array?.forEach { selectedItems.add(it) }
    }

    fun markAsSelected(allItems: List<Int>) {
        val wasEmpty = selectedItems.isEmpty()
        selectedItems += allItems

        if (wasEmpty) enterSelectionMode()
        callback.onSelectionChanged()
    }
}

interface ChoiceModeHelperCallback {
    fun onEnterSelectionMode()
    fun onExitSelectionMode()
    fun onSelectionChanged()
}