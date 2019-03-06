package com.tiagobagni.myshoppinglist.settings

import androidx.lifecycle.LiveData
import android.content.SharedPreferences

class SettingsLiveData(private val sharedPreferences: SharedPreferences): LiveData<String>(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onActive() {

    }

    override fun onInactive() {

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (this.sharedPreferences == sharedPreferences) {
            postValue(key)
        }
    }
}