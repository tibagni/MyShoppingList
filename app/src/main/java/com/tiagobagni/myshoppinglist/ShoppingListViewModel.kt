package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.ViewModel

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    val shoppingList = repository.getShoppingList()

    val checkedItems = repository.getCheckedItems()

    fun addItems(newItems: List<ShoppingListItem>) {
        repository.addShoppingListItems(newItems)
    }

    fun updateItem(item: ShoppingListItem, f: ShoppingListItemUpdater.() -> Unit) {
        val updater = ShoppingListItemUpdater(item)
        updater.f()
        repository.updateItem(updater.item)
    }

    fun clear() {
        repository.deleteAll()
    }

    class ShoppingListItemUpdater(originalItem: ShoppingListItem) {
        var item = originalItem
            private set

        fun toggleItemChecked() {
            item = item.copy(checked = !item.checked)
        }

        fun setComment(comment: String) {
            item = item.copy(comment = comment)
        }

        fun setPricePaid(pricePaid: Double) {
            item = item.copy(pricePaid = pricePaid)
        }
    }
}