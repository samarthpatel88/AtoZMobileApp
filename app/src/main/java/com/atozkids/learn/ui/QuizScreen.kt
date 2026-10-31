package com.atozkids.learn.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atozkids.learn.LocalGraph
import com.atozkids.learn.data.Item
import com.atozkids.learn.quiz.Question
import com.atozkids.learn.quiz.QuestionType
import com.atozkids.learn.quiz.QuizEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(categoryId: String, onBack: () -> Unit) {
    val graph = LocalGraph.current
    val category = remember(categoryId) { graph.content.category(categoryId) }
    val color = CategoryColors[graph.content.categories.indexOf(category) % CategoryColors.size]

    var round by remember { mutableIntStateOf(0) }
    val questions = remember(categoryId, round) { QuizEngine.build(category) }
    var index by remember(round) { mutableIntStateOf(0) }
    var score by remember(round) { mutableIntStateOf(0) }
    val finished = index >= questions.size

    DisposableEffect(Unit) {
        graph.sound.silence(true)
        onDispose { graph.sound.silence(false) }
    }

    KidScreen(
        "${category.title} quiz", color, onBack,
        actions = { StarsChip(graph.prefs.stars) },
    ) {
        if (finished) {
            val stars = QuizEngine.stars(score, questions.size)
            LaunchedEffect(Unit) {
                graph.prefs.addStars(score)
                graph.prefs.recordQuiz(categoryId, stars)
            }
            ResultPanel(
                title = when (stars) { 3 -> "Superstar!"; 2 -> "Great job!"; else -> "Good try!" },
                detail = "You got $score of ${questions.size} right",
                stars = stars,
                color = color,
                onAgain = { round++ },
                onDone = onBack,
            )
        } else {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { index / questions.size.toFloat() },
                    modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape),
                    color = color,
                    trackColor = Color.White,
                    drawStopIndicator = {},
                )
                Text("${index + 1}/${questions.size}", Modifier.padding(start = 10.dp), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Ink)
            }
            AnimatedContent(
                targetState = index,
                transitionSpec = {
                    (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
                },
                modifier = Modifier.weight(1f).fillMaxWidth(),
                label = "question",
            ) { i ->
                QuestionView(questions[i], color) { firstTry ->
                    if (firstTry) score++
                    index = i + 1
                }
            }
        }
    }
}

@Composable
private fun QuestionView(q: Question, color: Color, onDone: (firstTry: Boolean) -> Unit) {
    val scope = rememberCoroutineScope()
    var wrong by remember(q) { mutableStateOf(setOf<String>()) }
    var solved by remember(q) { mutableStateOf(false) }

    fun answer(option: Item) {
        if (solved || option.id in wrong) return
        if (option.id == q.target.id) {
            solved = true
            scope.launch {
                delay(900)
                onDone(wrong.isEmpty())
            }
        } else {
            wrong = wrong + option.id
        }
    }

    // Picture options need the room; the other types show the target picture above them.
    val showsPicture = q.type != QuestionType.PICK_PICTURE

    BoxWithConstraints(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 10.dp)) {
        val wide = maxWidth > maxHeight
        val question = @Composable {
            Text(q.prompt, Modifier.fillMaxWidth(), fontSize = 24.sp, lineHeight = 28.sp, fontWeight = FontWeight.ExtraBold, color = Ink, textAlign = TextAlign.Center)
        }
        val picture = @Composable { mod: Modifier -> AssetImage(q.target.picture, mod.kidCard(24.dp).padding(10.dp)) }
        val options = @Composable { mod: Modifier ->
            Column(mod, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                q.options.chunked(2).forEach { pair ->
                    Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        pair.forEach { option ->
                            OptionTile(q, option, color, state = when {
                                solved && option.id == q.target.id -> OptionState.Right
                                option.id in wrong -> OptionState.Wrong
                                else -> OptionState.Idle
                            }, modifier = Modifier.weight(1f).fillMaxHeight()) { answer(option) }
                        }
                    }
                }
            }
        }
        if (wide) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(Modifier.weight(0.8f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)) {
                    question()
                    if (showsPicture) picture(Modifier.weight(1f).fillMaxWidth())
                }
                options(Modifier.weight(1.2f).fillMaxHeight())
            }
        } else {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                question()
                if (showsPicture) {
                    picture(Modifier.weight(1.1f).fillMaxWidth())
                    options(Modifier.weight(0.9f).fillMaxWidth())
                } else {
                    options(Modifier.weight(1f).fillMaxWidth())
                }
            }
        }
    }
}

private enum class OptionState { Idle, Right, Wrong }

@Composable
private fun OptionTile(q: Question, option: Item, color: Color, state: OptionState, modifier: Modifier, onClick: () -> Unit) {
    val border = when (state) {
        OptionState.Right -> Mint
        OptionState.Wrong -> Coral
        OptionState.Idle -> Color.Transparent
    }
    val pop by animateFloatAsState(
        if (state == OptionState.Right) 1.05f else 1f,
        spring(Spring.DampingRatioMediumBouncy), label = "pop",
    )
    Bouncy(onClick, modifier.graphicsLayer { scaleX = pop; scaleY = pop }, click = false) {
        Box(
            Modifier
                .fillMaxSize()
                .kidCard(20.dp, if (state == OptionState.Wrong) Color(0xFFFFE9E9) else Color.White)
                .border(4.dp, border, RoundedCornerShape(20.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (q.type) {
                QuestionType.PICK_PICTURE -> AssetImage(option.picture, Modifier.fillMaxSize())
                QuestionType.PICK_NAME -> Text(option.name, fontSize = 22.sp, lineHeight = 25.sp, fontWeight = FontWeight.ExtraBold, color = color, textAlign = TextAlign.Center)
                QuestionType.PICK_LETTER -> Text(option.letter ?: "", fontSize = 44.sp, fontWeight = FontWeight.ExtraBold, color = color)
            }
        }
    }
}
