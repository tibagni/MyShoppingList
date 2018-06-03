package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.ViewModel

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    val shoppingList = repository.getShoppingList()

    fun addItems(newItems: List<ShoppingListItem>) {
        repository.addShoppingListItems(newItems)
    }

    fun toggleItemChecked(item: ShoppingListItem) {
        val newChecked = !item.checked
        repository.setItemChecked(item.id!!, newChecked)
    }

    fun clear() {
        repository.deleteAll()
    }
}