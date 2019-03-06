package com.tiagobagni.myshoppinglist.settings

import androidx.lifecycle.ViewModel
import com.tiagobagni.myshoppinglist.archive.ArchivedListsManager

class SettingsViewModel(private val archivedListsManager: ArchivedListsManager): ViewModel() {
    fun ensureMaxArchivedLists() {
        archivedListsManager.removeOlderArchivedLists()
    }
}