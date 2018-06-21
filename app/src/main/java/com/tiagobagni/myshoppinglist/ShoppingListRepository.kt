package com.tiagobagni.myshoppinglist

import org.jetbrains.anko.doAsync

class ShoppingListRepository(database: MyShoppingListDatabase) {
    private val shoppingListDao = database.shoppingListDao()

    fun getShoppingList() = shoppingListDao.getAllOrdered()

    fun addShoppingListItems(items: List<ShoppingListItem>){
        doAsync {
            shoppingListDao.insert(items)
        }
    }

    fun setItemChecked(itemId: Int, checked: Boolean) {
        doAsync {
            shoppingListDao.setChecked(itemId, checked)
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