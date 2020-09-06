package com.atozkids.utils

import android.content.Context
import android.os.Build
import android.provider.Settings


object DeviceUtils {

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

    }

    fun getDeviceModel(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }


    private fun capitalize(s: String): String {
        if (s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }

    fun getDeviceOs(): String {
        return "android " + Build.VERSION.RELEASE
    }
}