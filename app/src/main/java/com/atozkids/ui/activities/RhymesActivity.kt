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
import com.atozkids.ui.adapters.RhymesFragmentsAdapter
import com.atozkids.utils.DateTimeUtils
import com.atozkids.utils.FileUtils
import com.atozkids.utils.animatedClick
import com.atozkids.utils.callApi
import com.example.jean.jcplayer.JcPlayerManagerListener
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.model.JcAudio
import kotlinx.android.synthetic.main.activity_rhymes.*
import java.io.File
import java.lang.Exception
import java.util.*


class RhymesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rhymes)

        onInit()
    }

    private fun onInit() {
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
                    try {
                        jcPlayer.mute(DataManager.getSound().not())
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }
            })
        }

        if (DataManager.getRhymesListData() == null || DataManager.getRhymesLastCallDate() != DateTimeUtils.dateFormat.format(
                Date()
            )
        ) {
            getRhymesData()
        } else {
            displayRhymes(false)
        }
    }

    private fun displayRhymes(isFromNew: Boolean) {

        if (isFromNew) {
            val folder = File(
                Environment.getExternalStorageDirectory()
                    .toString() + NewFileDownloaderService.APP_FOLDER_NAME
            )

            if (folder.exists()) {
                folder.deleteRecursively()
            }
        }

        vpRhymes.adapter = RhymesFragmentsAdapter(
            supportFragmentManager,
            DataManager.getRhymesListData()!!.data
        )

        vpRhymes.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

            }

        })


        val data = DataManager.getRhymesListData()!!.data
        val jcAudios: ArrayList<JcAudio> = ArrayList()

        for (item in data) {
            if (FileUtils.getRealPhotoFile(item.title).exists().not()) {
                NewFileDownloaderService.downloadPhotoFile(this, item.title, item.image)
            }
            if (FileUtils.getRealSoundFile(item.title).exists().not()) {
                NewFileDownloaderService.downloadSoundFile(this, item.title, item.audio)
                jcAudios.add(JcAudio.createFromURL(item.title, item.audio))
            } else {
                jcAudios.add(
                    JcAudio.createFromFilePath(
                        item.title,
                        FileUtils.getRealSoundFile(item.title).absolutePath
                    )
                )
            }
        }

        jcPlayer.initPlaylist(jcAudios, object : JcPlayerManagerListener {
            override fun onCompletedAudio() {

            }

            override fun onContinueAudio(status: JcStatus) {

            }

            override fun onJcpError(throwable: Throwable) {

            }

            override fun onPaused(status: JcStatus) {

            }

            override fun onPlaying(status: JcStatus) {
            }

            override fun onPreparedAudio(status: JcStatus) {
                status.jcAudio.position?.let { vpRhymes.setCurrentItem(it, true) }
                try {
                    jcPlayer.mute(DataManager.getSound().not())
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onStopped(status: JcStatus) {

            }

            override fun onTimeChanged(status: JcStatus) {

            }

        })

        try {
            jcPlayer.mute(DataManager.getSound().not())
        }catch (e:Exception){
            e.printStackTrace()
        }
    }



    private fun getRhymesData() {
        showLoading()
        ApiManager.instance
            .getRhymes().callApi(
                onSuccess = {
                    hideLoading()
                    if (it.status) {
                        DataManager.setRhymesListData(it)
                        DataManager.setRhymesLastCallDate()
                        displayRhymes(true)
                    } else {
                        showErrorMsg(it.message)
                    }
                }, onFailure = {
                    hideLoading()
                    showErrorMsg(it!!.message + "")
                }
            )
    }

    override fun onResume() {
        super.onResume()
        if (jcPlayer.isPaused) {
            jcPlayer.currentAudio?.let { jcPlayer.playAudio(it) }
        }

        if (DataManager.getSound()) {
            imgSound.setImageResource(R.drawable.ic_sound)
        } else {
            imgSound.setImageResource(R.drawable.ic_sound_disable)
        }
    }

    override fun onPause() {
        super.onPause()
        if (jcPlayer.isPlaying) {
            jcPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        jcPlayer.kill()
    }
}

