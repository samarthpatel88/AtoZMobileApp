package com.atozkids.learn.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.atozkids.learn.LocalGraph
import kotlinx.coroutines.delay

@Composable
fun RhymesScreen(onBack: () -> Unit, onPick: (String) -> Unit) {
    val rhymes = LocalGraph.current.content.rhymes
    KidScreen("Rhymes", Mint, onBack) {
        LazyVerticalGrid(
            GridCells.Adaptive(150.dp),
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(rhymes) { rhyme ->
                Bouncy({ onPick(rhyme.id) }, Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().kidCard(20.dp)) {
                        AssetImage(rhyme.image, Modifier.fillMaxWidth().aspectRatio(1.5f), ContentScale.Crop)
                        Text(
                            rhyme.title,
                            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 15.sp, lineHeight = 18.sp, fontWeight = FontWeight.Bold, color = Ink,
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Full-screen rhyme. The lyrics are part of the picture and fill most of its height, so the
 * screen turns sideways and shows the whole picture; the player floats over its bottom edge
 * and fades away, and a tap anywhere brings it back.
 */
@Composable
fun RhymePlayerScreen(rhymeId: String, onBack: () -> Unit) {
    val graph = LocalGraph.current
    val rhymes = graph.content.rhymes
    var index by remember { mutableIntStateOf(rhymes.indexOfFirst { it.id == rhymeId }) }
    val rhyme = rhymes[index]
    var playing by remember { mutableStateOf(false) }
    var position by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    var duration by remember { mutableFloatStateOf(1f) }
    var controls by remember { mutableStateOf(true) }
    var lastTouch by remember { mutableIntStateOf(0) }

    LandscapeFullScreen()

    DisposableEffect(rhyme.id) {
        graph.sound.playRhyme(rhyme.audio)
        onDispose { graph.sound.stopRhyme() }
    }
    // Follow the player; it also pauses by itself when the app goes to the background.
    LaunchedEffect(rhyme.id, dragging) {
        while (!dragging) {
            duration = graph.sound.rhymeDurationMs.coerceAtLeast(1).toFloat()
            position = graph.sound.rhymePositionMs.toFloat()
            playing = graph.sound.rhymePlaying
            delay(200)
        }
    }
    // Keep the lyrics clear while the rhyme plays.
    LaunchedEffect(lastTouch, playing, dragging) {
        controls = true
        if (playing && !dragging) {
            delay(CONTROLS_MS)
            controls = false
        }
    }

    val touched = { lastTouch++; Unit }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(remember { MutableInteractionSource() }, indication = null, onClick = touched),
    ) {
        // Fills the bars beside the picture on phones wider than 3:2.
        AssetImage(rhyme.image, Modifier.fillMaxSize().blur(24.dp), ContentScale.Crop)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))
        AssetImage(rhyme.image, Modifier.fillMaxSize())

        AnimatedVisibility(controls, Modifier.fillMaxSize(), enter = fadeIn(), exit = fadeOut()) {
            Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(10.dp)) {
                RoundButton(Icons.AutoMirrored.Filled.ArrowBack, Scrim, onBack, Modifier.align(Alignment.TopStart))
                Row(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .widthIn(max = 560.dp)
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(Scrim)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    val step = { by: Int ->
                        touched()
                        index = (index + by + rhymes.size) % rhymes.size
                    }
                    RoundButton(Icons.AutoMirrored.Filled.KeyboardArrowLeft, Color.Transparent, { step(-1) }, size = 38.dp)
                    RoundButton(
                        if (playing) PauseIcon else Icons.Filled.PlayArrow, Mint,
                        {
                            touched()
                            when {
                                playing -> graph.sound.pauseRhyme()
                                position < duration - 300 -> graph.sound.resumeRhyme()
                                // Finished: play it again from the start.
                                else -> graph.sound.playRhyme(rhyme.audio)
                            }
                            playing = !playing
                        },
                        size = 44.dp,
                    )
                    RoundButton(Icons.AutoMirrored.Filled.KeyboardArrowRight, Color.Transparent, { step(1) }, size = 38.dp)
                    Text(clock(position), color = Color.White, fontSize = 13.sp)
                    Slider(
                        value = position.coerceAtMost(duration),
                        onValueChange = { dragging = true; position = it },
                        onValueChangeFinished = { graph.sound.seekRhyme(position.toInt()); dragging = false },
                        valueRange = 0f..duration,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Mint, inactiveTrackColor = Color.White.copy(alpha = 0.35f)),
                    )
                    Text(clock(duration), color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(end = 6.dp))
                }
            }
        }
    }
}

private val Scrim = Color.Black.copy(alpha = 0.55f)
private const val CONTROLS_MS = 3500L

/** Turns the screen sideways and hides the system bars until this screen is left. */
@Composable
private fun LandscapeFullScreen() {
    val activity = LocalContext.current as? Activity ?: return
    val view = LocalView.current
    DisposableEffect(Unit) {
        val bars = WindowCompat.getInsetsController(activity.window, view)
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        bars.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        bars.hide(WindowInsetsCompat.Type.systemBars())
        onDispose {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            bars.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

private fun clock(ms: Float): String {
    val s = (ms / 1000).toInt()
    return "%d:%02d".format(s / 60, s % 60)
}
