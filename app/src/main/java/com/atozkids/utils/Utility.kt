package com.atozkids.utils

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.atozkids.R
import com.atozkids.utils.widgets.MyProgressDialog
import com.google.gson.Gson

object Utility {
    internal var pdlg: MyProgressDialog? = null

    fun showLoading(c: Context) {
        if (pdlg == null) {
            pdlg = MyProgressDialog(c)
            pdlg!!.setCancelable(false)

            try {
                pdlg!!.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun closeLoading() {
        if (pdlg != null && pdlg!!.isShowing) {
            pdlg!!.dismiss()
            pdlg = null
        }
    }

    fun showErrorToast(ct: Context, message: String) {
        showToast(ct, message, true)
    }


    fun showSuccessToast(ct: Context, message: String) {
        showToast(ct, message, false)
    }

    private fun showToast(ct: Context, message: String, isError: Boolean) {
        try {
            val inflater = LayoutInflater.from(ct)
            val layout = inflater.inflate(
                R.layout.layout_toast_message,
                null
            )
            val textV = layout.findViewById(R.id.lbl_toast) as TextView
            if (isError) {
                textV.setBackgroundResource(R.drawable.toast_error_background)
                textV.setTextColor(Color.WHITE)
            }
            textV.text = message
            val toast = Toast(ct)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun getStringFromObject(`object`: Any): String {

        val gson = Gson()
        return gson.toJson(`object`)
    }
}