package com.atozkids.app

import android.app.Application
import androidx.multidex.MultiDex

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appInstance = this
        appStoreUrl = "https://play.google.com/store/apps/details?id=$packageName"
        MultiDex.install(this)
    }

    companion object {
        @JvmField
        var appStoreUrl: String? = ""

        @JvmField
        var appInstance: App? = null

        @JvmStatic
        fun getAppContext(): App {
            return appInstance as App
        }

    }
}
