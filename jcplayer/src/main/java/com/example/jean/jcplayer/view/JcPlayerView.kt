package com.example.jean.jcplayer.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.jean.jcplayer.JcPlayerManager
import com.example.jean.jcplayer.JcPlayerManagerListener
import com.example.jean.jcplayer.R
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.general.errors.AudioListNullPointerException
import com.example.jean.jcplayer.general.errors.OnInvalidPathListener
import com.example.jean.jcplayer.model.JcAudio
import kotlinx.android.synthetic.main.view_jcplayer.view.*


class JcPlayerView : LinearLayout, View.OnClickListener, SeekBar.OnSeekBarChangeListener,
    JcPlayerManagerListener {

    private val jcPlayerManager: JcPlayerManager by lazy {
        JcPlayerManager.getInstance(context).get()!!
    }

    val myPlaylist: List<JcAudio>?
        get() = jcPlayerManager.playlist

    val isPlaying: Boolean
        get() = jcPlayerManager.isPlaying()

    val isPaused: Boolean
        get() = jcPlayerManager.isPaused()

    val currentAudio: JcAudio?
        get() = jcPlayerManager.currentAudio

    var onInvalidPathListener: OnInvalidPathListener? = null

    var jcPlayerManagerListener: JcPlayerManagerListener? = null
        set(value) {
            jcPlayerManager.jcPlayerManagerListener = value
        }


    companion object {
        private const val PULSE_ANIMATION_DURATION = 200L
        private const val TITLE_ANIMATION_DURATION = 600
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()

        context.theme
            .obtainStyledAttributes(attrs, R.styleable.JcPlayerView, 0, 0)
            .also { setAttributes(it) }
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()

        context.theme
            .obtainStyledAttributes(attrs, R.styleable.JcPlayerView, defStyle, 0)
            .also { setAttributes(it) }
    }

    private fun init() {
        View.inflate(context, R.layout.view_jcplayer, this)

        btnNext?.setOnClickListener(this)
        btnPrev?.setOnClickListener(this)
        btnPlay?.setOnClickListener(this)
        btnPause?.setOnClickListener(this)
        btnRandom?.setOnClickListener(this)
        btnRepeat?.setOnClickListener(this)
        btnRepeatOne?.setOnClickListener(this)
    }

    private fun setAttributes(attrs: TypedArray) {
        val defaultColor = ResourcesCompat.getColor(resources, android.R.color.black, null)
        progressBarPlayer?.indeterminateDrawable?.setColorFilter(
            attrs.getColor(
                R.styleable.JcPlayerView_progress_color,
                defaultColor
            ), PorterDuff.Mode.SRC_ATOP
        )

        btnPlay.setColorFilter(
            attrs.getColor(
                R.styleable.JcPlayerView_play_icon_color,
                defaultColor
            )
        )
        btnPlay.setImageResource(
            attrs.getResourceId(
                R.styleable.JcPlayerView_play_icon,
                R.drawable.ic_play
            )
        )

        btnPause.setImageResource(
            attrs.getResourceId(
                R.styleable.JcPlayerView_pause_icon,
                R.drawable.ic_pause
            )
        )
        btnPause.setColorFilter(
            attrs.getColor(
                R.styleable.JcPlayerView_pause_icon_color,
                defaultColor
            )
        )

        btnNext?.setColorFilter(
            attrs.getColor(
                R.styleable.JcPlayerView_next_icon_color,
                defaultColor
            )
        )
        btnNext?.setImageResource(
            attrs.getResourceId(
                R.styleable.JcPlayerView_next_icon,
                R.drawable.ic_next
            )
        )

        btnPrev?.setColorFilter(
            attrs.getColor(
                R.styleable.JcPlayerView_previous_icon_color,
                defaultColor
            )
        )
        btnPrev?.setImageResource(
            attrs.getResourceId(
                R.styleable.JcPlayerView_previous_icon,
                R.drawable.ic_previous
            )
        )

        btnRandom?.setColorFilter(
            attrs.getColor(
                R.styleable.JcPlayerView_random_icon_color,
                defaultColor
            )
        )
        btnRandomIndicator?.setColorFilter(
            attrs.getColor(
                R.styleable.JcPlayerView_random_icon_color,
                defaultColor
            )
        )
        btnRandom?.setImageResource(
            attrs.getResourceId(
                R.styleable.JcPlayerView_random_icon,
                R.drawable.ic_shuffle
            )
        )
        attrs.getBoolean(R.styleable.JcPlayerView_show_random_button, true).also { showButton ->
            if (showButton) {
                btnRandom?.visibility = View.VISIBLE
            } else {
                btnRandom?.visibility = View.GONE
            }
        }

        btnRepeat?.setColorFilter(
            attrs.getColor(
                R.styleable.JcPlayerView_repeat_icon_color,
                defaultColor
            )
        )
        btnRepeatIndicator?.setColorFilter(
            attrs.getColor(
                R.styleable.JcPlayerView_repeat_icon_color,
                defaultColor
            )
        )
        btnRepeat?.setImageResource(
            attrs.getResourceId(
                R.styleable.JcPlayerView_repeat_icon,
                R.drawable.ic_repeat
            )
        )
        attrs.getBoolean(R.styleable.JcPlayerView_show_repeat_button, true).also { showButton ->
            if (showButton) {
                btnRepeat?.visibility = View.VISIBLE
            } else {
                btnRepeat?.visibility = View.GONE
            }
        }

        btnRepeatOne?.setColorFilter(
            attrs.getColor(
                R.styleable.JcPlayerView_repeat_one_icon_color,
                attrs.getColor(R.styleable.JcPlayerView_repeat_icon_color, defaultColor)
            )
        )
        btnRepeatOne?.setImageResource(
            attrs.getResourceId(
                R.styleable.JcPlayerView_repeat_one_icon,
                R.drawable.ic_repeat_one
            )
        )
    }

    /**
     * Initialize the playlist and controls.
     *
     * @param playlist List of JcAudio objects that you want play
     * @param jcPlayerManagerListener The view status jcPlayerManagerListener (optional)
     */
    fun initPlaylist(
        playlist: List<JcAudio>,
        jcPlayerManagerListener: JcPlayerManagerListener? = null
    ) {
        /*Don't sort if the playlist have position number.
        We need to do this because there is a possibility that the user reload previous playlist
        from persistence storage like sharedPreference or SQLite.*/
        if (isAlreadySorted(playlist).not()) {
            sortPlaylist(playlist)
        }

        jcPlayerManager.playlist = playlist as ArrayList<JcAudio>
        jcPlayerManager.jcPlayerManagerListener = jcPlayerManagerListener
        jcPlayerManager.jcPlayerManagerListener = this
    }

    /**
     * Initialize an anonymous playlist with a default JcPlayer title for all audios
     *
     * @param playlist List of urls strings
     */
    fun initAnonPlaylist(playlist: List<JcAudio>) {
        generateTitleAudio(playlist, context.getString(R.string.track_number))
        initPlaylist(playlist)
    }

    /**
     * Initialize an anonymous playlist, but with a custom title for all audios
     *
     * @param playlist List of JcAudio files.
     * @param title    Default title for all audios
     */
    fun initWithTitlePlaylist(playlist: List<JcAudio>, title: String) {
        generateTitleAudio(playlist, title)
        initPlaylist(playlist)
    }

    /**
     * Add an audio for the playlist. We can track the JcAudio by
     * its id. So here we returning its id after adding to list.
     *
     * @param jcAudio audio file generated from [JcAudio]
     * @return jcAudio position.
     */
    fun addAudio(jcAudio: JcAudio): Int {
        jcPlayerManager.playlist.let {
            val lastPosition = it.size
            jcAudio.position = lastPosition + 1

            if (it.contains(jcAudio).not()) {
                it.add(lastPosition, jcAudio)
            }

            return jcAudio.position!!
        }
    }

    /**
     * Remove an audio for the playlist
     *
     * @param jcAudio JcAudio object
     */
    fun removeAudio(jcAudio: JcAudio) {
        jcPlayerManager.playlist.let {
            if (it.contains(jcAudio)) {
                if (it.size > 1) {
                    // play next audio when currently played audio is removed.
                    if (jcPlayerManager.isPlaying()) {
                        if (jcPlayerManager.currentAudio == jcAudio) {
                            it.remove(jcAudio)
                            pause()
                            resetPlayerInfo()
                        } else {
                            it.remove(jcAudio)
                        }
                    } else {
                        it.remove(jcAudio)
                    }
                } else {
                    //TODO: Maybe we need jcPlayerManager.stopPlay() for stopping the player
                    it.remove(jcAudio)
                    pause()
                    resetPlayerInfo()
                }
            }
        }
    }

    /**
     * Plays the give audio.
     * @param jcAudio The audio to be played.
     */
    fun playAudio(jcAudio: JcAudio) {
        showProgressBar()

        jcPlayerManager.playlist.let {
            if (it.contains(jcAudio).not()) {
                it.add(jcAudio)
            }


            jcPlayerManager.playAudio(jcAudio)
        }
    }

    /**
     * Shows the play button on player.
     */
    private fun showPlayButton() {
        btnPlay?.visibility = View.VISIBLE
        btnPause?.visibility = View.GONE
    }

    /**
     * Shows the pause button on player.
     */
    private fun showPauseButton() {
        btnPlay?.visibility = View.GONE
        btnPause?.visibility = View.VISIBLE
    }

    /**
     * Goes to next audio.
     */
    fun next() {
        jcPlayerManager.let { player ->
            player.currentAudio?.let {
                resetPlayerInfo()
                showProgressBar()

                try {
                    player.nextAudio()
                } catch (e: AudioListNullPointerException) {
                    dismissProgressBar()
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Continues the current audio.
     */
    fun continueAudio() {
        showProgressBar()

        try {
            jcPlayerManager.continueAudio()
        } catch (e: AudioListNullPointerException) {
            dismissProgressBar()
            e.printStackTrace()
        }
    }

    /**
     * Pauses the current audio.
     */
    fun pause() {
        jcPlayerManager.pauseAudio()
        showPlayButton()
    }


    /**
     * Goes to precious audio.
     */
    fun previous() {
        resetPlayerInfo()
        showProgressBar()

        try {
            jcPlayerManager.previousAudio()
        } catch (e: AudioListNullPointerException) {
            dismissProgressBar()
            e.printStackTrace()
        }

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnPlay ->
                btnPlay?.let {
                    applyPulseAnimation(it)
                    continueAudio()
                }

            R.id.btnPause -> {
                btnPause?.let {
                    applyPulseAnimation(it)
                    pause()
                }
            }

            R.id.btnNext ->
                btnNext?.let {
                    applyPulseAnimation(it)
                    next()
                }

            R.id.btnPrev ->
                btnPrev?.let {
                    applyPulseAnimation(it)
                    previous()
                }

            R.id.btnRandom -> {
                jcPlayerManager.onShuffleMode = jcPlayerManager.onShuffleMode.not()
                btnRandomIndicator.visibility =
                    if (jcPlayerManager.onShuffleMode) View.VISIBLE else View.GONE
            }


            else -> { // Repeat case
                jcPlayerManager.activeRepeat()
                val active = jcPlayerManager.repeatPlaylist or jcPlayerManager.repeatCurrAudio

                btnRepeat?.visibility = View.VISIBLE
                btnRepeatOne?.visibility = View.GONE

                if (active) {
                    btnRepeatIndicator?.visibility = View.VISIBLE
                } else {
                    btnRepeatIndicator?.visibility = View.GONE
                }

                if (jcPlayerManager.repeatCurrAudio) {
                    btnRepeatOne?.visibility = View.VISIBLE
                    btnRepeat?.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Create a notification player with same playlist with a custom icon.
     *
     * @param iconResource icon path.
     */
    fun createNotification(iconResource: Int) {
        jcPlayerManager.createNewNotification(iconResource)
    }

    /**
     * Create a notification player with same playlist with a default icon
     */
    fun createNotification() {
        jcPlayerManager.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // For light theme
                it.createNewNotification(R.drawable.ic_notification_default_black)
            } else {
                // For dark theme
                it.createNewNotification(R.drawable.ic_notification_default_white)
            }
        }
    }

    override fun onPreparedAudio(status: JcStatus) {
        dismissProgressBar()
        resetPlayerInfo()
        onUpdateTitle(status.jcAudio.title)

        val duration = status.duration.toInt()
    }


    override fun onProgressChanged(seekBar: SeekBar, i: Int, fromUser: Boolean) {
        jcPlayerManager.let {
            if (fromUser) {
                it.seekTo(i)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        if (jcPlayerManager.currentAudio != null) {
            showProgressBar()
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        dismissProgressBar()

        if (jcPlayerManager.isPaused()) {
            showPlayButton()
        }
    }

    override fun onCompletedAudio() {
        resetPlayerInfo()

        try {
            jcPlayerManager.nextAudio()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onContinueAudio(status: JcStatus) {
        dismissProgressBar()
    }

    override fun onPlaying(status: JcStatus) {
        dismissProgressBar()
        showPauseButton()
    }

    override fun onTimeChanged(status: JcStatus) {
        val currentPosition = status.currentPosition.toInt()
    }

    override fun onPaused(status: JcStatus) {
    }

    override fun onStopped(status: JcStatus) {
    }

    override fun onJcpError(throwable: Throwable) {
        // TODO
//        jcPlayerManager.currentAudio?.let {
//            onInvalidPathListener?.onPathError(it)
//        }
    }

    private fun showProgressBar() {
        progressBarPlayer?.visibility = ProgressBar.VISIBLE
        btnPlay?.visibility = Button.GONE
        btnPause?.visibility = Button.GONE
    }

    private fun dismissProgressBar() {
        progressBarPlayer?.visibility = ProgressBar.GONE
        showPauseButton()
    }

    private fun onUpdateTitle(title: String) {

    }

    private fun resetPlayerInfo() {

    }


    /**
     * mute un-mute player
     */
    fun mute(mute: Boolean) {
        try {
            jcPlayerManager.mute(mute)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Sorts the playlist.
     */
    private fun sortPlaylist(playlist: List<JcAudio>) {
        for (i in playlist.indices) {
            val jcAudio = playlist[i]
            jcAudio.position = i
        }
    }

    /**
     * Check if playlist already sorted or not.
     * We need to check because there is a possibility that the user reload previous playlist
     * from persistence storage like sharedPreference or SQLite.
     *
     * @param playlist list of JcAudio
     * @return true if sorted, false if not.
     */
    private fun isAlreadySorted(playlist: List<JcAudio>?): Boolean {
        // If there is position in the first audio, then playlist is already sorted.
        return playlist?.let { it[0].position != -1 } == true
    }

    /**
     * Generates a default audio title for each audio on list.
     * @param playlist The audio list.
     * @param title The default title.
     */
    private fun generateTitleAudio(playlist: List<JcAudio>, title: String) {
        for (i in playlist.indices) {
            if (title == context.getString(R.string.track_number)) {
                playlist[i].title =
                    context.getString(R.string.track_number) + " " + (i + 1).toString()
            } else {
                playlist[i].title = title
            }
        }
    }

    private fun applyPulseAnimation(view: View?) {
        view?.postDelayed({
            YoYo.with(Techniques.Pulse)
                .duration(PULSE_ANIMATION_DURATION)
                .playOn(view)
        }, PULSE_ANIMATION_DURATION)
    }

    /**
     * Kills the player
     */
    fun kill() {
        jcPlayerManager.kill()
    }
}
