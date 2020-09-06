package com.atozkids.utils.prefrence

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.atozkids.app.App.Companion.getAppContext

/**
 * Class contains all the necessary generic methods to save and retrieve data from @[SharedPreferences]
 */
object SharedPreferenceManager {

    /**
     * Get instance of default @[SharedPreferences]
     */
    private val sharedPreferenceInstance: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(getAppContext())

    /**
     * Get instance of default @[SharedPreferences] Editor
     */
    val sharedPreferenceEditor: SharedPreferences.Editor
        get() = sharedPreferenceInstance.edit()

    /**
     * Get saved string from @[SharedPreferences]
     * @param key any string from @[PreferenceKeys]
     */
    fun getString(key: String): String {
        return sharedPreferenceInstance.getString(key, "") ?: return ""
    }

    /**
     * Save string to @[SharedPreferences]
     * @param key any string from @[PreferenceKeys]
     * @param value value to store
     */
    fun setString(key: String, value: String) {
        sharedPreferenceEditor.putString(key, value).commit()
    }

    /**
     * Get saved addToken from @[SharedPreferences]
     * @param key any string from @[PreferenceKeys]
     */
    fun getBoolean(key: String): Boolean {
        return sharedPreferenceInstance.getBoolean(key, true)
    }

    fun setBooleanPreference(key: String, value: Boolean) {
        val preferences = sharedPreferenceInstance
        if (preferences != null) {
            val editor = preferences!!.edit()
            editor.putBoolean(key, value)
            editor.commit()
        }
    }

    fun getBooleanPreference(key: String, defaultValue: Boolean): Boolean {
        var value = defaultValue
        val preferences = sharedPreferenceInstance
        if (preferences != null) {
            value = preferences!!.getBoolean(key, defaultValue)
        }
        return value
    }

    /**
     * Save addToken to @[SharedPreferences]
     * @param key any string from @[PreferenceKeys]
     * @param value value to store
     */
    fun setBoolean(key: String, value: Boolean) {
        sharedPreferenceEditor.putBoolean(key, value).commit()
    }

    /**
     * Get saved integer from @[SharedPreferences]
     * @param key any string from @[PreferenceKeys]
     */
    fun getInt(key: String): Int {
        return sharedPreferenceInstance.getInt(key, 0)
    }

    /**
     * save integer to @[SharedPreferences]
     * @param key any string from @[PreferenceKeys]
     * @param value value to store
     */
    fun setInt(key: String, value: Int) {
        sharedPreferenceEditor.putInt(key, value).commit()
    }

}