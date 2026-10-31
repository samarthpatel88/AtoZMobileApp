package com.atozkids.learn.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atozkids.learn.LocalGraph
import com.atozkids.learn.R

@Composable
fun HomeScreen(onLearn: () -> Unit, onQuiz: () -> Unit, onRhymes: () -> Unit, onSettings: () -> Unit) {
    val graph = LocalGraph.current
    val float by rememberInfiniteTransition(label = "float").animateFloat(
        -6f, 6f, infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "y",
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFBDE3FF), Cream)))
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val wide = maxWidth > maxHeight
            val logo = @Composable {
                androidx.compose.foundation.Image(
                    painterResource(R.drawable.logo_atoz),
                    contentDescription = "AtoZ Kids",
                    modifier = Modifier
                        .graphicsLayer { translationY = float }
                        .then(if (wide) Modifier.fillMaxHeight(0.5f) else Modifier.height(150.dp)),
                )
            }
            val cards = @Composable { mod: (Modifier) -> Modifier ->
                MenuCard("Learn", "img/menu/learning.webp", Coral, onLearn, mod(Modifier))
                MenuCard("Quiz", "img/menu/quiz.webp", Sky, onQuiz, mod(Modifier))
                MenuCard("Rhymes", "img/menu/rhymes.webp", Mint, onRhymes, mod(Modifier))
            }
            if (wide) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.weight(0.38f), contentAlignment = Alignment.Center) { logo() }
                    Row(Modifier.weight(0.62f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        cards { Modifier.weight(1f).fillMaxHeight() }
                    }
                }
            } else {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    logo()
                    cards { Modifier.weight(1f).fillMaxWidth() }
                }
            }
        }
        StarsChip(graph.prefs.stars, Modifier.align(Alignment.TopStart))
        RoundButton("⚙", Grape, onSettings, Modifier.align(Alignment.TopEnd), size = 48.dp)
    }
}

@Composable
private fun MenuCard(label: String, image: String, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Bouncy(onClick, modifier) {
        Column(
            Modifier
                .fillMaxSize()
                .shadow(8.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.25f), Color.White)))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AssetImage(image, Modifier.weight(1f).fillMaxWidth())
            Text(label, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}
