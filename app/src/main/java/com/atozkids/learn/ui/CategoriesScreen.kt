package com.atozkids.learn.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atozkids.learn.LocalGraph

/** Picks a category to learn, be quizzed on or play with, depending on [mode]. */
@Composable
fun CategoriesScreen(mode: String, onBack: () -> Unit, onPick: (String) -> Unit) {
    val graph = LocalGraph.current
    val (title, color) = when (mode) {
        "learn" -> "What to learn?" to Coral
        "quiz" -> "Pick a quiz" to Sky
        else -> "Memory match" to Orange
    }
    KidScreen(title, color, onBack) {
        LazyVerticalGrid(
            GridCells.Adaptive(104.dp),
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(graph.content.categories) { cat ->
                Bouncy({ onPick(cat.id) }, Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.fillMaxWidth().kidCard(20.dp).padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // The icon artwork already shows the category's name.
                        AssetImage(cat.icon, Modifier.fillMaxWidth().aspectRatio(0.82f))
                        if (mode == "quiz") StarRow(graph.prefs.bestStars(cat.id), Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}
