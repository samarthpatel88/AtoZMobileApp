package com.atozkids.ui.activities

import ApiManager
import BaseActivity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.atozkids.R
import com.atozkids.data.DataManager
import com.atozkids.interfaces.OnAnimatedClick
import com.atozkids.responsemodels.CategoryListResponseModel
import com.atozkids.ui.adapters.QuizFragmentsAdapter
import com.atozkids.utils.ImageUtils
import com.atozkids.utils.animatedClick
import com.atozkids.utils.callApi
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : BaseActivity() {
    object ParcelData {
        const val CATEGORY = "CATEGORY"
    }

    var quizCounter = 0

    lateinit var category: CategoryListResponseModel.Datum
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onInit()
    }

    private fun onInit() {
        category =
            intent.getSerializableExtra(LearningActivity.ParcelData.CATEGORY) as CategoryListResponseModel.Datum

        imgHome.setOnClickListener {
            it.animatedClick(object : OnAnimatedClick {
                override fun onClick() {
                    finish()
                }
            })
        }

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

        ImageUtils.showImageFromPath(category.categoryicon, imgCategory)
        txtCategory.text = category.categoryname
        getQuizItem()
    }

    private fun getQuizItem() {
        showLoading()
        ApiManager.instance
            .getQuiz(category.categoryid).callApi(
                onSuccess = {
                    hideLoading()
                    if (it.status) {
                        vpQuizItems.adapter = QuizFragmentsAdapter(supportFragmentManager, it.data)
                    } else {
                        showErrorMsg(it.message)
                    }
                }, onFailure = {
                    hideLoading()
                    showErrorMsg(it!!.message)
                }
            )
    }

    fun setQuizCorrectCounter() {
        quizCounter++

        Handler().postDelayed({
            if (vpQuizItems.currentItem == vpQuizItems.adapter!!.count - 1) {
                showFinalScoreDialog()
            } else {
                vpQuizItems.setCurrentItem(vpQuizItems.currentItem + 1, true)
            }
        }, 2000)
    }

    private fun showFinalScoreDialog() {
        if (this.isDestroyed) {
            return
        }
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setCancelable(false)
        alert.setTitle(getString(R.string.str_result))
        alert.setMessage(getString(R.string.str_your_final_score_is) + " " + quizCounter + "/" + vpQuizItems.adapter!!.count)
        alert.setPositiveButton(
            getString(R.string.str_close)
        ) { dialog, which ->
            dialog.dismiss()
            finish()
        }
        val dialog = alert.create()
        dialog.show()
    }


    fun setInCorrectCounter() {
        Handler().postDelayed({
            if (vpQuizItems.currentItem == vpQuizItems.adapter!!.count - 1) {
                showFinalScoreDialog()
            } else {
                vpQuizItems.setCurrentItem(vpQuizItems.currentItem + 1, true)
            }
        }, 5000)
    }

}
