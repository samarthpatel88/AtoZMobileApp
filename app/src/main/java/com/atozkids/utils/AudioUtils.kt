package com.atozkids.utils

import android.media.MediaPlayer
import com.atozkids.app.App
import java.io.IOException


object AudioUtils {
    private lateinit var mp: MediaPlayer

    fun playLearningAudio(url: String) {
        try {
            mp = MediaPlayer()
            mp.setDataSource(url)
            mp.prepare()
            mp.start()
            mp.setOnCompletionListener {
                mp.release()
            }
        } catch (e: IOException) {
            log(e.message + "")
        }
    }

    /**
     * stop playing previous sound
     */
    fun stopPrevious() {
        try {
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
        } catch (e: Exception) {
            log(e.message + "")
        }
    }

    fun playResourceAudio(sound: Int) {
        val mp = MediaPlayer.create(App.getAppContext(), sound)
        try {
            mp.start()
            mp.setOnCompletionListener {
                mp.release()
            }
        } catch (e: IOException) {
            log(e.message + "")
        }
    }
}
