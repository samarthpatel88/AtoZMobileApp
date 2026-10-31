package com.atozkids.learn.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atozkids.learn.LocalGraph
import com.atozkids.learn.R

@Composable
fun HomeScreen(onLearn: () -> Unit, onQuiz: () -> Unit, onRhymes: () -> Unit, onPlay: () -> Unit, onSettings: () -> Unit) {
    val graph = LocalGraph.current
    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(0f to Color(0xFFBDE3FF), 0.6f to Cream))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            StarsChip(graph.prefs.stars)
            Spacer(Modifier.weight(1f))
            RoundButton(Icons.Filled.Settings, Grape, onSettings)
        }
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
            val wide = maxWidth > maxHeight
            val logoHeight = maxHeight * 0.2f
            val logo = @Composable { mod: Modifier ->
                Image(painterResource(R.drawable.logo_atoz), contentDescription = "AtoZ Kids", modifier = mod)
            }
            val learn = @Composable { mod: Modifier -> MenuCard(Coral, onLearn, mod) { AssetImage("img/menu/learning.webp", Modifier.fillMaxSize()) } }
            val quiz = @Composable { mod: Modifier -> MenuCard(Sky, onQuiz, mod) { AssetImage("img/menu/quiz.webp", Modifier.fillMaxSize()) } }
            val rhymes = @Composable { mod: Modifier -> MenuCard(Mint, onRhymes, mod) { AssetImage("img/menu/rhymes.webp", Modifier.fillMaxSize()) } }
            val play = @Composable { mod: Modifier -> MenuCard(Orange, onPlay, mod) { PlayArt() } }
            if (wide) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    logo(Modifier.weight(0.3f))
                    Row(Modifier.weight(0.7f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val mod = Modifier.weight(1f).fillMaxHeight()
                        learn(mod); quiz(mod); rhymes(mod); play(mod)
                    }
                }
            } else {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    logo(Modifier.height(logoHeight))
                    Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val mod = Modifier.weight(1f).fillMaxHeight()
                        learn(mod); quiz(mod)
                    }
                    Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val mod = Modifier.weight(1f).fillMaxHeight()
                        rhymes(mod); play(mod)
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuCard(color: Color, onClick: () -> Unit, modifier: Modifier, art: @Composable () -> Unit) {
    Bouncy(onClick, modifier) {
        Box(
            Modifier
                .fillMaxSize()
                .kidCard(26.dp)
                .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.32f), Color.White)))
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) { art() }
    }
}

/** The other menu cards carry their label in the artwork; this one has no artwork yet. */
@Composable
private fun PlayArt() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🧩", fontSize = 64.sp)
        Text("Play", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Orange)
    }
}
