package com.tiagobagni.myshoppinglist

import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListItem
import org.jetbrains.anko.doAsync

class ShoppingListRepository(database: MyShoppingListDatabase) {
    private val shoppingListDao = database.shoppingListDao()
    private val archivedShoppingListDao = database.archivedShoppingListDao()

    fun getShoppingList() = shoppingListDao.getAllOrdered()

    fun getCheckedItems() = shoppingListDao.getChecked()

    fun addShoppingListItems(items: List<ShoppingListItem>) {
        doAsync {
            shoppingListDao.insert(items)
        }
    }

    fun deleteAll() {
        doAsync {
            shoppingListDao.deleteAll()
        }
    }

    fun updateItem(item: ShoppingListItem) {
        doAsync {
            shoppingListDao.updateItem(item)
        }
    }

    fun archive(items: List<ShoppingListItem>) {
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