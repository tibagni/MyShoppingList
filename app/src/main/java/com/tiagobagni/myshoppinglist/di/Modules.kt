package com.tiagobagni.myshoppinglist.di

import android.arch.persistence.room.Room
import com.tiagobagni.myshoppinglist.MyShoppingListDatabase
import com.tiagobagni.myshoppinglist.ShoppingListRepository
import com.tiagobagni.myshoppinglist.ShoppingListViewModel
import com.tiagobagni.myshoppinglist.stock.StockItemsViewModel
import com.tiagobagni.myshoppinglist.stock.StockRepository
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

val appModule = applicationContext {
    factory { StockRepository(get()) }
    factory { ShoppingListRepository(get()) }
    bean {
        Room.databaseBuilder(
            androidApplication(),
            MyShoppingListDatabase::class.java,
            "my_shopping_list"
        ).build()
    }

    viewModel { ShoppingListViewModel(get()) }
    viewModel { StockItemsViewModel(get()) }
}

val modules = listOf(appModule)