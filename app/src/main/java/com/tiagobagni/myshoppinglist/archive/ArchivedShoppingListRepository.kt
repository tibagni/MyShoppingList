package com.tiagobagni.myshoppinglist.archive

import com.tiagobagni.myshoppinglist.MyShoppingListDatabase
import com.tiagobagni.myshoppinglist.ShoppingListItem
import org.jetbrains.anko.doAsync

class ArchivedShoppingListRepository(database: MyShoppingListDatabase) {
    private val archivedShoppingListDao = database.archivedShoppingListDao()

    fun getArchivedTimestamps() = archivedShoppingListDao.getArchivedTimestamps()
    fun getArchivedTimestampsSync() = archivedShoppingListDao.getArchivedTimestampsSync()

    fun getArchivedItemsFrom(timestamp: Long) =
        archivedShoppingListDao.getArchivedItemsFrom(timestamp)

    fun getArchivedItemsOf(stockItemId: Int) =
        archivedShoppingListDao.getArchivedItemsOf(stockItemId)

    fun deleteItemsFrom(timestamp: Long) {
        doAsync {
            archivedShoppingListDao.deleteItemsFrom(timestamp)
        }
    }

    fun addItems(items: List<ShoppingListItem>) {
        doAsync {
            archivedShoppingListDao.insert(items.map {
                ArchivedShoppingListItem(
                    it.stockItemId,
                    it.name,
                    it.icon,
                    it.comment,
                    it.pricePaid,
                    System.currentTimeMillis()
                )
            })
        }
    }
}