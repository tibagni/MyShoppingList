package com.tiagobagni.myshoppinglist

import android.arch.lifecycle.ViewModel
import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListRepository

class MainViewModel(archivedShoppingListRepository: ArchivedShoppingListRepository,
                    shoppingListRepository: ShoppingListRepository) : ViewModel() {

    val archivedListsTimestamps = archivedShoppingListRepository.getArchivedLists()
    val activeLists = shoppingListRepository.getAllShoppingLists()
}