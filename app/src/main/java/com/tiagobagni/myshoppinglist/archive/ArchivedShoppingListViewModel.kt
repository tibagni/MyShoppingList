package com.tiagobagni.myshoppinglist.archive

import android.arch.lifecycle.ViewModel

class ArchivedShoppingListViewModel(private val archivedRepository: ArchivedShoppingListRepository) :
    ViewModel() {

    fun getArchivedItemsFrom(timestamp: Long) = archivedRepository.getArchivedItemsFrom(timestamp)
}