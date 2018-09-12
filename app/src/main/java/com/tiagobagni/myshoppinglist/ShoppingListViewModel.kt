package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.tiagobagni.myshoppinglist.archive.ArchivedListsManager
import com.tiagobagni.myshoppinglist.settings.SettingsRepository

class ShoppingListViewModel(
    private val repository: ShoppingListRepository,
    private val settingsRepository: SettingsRepository,
    private val archivedListsManager: ArchivedListsManager,
    private val listName: String
) : ViewModel() {
    private val maxArchivedLists = settingsRepository.getMaxArchivedList()
    private val showArchiveOptionMediator = MediatorLiveData<Boolean>()

    val shoppingList = repository.getAllItems(listName)
    val checkedItems = repository.getCheckedItems(listName)
    val showArchiveOption = showArchiveOptionMediator as LiveData<Boolean>

    init {
        showArchiveOptionMediator.addSource(shoppingList) {
            showArchiveOptionMediator.postValue(shouldShowArchiveOption(it))
        }

        showArchiveOptionMediator.addSource(maxArchivedLists) {
            showArchiveOptionMediator.postValue(shouldShowArchiveOption(shoppingList.value))
        }
    }

    private fun shouldShowArchiveOption(shoppingList: List<ShoppingListItem>?): Boolean {
        val isArchiveEnabled = settingsRepository.getMaxArchivedListsSync() > 0
        val isListCompleted = shoppingList != null && shoppingList.isNotEmpty()
                && shoppingList.map { it.checked }.reduce { acc, b -> acc && b }

        return isArchiveEnabled && isListCompleted
    }

    fun addItems(newItems: List<ShoppingListItem>) {
        repository.addItems(newItems)
    }

    fun archiveList() {
        val allItems = shoppingList.value
        allItems?.let {
            archivedListsManager.archive(it)
        }

        clear()
    }

    fun updateItem(item: ShoppingListItem, f: ShoppingListItemUpdater.() -> Unit) {
        val updater = ShoppingListItemUpdater(item)
        updater.f()
        repository.updateItem(updater.item)
    }

    fun clear() {
        repository.deleteShoppingList(ShoppingList(listName))
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