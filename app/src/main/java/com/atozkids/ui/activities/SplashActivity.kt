package com.atozkids.ui.activities

import ApiManager
import ApiRepo
import BaseActivity
import android.os.Bundle
import com.atozkids.BuildConfig
import com.atozkids.R
import com.atozkids.utils.DeviceUtils
import com.atozkids.utils.callApi
import com.atozkids.utils.openActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject


class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        insertUpdateDeviceDetail()
    }

    private fun insertUpdateDeviceDetail() {

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result!!.token

                val jsonObject = JsonObject()
                jsonObject.addProperty("appid", ApiRepo.APP_CODE)
                jsonObject.addProperty("appversion", BuildConfig.VERSION_NAME)
                jsonObject.addProperty("deviceos", DeviceUtils.getDeviceOs())
                jsonObject.addProperty("deviceid", DeviceUtils.getDeviceId(this))
                jsonObject.addProperty("fcmtoken", token)
                jsonObject.addProperty("modelno", DeviceUtils.getDeviceModel())

                ApiManager.instance
                    .insertUpdateDeviceDetails(jsonObject).callApi(
                        onSuccess = {
                            openActivity(DashboardActivity::class.java)
                            finishAffinity()
                        }, onFailure = {
                            showErrorMsg(it!!.message)
                        }
                    )
            })

    }

}
