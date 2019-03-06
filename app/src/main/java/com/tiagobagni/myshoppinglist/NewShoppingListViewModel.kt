package com.tiagobagni.myshoppinglist

import androidx.lifecycle.ViewModel

class NewShoppingListViewModel(private val repository: ShoppingListRepository): ViewModel() {

    val existingShoppingLists = repository.getAllShoppingLists()

    fun createShoppingList(listName: String) {
        repository.createNewShoppingList(ShoppingList(listName))
    }
}