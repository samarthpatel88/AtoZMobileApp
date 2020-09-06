package com.atozkids.ui.activities

import ApiManager
import BaseActivity
import android.os.Bundle
import android.os.Environment
import androidx.viewpager.widget.ViewPager
import com.atozkids.R
import com.atozkids.data.DataManager
import com.atozkids.interfaces.OnAnimatedClick
import com.atozkids.services.NewFileDownloaderService
import com.atozkids.ui.adapters.CategoryFragmentsAdapter
import com.atozkids.utils.DateTimeUtils
import com.atozkids.utils.FileUtils
import com.atozkids.utils.animatedClick
import com.atozkids.utils.callApi
import kotlinx.android.synthetic.main.activity_category_selection.*
import java.io.File
import java.util.*
import kotlin.math.ceil

class CategorySelectionActivity : BaseActivity() {
    object CategoryClass {
        const val TYPE = "TYPE"
        const val LEARNING = "LEARNING"
        const val QUIZ = "QUIZ"
    }

    lateinit var isForSelection: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_selection)

        onInit()
        if (DataManager.getCategoryListData() == null || DataManager.getCategoryLastCallDate() != DateTimeUtils.dateFormat.format(
                Date()
            )
        ) {
            getCategoryData()
        } else {
            displayCategory(false)
        }
    }

    private fun onInit() {
        isForSelection = intent.getStringExtra(CategoryClass.TYPE)!!
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

    override fun onResume() {
        super.onResume()
        if (DataManager.getSound()) {
            imgSound.setImageResource(R.drawable.ic_sound)
        } else {
            imgSound.setImageResource(R.drawable.ic_sound_disable)
        }
    }

    private fun displayCategory(isFromNew:Boolean) {

        if (isFromNew) {
            val folder = File(
                Environment.getExternalStorageDirectory()
                    .toString() + NewFileDownloaderService.APP_FOLDER_NAME
            )

            if (folder.exists()) {
                folder.deleteRecursively()
            }
        }

        val category = DataManager.getCategoryListData()!!.data
        val counts = ceil((category.size.toDouble() / 8))
        if (counts.toInt() == 1) {

            imgPrev.isEnabled = false
            imgPrev.setImageDrawable(resources.getDrawable(R.drawable.ic_left_disable))

            imgNext.isEnabled = false
            imgNext.setImageDrawable(resources.getDrawable(R.drawable.ic_right_disable))

        }

        vpCategory.adapter =
            CategoryFragmentsAdapter(supportFragmentManager, counts.toInt(), isForSelection)
        vpCategory.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

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

                    if (counts > 1) {
                        imgNext.isEnabled = true
                        imgNext.setImageDrawable(resources.getDrawable(R.drawable.ic_right))
                    }
                } else {
                    imgPrev.isEnabled = true
                    imgPrev.setImageDrawable(resources.getDrawable(R.drawable.ic_left))

                    if (position == (counts.toInt() - 1)) {
                        imgNext.isEnabled = false
                        imgNext.setImageDrawable(resources.getDrawable(R.drawable.ic_right_disable))
                    }
                }
            }

        })

        imgPrev.setOnClickListener {
            it.animatedClick(object : OnAnimatedClick {
                override fun onClick() {
                    vpCategory.setCurrentItem(vpCategory.currentItem - 1, true)
                }
            })

        }
        imgNext.setOnClickListener {
            it.animatedClick(object : OnAnimatedClick {
                override fun onClick() {
                    vpCategory.setCurrentItem(vpCategory.currentItem + 1, true)
                }
            })
        }

        for (item in category) {
            if (FileUtils.getRealPhotoFile(item.categoryname).exists().not()) {
                NewFileDownloaderService.downloadPhotoFile(this, item.categoryname, item.categoryicon)
            }
        }
    }

    private fun getCategoryData() {
        showLoading()
        ApiManager.instance
            .getCategoryList().callApi(
                onSuccess = {
                    hideLoading()
                    if (it.status) {
                        DataManager.setCategoryListData(it)
                        DataManager.setCategoryLastCallDate()
                        displayCategory(true)
                    } else {
                        showErrorMsg(it.message)
                    }
                }, onFailure = {
                    hideLoading()
                    showErrorMsg(it!!.message)
                }
            )
    }
}