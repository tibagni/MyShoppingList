package com.tiagobagni.myshoppinglist.di

import android.arch.persistence.room.Room
import android.support.v7.preference.PreferenceManager
import com.tiagobagni.myshoppinglist.*
import com.tiagobagni.myshoppinglist.archive.ArchivedListsManager
import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListViewModel
import com.tiagobagni.myshoppinglist.archive.ArchivedShoppingListRepository
import com.tiagobagni.myshoppinglist.settings.SettingsKeys
import com.tiagobagni.myshoppinglist.settings.SettingsRepository
import com.tiagobagni.myshoppinglist.settings.SettingsViewModel
import com.tiagobagni.myshoppinglist.stock.StockItemsDetailsViewModel
import com.tiagobagni.myshoppinglist.stock.StockItemsViewModel
import com.tiagobagni.myshoppinglist.stock.StockRepository
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

val appModule = applicationContext {
    factory { StockRepository(get()) }
    factory { ShoppingListRepository(get()) }
    factory { ArchivedShoppingListRepository(get()) }
    factory { SettingsRepository(get(), get()) }
    factory { SettingsViewModel(get()) }
    bean {
        Room.databaseBuilder(
            androidApplication(),
            MyShoppingListDatabase::class.java,
            "my_shopping_list"
        ).build()
    }

    bean {
        PreferenceManager.getDefaultSharedPreferences(androidApplication())
    }
    bean { SettingsKeys(androidApplication()) }
    bean { ArchivedListsManager(get(), get()) }

    viewModel { params -> ShoppingListViewModel(get(), get(), get(), params["listName"]) }
    viewModel { StockItemsViewModel(get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { ArchivedShoppingListViewModel(get()) }
    viewModel { StockItemsDetailsViewModel(get()) }
    viewModel { NewShoppingListViewModel(get()) }
}

val modules = listOf(appModule)