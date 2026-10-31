package com.atozkids.learn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atozkids.learn.LocalGraph
import com.atozkids.learn.quiz.QuizEngine

/** Picks what to learn or be quizzed on. [learn] decides the title and colour. */
@Composable
fun CategoriesScreen(learn: Boolean, onBack: () -> Unit, onPick: (String) -> Unit) {
    val categories = LocalGraph.current.content.categories
    KidScreen(if (learn) "What to learn?" else "Pick a quiz", if (learn) Coral else Sky, onBack) {
        LazyVerticalGrid(
            GridCells.Adaptive(150.dp),
            Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            itemsIndexed(categories) { index, cat ->
                val color = CategoryColors[index % CategoryColors.size]
                Bouncy({ onPick(cat.id) }, Modifier.fillMaxWidth().aspectRatio(0.85f)) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .shadow(6.dp, RoundedCornerShape(28.dp))
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White)
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AssetImage(cat.icon, Modifier.weight(1f).fillMaxWidth())
                        Text(cat.title, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = color, modifier = Modifier.padding(top = 6.dp))
                        Text(if (learn) "${cat.items.size} to learn" else "${minOf(QuizEngine.QUESTIONS, cat.items.size)} questions", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
