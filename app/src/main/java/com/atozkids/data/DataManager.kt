package com.atozkids.data

import com.atozkids.responsemodels.CategoryItemsResponseModel
import com.atozkids.responsemodels.CategoryListResponseModel
import com.atozkids.responsemodels.MenuContentResponseModel
import com.atozkids.responsemodels.RhymesResponseModel
import com.atozkids.utils.DateTimeUtils
import com.atozkids.utils.Utility
import com.atozkids.utils.prefrence.PreferenceKeys
import com.atozkids.utils.prefrence.SharedPreferenceManager
import com.google.gson.Gson
import java.util.*

object DataManager {

    /**
     * sounds
     */
    fun getSound(): Boolean {
        return SharedPreferenceManager.getBoolean(PreferenceKeys.SOUND_SETTING)
    }

    fun setSound(soundStatus: Boolean) {
        SharedPreferenceManager.setBoolean(PreferenceKeys.SOUND_SETTING, soundStatus)
    }


    /**
     * menu content
     */
    fun setMenuContentData(data: MenuContentResponseModel.Data) {
        SharedPreferenceManager.setString(
            PreferenceKeys.MENU_CONTENT,
            Utility.getStringFromObject(data)
        )
    }

    fun getMenuContentData(): MenuContentResponseModel.Data? {
        if (SharedPreferenceManager.getString(PreferenceKeys.MENU_CONTENT).isEmpty()) {
            return null
        }
        return Gson().fromJson(
            SharedPreferenceManager.getString(PreferenceKeys.MENU_CONTENT),
            MenuContentResponseModel.Data::class.java
        )
    }

    fun setMenuLastCallDate() {
        SharedPreferenceManager.setString(
            PreferenceKeys.MENU_LAST_CALL_DATE,
            DateTimeUtils.dateFormat.format(Date())
        )
    }

    fun getMenuLastCallDate(): String {
        return SharedPreferenceManager.getString(PreferenceKeys.MENU_LAST_CALL_DATE)
    }


    /**
     * category list
     */
    fun setCategoryListData(data: CategoryListResponseModel) {
        SharedPreferenceManager.setString(
            PreferenceKeys.CATEGORY_LIST,
            Utility.getStringFromObject(data)
        )
    }

    fun getCategoryListData(): CategoryListResponseModel? {
        if (SharedPreferenceManager.getString(PreferenceKeys.CATEGORY_LIST).isEmpty()) {
            return null
        }
        return Gson().fromJson(
            SharedPreferenceManager.getString(PreferenceKeys.CATEGORY_LIST),
            CategoryListResponseModel::class.java
        )
    }

    fun setCategoryLastCallDate() {
        SharedPreferenceManager.setString(
            PreferenceKeys.CATEGORY_LAST_CALL_DATE,
            DateTimeUtils.dateFormat.format(Date())
        )
    }

    fun getCategoryLastCallDate(): String {
        return SharedPreferenceManager.getString(PreferenceKeys.CATEGORY_LAST_CALL_DATE)
    }


    /**
     * category items
     */
    fun setCategoryItemsData(data: CategoryItemsResponseModel, categoryId: Int) {
        SharedPreferenceManager.setString(
            PreferenceKeys.CATEGORY_ITEMS + "_" + categoryId,
            Utility.getStringFromObject(data)
        )
    }

    fun getCategoryItemsData(categoryId: Int): CategoryItemsResponseModel? {
        if (SharedPreferenceManager.getString(PreferenceKeys.CATEGORY_ITEMS + "_" + categoryId)
                .isEmpty()
        ) {
            return null
        }
        return Gson().fromJson(
            SharedPreferenceManager.getString(PreferenceKeys.CATEGORY_ITEMS + "_" + categoryId),
            CategoryItemsResponseModel::class.java
        )
    }

    fun setCategoryItemsLastCallDate(categoryId: Int) {
        SharedPreferenceManager.setString(
            PreferenceKeys.CATEGORY_ITEMS_LAST_CALL_DATE + "_" + categoryId,
            DateTimeUtils.dateFormat.format(Date())
        )
    }

    fun getCategoryItemsLastCallDate(categoryId: Int): String {
        return SharedPreferenceManager.getString(PreferenceKeys.CATEGORY_ITEMS_LAST_CALL_DATE + "_" + categoryId)
    }

    /**
     * voice selection
     */
    fun setVoiceGender(gender: Int) {
        SharedPreferenceManager.setInt(PreferenceKeys.VOICE_GENDER, gender)
    }

    fun getVoiceGender(): Int {
        return SharedPreferenceManager.getInt(PreferenceKeys.VOICE_GENDER)
    }

    /**
     * Rhymes list
     */

    fun setRhymesListData(data: RhymesResponseModel) {
        SharedPreferenceManager.setString(
            PreferenceKeys.RHYMES_ITEMS,
            Utility.getStringFromObject(data)
        )
    }

    fun getRhymesListData(): RhymesResponseModel? {
        if (SharedPreferenceManager.getString(PreferenceKeys.RHYMES_ITEMS)
                .isEmpty()
        ) {
            return null
        }
        return Gson().fromJson(
            SharedPreferenceManager.getString(PreferenceKeys.RHYMES_ITEMS),
            RhymesResponseModel::class.java
        )
    }

    fun setRhymesLastCallDate() {
        SharedPreferenceManager.setString(
            PreferenceKeys.RHYMES_ITEMS_LAST_CALL_DATE,
            DateTimeUtils.dateFormat.format(Date())
        )
    }

    fun getRhymesLastCallDate(): String {
        return SharedPreferenceManager.getString(PreferenceKeys.RHYMES_ITEMS_LAST_CALL_DATE)
    }
}
