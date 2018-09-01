package com.tiagobagni.myshoppinglist

import android.app.Application
import com.tiagobagni.myshoppinglist.di.modules
import org.koin.android.ext.android.startKoin
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes


class MyShoppingListApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, modules)
        AppCenter.start(
            this,
            BuildConfig.APP_CENTER_SECRET,
            Analytics::class.java,
            Crashes::class.java
        )
    }
}