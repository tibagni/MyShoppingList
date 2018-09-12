package com.tiagobagni.myshoppinglist.archive

import com.tiagobagni.myshoppinglist.ShoppingListItem
import com.tiagobagni.myshoppinglist.analytics.EventLogger
import com.tiagobagni.myshoppinglist.settings.SettingsRepository
import org.jetbrains.anko.doAsync

class ArchivedListsManager(
    private val archivedListsRepository: ArchivedShoppingListRepository,
    private val settingsRepository: SettingsRepository
) {
    fun archive(items: List<ShoppingListItem>) {
        EventLogger.logArchiveList()
        archivedListsRepository.addItems(items)

        // Make sure we don't exceed the max allowed number of archived lists
        removeOlderArchivedLists()
    }

    fun removeOlderArchivedLists() {
        doAsync {
            val maxArchivedLists = settingsRepository.getMaxArchivedListsSync()
            val allArchivedTimestamps = archivedListsRepository.getArchivedListsSync()
                .mapNotNull { it.archiveTimestamp }

            allArchivedTimestamps?.let {
                if (it.size > maxArchivedLists) {
                    val toRemove = it.sorted().take(it.size - maxArchivedLists)
                    toRemove.forEach { archivedListsRepository.deleteItemsFrom(it) }
                }
            }
        }
    }
}