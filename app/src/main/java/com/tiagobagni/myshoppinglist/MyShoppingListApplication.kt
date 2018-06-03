package com.tiagobagni.myshoppinglist

import android.app.Application
import com.tiagobagni.myshoppinglist.di.modules
import org.koin.android.ext.android.startKoin

class MyShoppingListApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, modules)
    }
}