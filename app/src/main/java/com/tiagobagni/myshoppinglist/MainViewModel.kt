package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.ViewModel
import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListRepository

class MainViewModel(repository: ArchivedShoppingListRepository) : ViewModel() {

    val archivedListsTimestamps = repository.getArchivedTimestamps()
}