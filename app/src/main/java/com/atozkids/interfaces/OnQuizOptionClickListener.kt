package com.atozkids.interfaces

import android.view.View
import com.atozkids.responsemodels.QuizResponseModel

interface OnQuizOptionClickListener {
    fun onClick(view: View, option: QuizResponseModel.Datum.Option)
}