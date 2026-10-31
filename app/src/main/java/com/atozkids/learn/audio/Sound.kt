package com.atozkids.learn.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.atozkids.learn.data.Prefs

/**
 * All audio in the app: short effects (SoundPool), the spoken word / rhyme (MediaPlayer),
 * and looping background music. Everything is read from assets, nothing touches the network.
 */
class Sound(private val context: Context, private val prefs: Prefs) {
    private val assets = context.assets
    private val attrs = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val pool = SoundPool.Builder().setMaxStreams(4).setAudioAttributes(attrs).build()
    private val effects = listOf("click", "success", "fail", "cheer", "star").associateWith { name ->
        assets.openFd("audio/sfx/$name.mp3").use { pool.load(it, 1) }
    }

    private var voice: MediaPlayer? = null
    private var rhyme: MediaPlayer? = null
    private var music: MediaPlayer? = null
    private var inForeground = false
    private var silent = false

    /** The quiz is played in silence: no effects, no voice, no music. */
    fun silence(on: Boolean) {
        silent = on
        if (on) stopVoice()
        syncMusic()
    }

    fun sfx(name: String) {
        if (!prefs.sfx || silent) return
        effects[name]?.let { pool.play(it, 1f, 1f, 1, 0, 1f) }
    }

    /** Speak an item's word. Replaces whatever was being spoken. */
    fun speak(assetPath: String) {
        stopVoice()
        voice = player(assetPath, loop = false, onDone = { if (voice === it) stopVoice() })
        voice?.start()
    }

    fun stopVoice() {
        voice?.release()
        voice = null
    }

    // --- rhymes: need position and seeking, and they pause the background music ---

    fun playRhyme(assetPath: String) {
        stopRhyme()
        stopVoice()
        rhyme = player(assetPath, loop = false, onDone = {})
        music?.pause()
        rhyme?.start()
    }

    val rhymeDurationMs get() = rhyme?.duration ?: 0
    val rhymePositionMs get() = rhyme?.currentPosition ?: 0
    val rhymePlaying get() = rhyme?.isPlaying == true

    fun pauseRhyme() { rhyme?.pause() }
    fun resumeRhyme() { rhyme?.start() }
    fun seekRhyme(ms: Int) { rhyme?.seekTo(ms) }

    fun stopRhyme() {
        rhyme?.release()
        rhyme = null
        syncMusic()
    }

    // --- background music ---

    /** Start, pause or resume the music so it matches the setting and the app state. */
    fun syncMusic() {
        val shouldPlay = prefs.music && inForeground && rhyme == null && !silent
        if (shouldPlay) {
            if (music == null) {
                music = player("audio/sfx/music.mp3", loop = true, onDone = {})?.apply { setVolume(0.28f, 0.28f) }
            }
            music?.takeIf { !it.isPlaying }?.start()
        } else {
            music?.takeIf { it.isPlaying }?.pause()
        }
    }

    fun onForeground() { inForeground = true; syncMusic() }

    fun onBackground() {
        inForeground = false
        stopVoice()
        rhyme?.pause()
        music?.pause()
    }

    private fun player(path: String, loop: Boolean, onDone: (MediaPlayer) -> Unit): MediaPlayer? = try {
        MediaPlayer().apply {
            setAudioAttributes(attrs)
            assets.openFd(path).use { setDataSource(it.fileDescriptor, it.startOffset, it.length) }
            isLooping = loop
            setOnCompletionListener { onDone(it) }
            prepare()
        }
    } catch (e: Exception) {
        null
    }
}
