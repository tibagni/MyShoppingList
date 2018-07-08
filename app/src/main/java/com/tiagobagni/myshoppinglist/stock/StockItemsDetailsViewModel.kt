package com.tiagobagni.myshoppinglist.stock

import android.arch.lifecycle.ViewModel
import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListRepository

class StockItemsDetailsViewModel(private val archivedItemsRepository: ArchivedShoppingListRepository) :
    ViewModel() {

    fun getArchivedItemsOf(stockItemId: Int) =
        archivedItemsRepository.getArchivedItemsOf(stockItemId)
}