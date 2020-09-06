package com.atozkids.ui.activities

import ApiManager
import BaseActivity
import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.PopupMenu
import com.atozkids.R
import com.atozkids.app.App.Companion.appStoreUrl
import com.atozkids.data.DataManager
import com.atozkids.interfaces.OnAnimatedClick
import com.atozkids.utils.*
import com.google.android.material.button.MaterialButton
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.*


class DashboardActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        onInit()

        Permissions.check(
            this /*context*/,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            null,
            object : PermissionHandler() {
                override fun onGranted() {
                    if (DataManager.getMenuContentData() == null || DataManager.getMenuLastCallDate() != DateTimeUtils.dateFormat.format(
                            Date()
                        )
                    ) {
                        getMenuContentData()
                    } else {
                        displayMenu()
                    }
                }
            })
    }

    private fun displayMenu() {

        imgLearning.setOnClickListener {
            if (DataManager.getSound()) {
                AudioUtils.playResourceAudio(R.raw.click)
            }
            Permissions.check(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        openActivity(CategorySelectionActivity::class.java) {
                            putString(
                                CategorySelectionActivity.CategoryClass.TYPE,
                                CategorySelectionActivity.CategoryClass.LEARNING
                            )
                        }
                    }
                })

        }
        imgQuiz.setOnClickListener {
            if (DataManager.getSound()) {
                AudioUtils.playResourceAudio(R.raw.click)
            }
            Permissions.check(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        openActivity(CategorySelectionActivity::class.java) {
                            putString(
                                CategorySelectionActivity.CategoryClass.TYPE,
                                CategorySelectionActivity.CategoryClass.QUIZ
                            )
                        }
                    }
                })

        }
        imgRhymes.setOnClickListener {
            if (DataManager.getSound()) {
                AudioUtils.playResourceAudio(R.raw.click)
            }
            Permissions.check(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                null,
                object : PermissionHandler() {
                    override fun onGranted() {
                        openActivity(RhymesActivity::class.java)
                    }
                })

        }
    }

    private fun onInit() {
        imgSound.setOnClickListener {
            it.animatedClick(object : OnAnimatedClick {
                override fun onClick() {
                    if (DataManager.getSound()) {
                        imgSound.setImageResource(R.drawable.ic_sound_disable)
                    } else {
                        imgSound.setImageResource(R.drawable.ic_sound)
                    }
                    DataManager.setSound(DataManager.getSound().not())
                }
            })
        }

        imgShare.setOnClickListener {
            it.animatedClick(object : OnAnimatedClick {
                override fun onClick() {
                    shareAppUrl()
                }
            })
        }

        imgRateUs.setOnClickListener {
            it.animatedClick(object : OnAnimatedClick {
                override fun onClick() {
                    rateApp()
                }
            })

        }

        imgSetting.setOnClickListener {
            it.animatedClick(object : OnAnimatedClick {
                override fun onClick() {
                    showSettingDialog(it)
                }
            })
        }

        imgVoice.setOnClickListener {
            it.animatedClick(object : OnAnimatedClick {
                override fun onClick() {
                    showVoiceSelectionDialog()
                }

            })
        }
    }

    private fun showVoiceSelectionDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_voice_selection)
        val imgMale = dialog.findViewById(R.id.imgMale) as ImageView
        val imgFemale = dialog.findViewById(R.id.imgFemale) as ImageView
        val mBtnSave = dialog.findViewById(R.id.mBtnSave) as MaterialButton

        var gender = DataManager.getVoiceGender()

        if (gender == 0) {
            imgMale.background = resources.getDrawable(R.drawable.shape_circle_selected_green)
        } else {
            imgFemale.background = resources.getDrawable(R.drawable.shape_circle_selected_green)
        }
        imgMale.setOnClickListener {
            if (DataManager.getSound()) {
                AudioUtils.playResourceAudio(R.raw.click)
            }
            gender = 0
            imgMale.background = resources.getDrawable(R.drawable.shape_circle_selected_green)
            imgFemale.background = null
        }
        imgFemale.setOnClickListener {
            if (DataManager.getSound()) {
                AudioUtils.playResourceAudio(R.raw.click)
            }
            gender = 1
            imgMale.background = null
            imgFemale.background = resources.getDrawable(R.drawable.shape_circle_selected_green)
        }
        mBtnSave.setOnClickListener {
            if (DataManager.getSound()) {
                AudioUtils.playResourceAudio(R.raw.click)
            }
            DataManager.setVoiceGender(gender)
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onResume() {
        super.onResume()
        if (DataManager.getSound()) {
            imgSound.setImageResource(R.drawable.ic_sound)
        } else {
            imgSound.setImageResource(R.drawable.ic_sound_disable)
        }
    }

    private fun getMenuContentData() {
        showLoading()
        ApiManager.instance
            .getMenuContent().callApi(
                onSuccess = {
                    hideLoading()
                    if (it.status) {
                        DataManager.setMenuContentData(it.data)
                        DataManager.setMenuLastCallDate()
                        displayMenu()
                    } else {
                        showErrorMsg(it.message)
                    }
                }, onFailure = {
                    hideLoading()
                    showErrorMsg(it!!.message)
                }
            )
    }

    private fun shareAppUrl() {
        try {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = ("text/plain")
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            var shareContent = ""
            if (DataManager.getMenuContentData() != null) {
                shareContent = DataManager.getMenuContentData()!!.share + "\n\n"
            }
            shareContent += appStoreUrl
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareContent)
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.str_share_using)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun rateApp() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(appStoreUrl)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
    }

    private fun showSettingDialog(settingIcon: View) {
        val popup = PopupMenu(this@DashboardActivity, settingIcon)
        popup.menuInflater.inflate(R.menu.setting_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.menuPrivacy) openActivity(WebViewActivity::class.java) {
                putString(
                    WebViewActivity.Pages.PRIVACY_POLICY, ""
                )
            } else openActivity(WebViewActivity::class.java) {
                putString(
                    WebViewActivity.Pages.CONTACT_US, ""
                )
            }
            return@setOnMenuItemClickListener true
        }

        popup.show() //showing popup menu

    }

    /**
     * show confirmation dialog on back exit
     */
    override fun onBackPressed() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setCancelable(false)
        alert.setMessage(getString(R.string.str_are_you_sure_exit))
        alert.setPositiveButton(
            getString(R.string.str_yes)
        ) { dialog, which ->
            dialog.dismiss()
            finish()
        }
        alert.setNeutralButton(
            getString(R.string.str_rate_us)
        ) { dialog, which ->
            rateApp()
            dialog.dismiss()
            finish()
        }
        alert.setNegativeButton(
            getString(R.string.str_no)
        ) { dialog, which ->
            dialog.dismiss()
        }
        val dialog = alert.create()
        dialog.show()
        val negButton: Button =
            (dialog as AlertDialog).getButton(DialogInterface.BUTTON_NEGATIVE)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 20, 0)
        negButton.layoutParams = params
    }

}
