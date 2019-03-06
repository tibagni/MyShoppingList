package com.tiagobagni.myshoppinglist.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import android.content.SharedPreferences

class SettingsRepository(
    private val sharedPrefs: SharedPreferences,
    private val settingsKeys: SettingsKeys
) {
    companion object {
        const val DEFAULT_MAX_ARCHIVED_LISTS = "5"
    }

    private val settingsLiveData = SettingsLiveData(sharedPrefs)
    private val maxArchivedListsLiveData = MediatorLiveData<Int>()

    init {
        maxArchivedListsLiveData.addSource(settingsLiveData) {
            if (it == settingsKeys.maxArchivedLists) {
                maxArchivedListsLiveData.postValue(getMaxArchivedListsSync())
            }
        }
    }

    fun getMaxArchivedListsSync(): Int {
        val historySize = sharedPrefs.getString(
            settingsKeys.maxArchivedLists,
            DEFAULT_MAX_ARCHIVED_LISTS
        )
        return Integer.valueOf(historySize)
    }

    fun getMaxArchivedList() = maxArchivedListsLiveData as LiveData<Int>
}