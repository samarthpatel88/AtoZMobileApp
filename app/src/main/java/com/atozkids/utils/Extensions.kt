package com.atozkids.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.atozkids.R
import com.atozkids.app.App.Companion.appInstance
import com.atozkids.app.App.Companion.getAppContext
import com.atozkids.data.DataManager
import com.atozkids.interfaces.OnAnimatedClick
import com.atozkids.responsemodels.CommonErrorResponse
import com.atozkids.utils.Utility.isNetworkConnected
import com.atozkids.utils.Utility.showErrorToast
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


fun <T> Call<T>.callApi(
    onSuccess: (body: T) -> Unit,
    onFailure: (t: CommonErrorResponse?) -> Unit
) {
    if (isNetworkConnected(appInstance!!)) {
        this.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>) {
                val body: T? = response.body()
                if (body != null) {
                    onSuccess(body)
                } else {
                    onFailure(
                        Gson().fromJson(
                            response.errorBody()?.string(),
                            CommonErrorResponse::class.java
                        )
                    )
                }
            }

            override fun onFailure(call: Call<T>?, t: Throwable?) {
                val errorResponse = CommonErrorResponse()
                errorResponse.message = t!!.message
                onFailure(errorResponse)
            }

        })
    } else {
        val errorResponse = CommonErrorResponse()
        errorResponse.message = getAppContext().getString(R.string.str_no_internet_message)
        onFailure(errorResponse)

    }
}

fun View.onClick(onClick: () -> Unit) {
    setOnClickListener { onClick() }
}

fun String.showSuccessMsg() {
    Toast.makeText(appInstance, this, Toast.LENGTH_LONG).show()
}

fun <T> Activity.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    var intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}

fun <T> Activity.openActivityForResult(it: Class<T>, code: Int, extras: Bundle.() -> Unit = {}) {
    var intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivityForResult(intent, code)
}

fun <T> Fragment.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    var intent = Intent(activity, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}

fun String.showFailedMsg() {
    Toast.makeText(appInstance, this, Toast.LENGTH_LONG).show()
}

fun Context.toast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun log(message: String) {
    Log.e("Content", " == $message")
}

fun View.animatedClick(onAnimatedClick: OnAnimatedClick) {
    if (DataManager.getSound()) {
        AudioUtils.playResourceAudio(R.raw.click)
    }
    val scaleAnimation = ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, 0.5f, 0.5f)
    scaleAnimation.duration = 10
    scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {
            val scaleAnimationReverse = ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, 0.5f, 0.5f)
            scaleAnimationReverse.duration = 10
            scaleAnimationReverse.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    onAnimatedClick.onClick()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            startAnimation(scaleAnimationReverse)
        }

        override fun onAnimationRepeat(animation: Animation) {}
    })
    startAnimation(scaleAnimation)
}


fun Fragment.isNetConnected(): Boolean {
    return if (isNetworkConnected(appInstance!!)) {
        true
    } else {
        showErrorToast(appInstance!!, getString(R.string.str_no_internet_message))
        false
    }
}