package com.atozkids.interfaces
import android.view.View
import com.atozkids.responsemodels.CategoryListResponseModel

interface OnCategoryClickListener {
    fun onClick(view: View,category: CategoryListResponseModel.Datum)
}