package com.tiagobagni.myshoppinglist.archive

import com.tiagobagni.myshoppinglist.MyShoppingListDatabase
import org.jetbrains.anko.doAsync

class ArchivedShoppingListRepository(database: MyShoppingListDatabase) {
    private val archivedShoppingListDao = database.archivedShoppingListDao()

    fun getArchivedTimestamps() = archivedShoppingListDao.getArchivedTimestamps()

    fun getArchivedItemsFrom(timestamp: Long) =
        archivedShoppingListDao.getArchivedItemsFrom(timestamp)

    fun getArchivedItemsOf(stockItemId: Int) =
        archivedShoppingListDao.getArchivedItemsOf(stockItemId)

    fun deleteItemsFrom(timestamp: Long) {
        doAsync {
            archivedShoppingListDao.deleteItemsFrom(timestamp)
        }
    }
}