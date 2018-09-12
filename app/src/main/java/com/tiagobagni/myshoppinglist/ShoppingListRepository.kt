package com.tiagobagni.myshoppinglist

import org.jetbrains.anko.doAsync

class ShoppingListRepository(database: MyShoppingListDatabase) {
    private val shoppingListItemDao = database.shoppingListItemDao()
    private val shoppingListDao = database.shoppingListDao()

    fun createNewShoppingListSync(shoppingList: ShoppingList) = shoppingListDao.insert(shoppingList)

    fun createNewShoppingList(shoppingList: ShoppingList) {
        doAsync {
            shoppingListDao.insert(shoppingList)
        }
    }

    fun deleteShoppingList(shoppingList: ShoppingList) {
        doAsync {
            shoppingListDao.delete(shoppingList)
        }
    }

    fun getAllShoppingLists() = shoppingListDao.getAllListsOrdered()

    fun getAllItems(listName: String) = shoppingListItemDao.getAllItemsOrdered(listName)

    fun getCheckedItems(listName: String) = shoppingListItemDao.getCheckedItems(listName)

    fun addItems(items: List<ShoppingListItem>) {
        doAsync {
            shoppingListItemDao.insertItem(items)
        }
    }

    fun updateItem(item: ShoppingListItem) {
        doAsync {
            shoppingListItemDao.updateItem(item)
        }
    }
}