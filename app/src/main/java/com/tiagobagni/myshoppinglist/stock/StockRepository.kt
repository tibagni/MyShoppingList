package com.tiagobagni.myshoppinglist.stock

import com.tiagobagni.myshoppinglist.MyShoppingListDatabase
import org.jetbrains.anko.doAsync

class StockRepository(database: MyShoppingListDatabase) {
    private val stockDao = database.stockDao()

    fun getAllStockItems() = stockDao.getAll()

    fun getAllStockItemsByName(name: String) = stockDao.get("%$name%")

    fun addStockItem(stockItem: StockItem) {
        doAsync {
            stockDao.insert(stockItem)
        }
    }

    fun deleteStockItems(sotckItems: List<StockItem>) {
        doAsync {
            stockDao.delete(sotckItems)
        }
    }
}