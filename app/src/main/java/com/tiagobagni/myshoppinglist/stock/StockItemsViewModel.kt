package com.tiagobagni.myshoppinglist.stock

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import android.text.TextUtils

class StockItemsViewModel(private val stockRepository: StockRepository) : ViewModel() {
    private val filter = MutableLiveData<String>()
    val stockItemsList = Transformations.switchMap(filter) {
        return@switchMap if (TextUtils.isEmpty(it)) {
            stockRepository.getAllStockItems()
        } else {
            stockRepository.getAllStockItemsByName(it)
        }
    }!!

    init {
        filter.value = ""
    }

    fun createStockItem(item: StockItem) {
        stockRepository.addStockItem(item)
    }

    fun deleteStockItems(items: List<StockItem>) {
        stockRepository.deleteStockItems(items)
    }

    fun search(searchText: String) {
        filter.value = searchText
    }
}