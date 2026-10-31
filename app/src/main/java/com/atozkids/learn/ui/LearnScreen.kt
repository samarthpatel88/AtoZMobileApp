package com.atozkids.learn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atozkids.learn.LocalGraph
import com.atozkids.learn.data.Category
import com.atozkids.learn.data.Item
import kotlinx.coroutines.launch

@Composable
fun LearnScreen(categoryId: String, onBack: () -> Unit) {
    val graph = LocalGraph.current
    val category = remember(categoryId) { graph.content.category(categoryId) }
    val color = CategoryColors[graph.content.categories.indexOf(category) % CategoryColors.size]
    val pager = rememberPagerState { category.items.size }
    val scope = rememberCoroutineScope()

    // Say the word whenever a page settles (and again if the voice is switched).
    LaunchedEffect(pager.settledPage, graph.prefs.female) {
        graph.sound.speak(category.items[pager.settledPage].audio(graph.prefs.female))
    }

    KidScreen(
        category.title, color, onBack,
        actions = {
            Bouncy({ graph.prefs.updateFemale(!graph.prefs.female) }) {
                Text(
                    if (graph.prefs.female) "👧" else "👦",
                    Modifier.background(Color.White, CircleShape).padding(horizontal = 9.dp, vertical = 3.dp),
                    fontSize = 24.sp,
                )
            }
        },
    ) {
        HorizontalPager(pager, Modifier.weight(1f).fillMaxWidth(), pageSpacing = 12.dp, contentPadding = PaddingValues(horizontal = 14.dp)) { page ->
            val item = category.items[page]
            ItemCard(category, item, color) { graph.sound.speak(item.audio(graph.prefs.female)) }
        }
        LinearProgressIndicator(
            progress = { (pager.currentPage + 1) / category.items.size.toFloat() },
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 8.dp).height(6.dp).clip(CircleShape),
            color = color,
            trackColor = Color.White,
            drawStopIndicator = {},
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoundButton(Icons.AutoMirrored.Filled.KeyboardArrowLeft, color, { scope.launch { pager.animateScrollToPage(pager.currentPage - 1) } }, size = 52.dp, enabled = pager.currentPage > 0)
            Text("${pager.currentPage + 1} / ${category.items.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink)
            RoundButton(VolumeIcon, Sun, { graph.sound.speak(category.items[pager.currentPage].audio(graph.prefs.female)) }, size = 56.dp)
            RoundButton(Icons.AutoMirrored.Filled.KeyboardArrowRight, color, { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } }, size = 52.dp, enabled = pager.currentPage < category.items.size - 1)
        }
    }
}

@Composable
private fun ItemCard(category: Category, item: Item, color: Color, onTap: () -> Unit) {
    Bouncy(onTap, Modifier.fillMaxSize().padding(vertical = 4.dp), click = false) {
        BoxWithConstraints(Modifier.fillMaxSize().kidCard(28.dp).padding(14.dp)) {
            val wide = maxWidth > maxHeight
            if (category.isAlphabet) {
                // Letter tile beside the picture-with-word card.
                if (wide) {
                    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        AssetImage(item.tile, Modifier.weight(1f).fillMaxSize())
                        AssetImage(item.picture, Modifier.weight(1f).fillMaxSize())
                    }
                } else {
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        AssetImage(item.tile, Modifier.weight(1f).fillMaxWidth())
                        AssetImage(item.picture, Modifier.weight(1f).fillMaxWidth())
                    }
                }
            } else if (wide) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    AssetImage(item.picture, Modifier.weight(1.3f).fillMaxSize())
                    Text(item.name, Modifier.weight(1f), fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = color, lineHeight = 46.sp)
                }
            } else {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AssetImage(item.picture, Modifier.weight(1f).fillMaxWidth())
                    Text(item.name, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = color)
                }
            }
        }
    }
}
