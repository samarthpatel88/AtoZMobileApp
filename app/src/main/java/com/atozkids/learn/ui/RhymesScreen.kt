package com.atozkids.learn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atozkids.learn.LocalGraph
import kotlinx.coroutines.delay

@Composable
fun RhymesScreen(onBack: () -> Unit, onPick: (String) -> Unit) {
    val rhymes = LocalGraph.current.content.rhymes
    KidScreen("Rhymes", Mint, onBack) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(rhymes) { rhyme ->
                Bouncy({ onPick(rhyme.id) }, Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AssetImage(rhyme.image, Modifier.width(120.dp).aspectRatio(1.5f).clip(RoundedCornerShape(16.dp)), ContentScale.Crop)
                        Text(rhyme.title, Modifier.weight(1f).padding(horizontal = 14.dp), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                        RoundButton("▶", Mint, { onPick(rhyme.id) }, size = 48.dp, )
                    }
                }
            }
        }
    }
}

@Composable
fun RhymePlayerScreen(rhymeId: String, onBack: () -> Unit) {
    val graph = LocalGraph.current
    val rhyme = remember(rhymeId) { graph.content.rhyme(rhymeId) }
    var playing by remember { mutableStateOf(true) }
    var position by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    var duration by remember { mutableFloatStateOf(1f) }

    DisposableEffect(rhymeId) {
        graph.sound.playRhyme(rhyme.audio) { playing = false; position = 0f }
        onDispose { graph.sound.stopRhyme() }
    }
    // Follow the player while it is playing.
    LaunchedEffect(playing, dragging) {
        while (playing && !dragging) {
            duration = graph.sound.rhymeDurationMs.coerceAtLeast(1).toFloat()
            position = graph.sound.rhymePositionMs.toFloat()
            delay(200)
        }
    }

    KidScreen(rhyme.title, Mint, onBack) {
        Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                AssetImage(rhyme.image, Modifier.fillMaxSize())
            }
            Spacer(Modifier.height(12.dp))
            Slider(
                value = position,
                onValueChange = { dragging = true; position = it },
                onValueChangeFinished = { graph.sound.seekRhyme(position.toInt()); dragging = false },
                valueRange = 0f..duration,
                colors = SliderDefaults.colors(thumbColor = Mint, activeTrackColor = Mint),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(clock(position), color = Ink)
                Text(clock(duration), color = Ink)
            }
            Spacer(Modifier.height(8.dp))
            RoundButton(
                if (playing) "⏸" else "▶", Mint,
                {
                    if (playing) {
                        graph.sound.pauseRhyme(); playing = false
                    } else if (graph.sound.rhymeDurationMs > 0 && position < duration - 300) {
                        graph.sound.resumeRhyme(); playing = true
                    } else {
                        // Finished: play it again from the start.
                        graph.sound.playRhyme(rhyme.audio) { playing = false; position = 0f }
                        playing = true
                    }
                },
                size = 76.dp,
            )
        }
    }
}

private fun clock(ms: Float): String {
    val s = (ms / 1000).toInt()
    return "%d:%02d".format(s / 60, s % 60)
}
