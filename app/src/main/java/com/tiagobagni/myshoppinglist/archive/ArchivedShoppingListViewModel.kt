package com.tiagobagni.myshoppinglist.archive

import androidx.lifecycle.ViewModel

class ArchivedShoppingListViewModel(private val archivedRepository: ArchivedShoppingListRepository) :
    ViewModel() {

    fun getArchivedItemsFrom(timestamp: Long) = archivedRepository.getArchivedItemsFrom(timestamp)

    fun deleteItemsFrom(timestamp: Long) {
        archivedRepository.deleteItemsFrom(timestamp)
    }
}