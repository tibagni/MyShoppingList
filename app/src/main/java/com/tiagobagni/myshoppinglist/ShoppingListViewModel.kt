package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel

class ShoppingListViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    val shoppingList = repository.getShoppingList()
    val checkedItems = repository.getCheckedItems()

    val listCompletedStatus: LiveData<Boolean> = Transformations.map(shoppingList) {
        it.isNotEmpty() && it.map { it.checked }.reduce { acc, b -> acc && b }
    }

    fun addItems(newItems: List<ShoppingListItem>) {
        repository.addShoppingListItems(newItems)
    }

    fun archiveList() {
        val allItems = shoppingList.value
        allItems?.let {
            repository.archive(it)
        }

        clear()
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