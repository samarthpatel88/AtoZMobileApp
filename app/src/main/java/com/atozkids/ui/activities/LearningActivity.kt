package com.atozkids.ui.activities

import ApiManager
import BaseActivity
import android.app.Dialog
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.atozkids.R
import com.atozkids.data.DataManager
import com.atozkids.interfaces.OnAnimatedClick
import com.atozkids.responsemodels.CategoryItemsResponseModel
import com.atozkids.responsemodels.CategoryListResponseModel
import com.atozkids.services.NewFileDownloaderService
import com.atozkids.ui.adapters.LearningItemsAdapter
import com.atozkids.utils.*
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_learning.*
import java.io.File
import java.util.*


class LearningActivity : BaseActivity() {
    object ParcelData {
        const val CATEGORY = "CATEGORY"
    }

    object FileConcat {
        const val ITEM_IMAGE = "ITEM_IMAGE"
        const val CAPTION_IMAGE = "CAPTION_IMAGE"
        const val MALE_AUDIO = "MALE_AUDIO"
        const val FEMALE_AUDIO = "FEMALE_AUDIO"
    }

    lateinit var category: CategoryListResponseModel.Datum
    private var timerAutoSwipe = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onInit()
    }

    private fun onInit() {
        category =
            intent.getSerializableExtra(ParcelData.CATEGORY) as CategoryListResponseModel.Datum

        if (DataManager.getCategoryItemsData(category.categoryid) == null || DataManager.getCategoryItemsLastCallDate(
                category.categoryid
            ) != DateTimeUtils.dateFormat.format(
                Date()
            )
        ) {
            getCategoryItemsData()
        } else {
            displayCategoryItems(false)
        }

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

        imgPrev.isEnabled = false
    }

    private fun displayCategoryItems(isFromNew:Boolean) {


        if (isFromNew) {
            val folder = File(
                Environment.getExternalStorageDirectory()
                    .toString() + NewFileDownloaderService.APP_FOLDER_NAME
            )

            if (folder.exists()) {
                folder.deleteRecursively()
            }
        }


        val data = DataManager.getCategoryItemsData(category.categoryid)!!.data

        if (data.size == 1) {

            imgPrev.isEnabled = false
            imgPrev.setImageDrawable(resources.getDrawable(R.drawable.ic_left_disable))

            imgNext.isEnabled = false
            imgNext.setImageDrawable(resources.getDrawable(R.drawable.ic_right_disable))

        }

        for (item in data) {
            //item image
            if (FileUtils.getRealPhotoFile(item.itemname + FileConcat.ITEM_IMAGE).exists().not()) {
                NewFileDownloaderService.downloadPhotoFile(
                    this,
                    item.itemname + FileConcat.ITEM_IMAGE,
                    item.itemimage
                )
            }

            //caption image
            if (FileUtils.getRealPhotoFile(item.itemname + FileConcat.CAPTION_IMAGE).exists()
                    .not()
            ) {
                NewFileDownloaderService.downloadPhotoFile(
                    this,
                    item.itemname + FileConcat.CAPTION_IMAGE,
                    item.captionimage
                )
            }

            //male audio
            if (FileUtils.getRealSoundFile(item.itemname + FileConcat.MALE_AUDIO).exists().not()) {
                NewFileDownloaderService.downloadSoundFile(
                    this,
                    item.itemname + FileConcat.MALE_AUDIO,
                    item.audiomale
                )
            }

            //female audio
            if (FileUtils.getRealSoundFile(item.itemname + FileConcat.FEMALE_AUDIO).exists()
                    .not()
            ) {
                NewFileDownloaderService.downloadSoundFile(
                    this,
                    item.itemname + FileConcat.FEMALE_AUDIO,
                    item.audiofemale
                )
            }
        }

        vpItems.adapter = LearningItemsAdapter(
            supportFragmentManager,
            data
        )

        playVoice(data)

        vpItems.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == 0) {
                    AudioUtils.stopPrevious()
                    playVoice(data)
                } else {
                    AudioUtils.stopPrevious()
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    imgPrev.isEnabled = false
                    imgPrev.setImageDrawable(resources.getDrawable(R.drawable.ic_left_disable))

                    if (data.size > 1) {
                        imgNext.isEnabled = true
                        imgNext.setImageDrawable(resources.getDrawable(R.drawable.ic_right))
                    }
                } else {
                    imgPrev.isEnabled = true
                    imgPrev.setImageDrawable(resources.getDrawable(R.drawable.ic_left))

                    if (position == (data.size - 1)) {
                        imgNext.isEnabled = false
                        imgNext.setImageDrawable(resources.getDrawable(R.drawable.ic_right_disable))

                        Handler().postDelayed({
                            if (vpItems.currentItem == (data.size - 1)) {
                                showCompleteDialog()
                            }
                        }, 10000)
                    }else{
                        imgNext.isEnabled = true
                        imgNext.setImageDrawable(resources.getDrawable(R.drawable.ic_right))
                    }
                }

                setAutoSwipe()
            }

        })

        imgPrev.setOnClickListener {
            it.animatedClick(object : OnAnimatedClick {
                override fun onClick() {
                    vpItems.setCurrentItem(vpItems.currentItem - 1, true)
                }
            })
        }

        imgNext.setOnClickListener {
            it.animatedClick(object : OnAnimatedClick {
                override fun onClick() {
                    vpItems.setCurrentItem(vpItems.currentItem + 1, true)
                }
            })
        }

        if (DataManager.getCategoryItemsData(category.categoryid)!!.data.isEmpty()) {
            imgPrev.isEnabled = false
            imgNext.isEnabled = false
            showErrorMsg(getString(R.string.str_no_information_available))
        }

        imgReplay.setOnClickListener {
            it.animatedClick(object : OnAnimatedClick {
                override fun onClick() {
                    if (DataManager.getCategoryItemsData(category.categoryid)!!.data.isEmpty()
                            .not()
                    ) {
                        playVoice(data)
                    }
                }
            })
        }

        setAutoSwipe()

    }

    /**
     * play voice based on gender selection for learning item
     */
    private fun playVoice(data: MutableList<CategoryItemsResponseModel.Datum>) {
        if (data.isEmpty()) {
            return
        }

        if (DataManager.getSound().not()){
            return
        }
        val mItem = data[vpItems.currentItem]
        if (DataManager.getVoiceGender() == 0) {
            FileUtils.getRealSoundFile(mItem.itemname + FileConcat.MALE_AUDIO)
                .let {
                    if (it.exists()) {
                        AudioUtils.playLearningAudio(it.absolutePath)
                    } else {
                        AudioUtils.playLearningAudio(mItem.audiomale)
                    }
                }
        } else {
            FileUtils.getRealSoundFile(mItem.itemname + FileConcat.FEMALE_AUDIO)
                .let {
                    if (it.exists()) {
                        AudioUtils.playLearningAudio(it.absolutePath)
                    } else {
                        AudioUtils.playLearningAudio(mItem.audiofemale)
                    }
                }
        }
    }

    /**
     * auto swipe in 10 seconds
     */
    private fun setAutoSwipe() {
        timerAutoSwipe.cancel()
        timerAutoSwipe = Timer()
        timerAutoSwipe.schedule(
            object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        vpItems.setCurrentItem(vpItems.currentItem + 1, true)
                    }
                }
            },
            10000,
            10000
        )
    }

    /**
     * show learning complete dialog
     */
    private fun showCompleteDialog() {
        if (this.isDestroyed) {
            return
        }
        if (DataManager.getSound()) {
            AudioUtils.playResourceAudio(R.raw.success)
        }
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_complete_learning)
        val txtMessage = dialog.findViewById(R.id.txtMessage) as TextView
        txtMessage.text = getString(R.string.str_you_have_completed_learning_section).replace(
            "*n",
            category.categoryname
        )
        val mBtnClose = dialog.findViewById(R.id.mBtnClose) as MaterialButton
        val mBtnRestart = dialog.findViewById(R.id.mBtnRestart) as MaterialButton
        mBtnClose.setOnClickListener {
            if (DataManager.getSound()) {
                AudioUtils.playResourceAudio(R.raw.click)
            }
            dialog.dismiss()
            finish()
        }
        mBtnRestart.setOnClickListener {
            if (DataManager.getSound()) {
                AudioUtils.playResourceAudio(R.raw.click)
            }
            dialog.dismiss()
            vpItems.setCurrentItem(0, true)
        }
        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun getCategoryItemsData() {
        showLoading()
        ApiManager.instance
            .getCategoryItems(category.categoryid).callApi(
                onSuccess = {
                    hideLoading()
                    if (it.status) {
                        DataManager.setCategoryItemsData(it, category.categoryid)
                        DataManager.setCategoryItemsLastCallDate(category.categoryid)
                        displayCategoryItems(true)
                    } else {
                        showErrorMsg(it.message)
                    }
                }, onFailure = {
                    hideLoading()
                    showErrorMsg(it!!.message)
                }
            )
    }

    override fun onResume() {
        super.onResume()
        if (DataManager.getSound()) {
            imgSound.setImageResource(R.drawable.ic_sound)
        } else {
            imgSound.setImageResource(R.drawable.ic_sound_disable)
        }
        if (DataManager.getCategoryItemsData(category.categoryid) != null && DataManager.getCategoryItemsData(
                category.categoryid
            )!!.data.isEmpty().not()
        ) {
            setAutoSwipe()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            timerAutoSwipe.cancel()
        } catch (e: Exception) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioUtils.stopPrevious()
    }
}