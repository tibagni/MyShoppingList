package com.tiagobagni.myshoppinglist

import org.jetbrains.anko.doAsync

class ShoppingListRepository(database: MyShoppingListDatabase) {
    private val shoppingListDao = database.shoppingListDao()

    fun getShoppingList() = shoppingListDao.getAllOrdered()

    fun getCheckedItems() = shoppingListDao.getChecked()

    fun addShoppingListItems(items: List<ShoppingListItem>){
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

}