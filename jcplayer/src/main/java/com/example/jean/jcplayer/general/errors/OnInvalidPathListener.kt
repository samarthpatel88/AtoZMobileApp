package com.example.jean.jcplayer.general.errors

import com.example.jean.jcplayer.model.JcAudio


interface OnInvalidPathListener {

    /**
     * Audio path error jcPlayerManagerListener.
     * @param jcAudio The wrong audio.
     */
    fun onPathError(jcAudio: JcAudio)
}