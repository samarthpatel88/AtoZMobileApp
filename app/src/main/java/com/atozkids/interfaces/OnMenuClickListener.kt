package com.atozkids.interfaces

import com.atozkids.responsemodels.MenuContentResponseModel

interface OnMenuClickListener {
    fun onClick(menu: MenuContentResponseModel.Data.Menubar)
}