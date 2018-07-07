package com.tiagobagni.myshoppinglist.archive

import com.tiagobagni.myshoppinglist.MyShoppingListDatabase

class ArchivedShoppingListRepository(database: MyShoppingListDatabase) {
    private val archivedShoppingListDao = database.archivedShoppingListDao()

    fun getArchivedTimestamps() = archivedShoppingListDao.getArchivedTimestamps()

    fun getArchivedItemsFrom(timestamp: Long) =
        archivedShoppingListDao.getArchivedItemsFrom(timestamp)
}