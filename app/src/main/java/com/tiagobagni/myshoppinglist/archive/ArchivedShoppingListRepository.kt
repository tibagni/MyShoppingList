package com.tiagobagni.myshoppinglist.archive

import com.tiagobagni.myshoppinglist.MyShoppingListDatabase
import com.tiagobagni.myshoppinglist.ShoppingListItem
import org.jetbrains.anko.doAsync

class ArchivedShoppingListRepository(database: MyShoppingListDatabase) {
    private val archivedShoppingListDao = database.archivedShoppingListDao()

    fun getArchivedLists() = archivedShoppingListDao.getArchivedLists()
    fun getArchivedListsSync() = archivedShoppingListDao.getArchivedListsSync()

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
            val timestamp = System.currentTimeMillis()
            archivedShoppingListDao.insert(items.map {
                ArchivedShoppingListItem(
                    it.listName,
                    it.stockItemId,
                    it.name,
                    it.icon,
                    it.comment,
                    it.pricePaid,
                    timestamp
                )
            })
        }
    }
}