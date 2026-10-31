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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.shadow
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

    KidScreen(
        "${category.title} quiz", color, onBack,
        actions = { StarsChip(graph.prefs.stars) },
    ) {
        if (finished) {
            ResultView(score, questions.size, color, onAgain = { round++ }, onHome = onBack)
        } else {
            LinearProgressIndicator(
                progress = { index / questions.size.toFloat() },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = color,
                trackColor = Color.White,
            )
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
    val graph = LocalGraph.current
    val scope = rememberCoroutineScope()
    var wrong by remember(q) { mutableStateOf(setOf<String>()) }
    var solved by remember(q) { mutableStateOf(false) }

    fun speakTarget() = graph.sound.speak(q.target.audio(graph.prefs.female))

    // A "pick the picture" question is asked out loud.
    LaunchedEffect(q) { if (q.type == QuestionType.PICK_PICTURE) speakTarget() }

    fun answer(option: Item) {
        if (solved || option.id in wrong) return
        if (option.id == q.target.id) {
            solved = true
            graph.sound.sfx("success")
            speakTarget()
            scope.launch {
                delay(1500)
                onDone(wrong.isEmpty())
            }
        } else {
            wrong = wrong + option.id
            graph.sound.sfx("fail")
        }
    }

    val prompt = when (q.type) {
        QuestionType.PICK_PICTURE -> "Tap the ${q.target.name}"
        QuestionType.PICK_NAME -> "What is this?"
        QuestionType.PICK_LETTER -> "Which letter does ${q.target.name} start with?"
    }

    BoxWithConstraints(Modifier.fillMaxSize().padding(16.dp)) {
        val wide = maxWidth > maxHeight
        val header = @Composable { mod: Modifier ->
            Column(mod, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(prompt, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Ink, textAlign = TextAlign.Center)
                Spacer(Modifier.height(10.dp))
                if (q.type == QuestionType.PICK_PICTURE) {
                    RoundButton("🔊", Sun, { speakTarget() }, size = 72.dp)
                } else {
                    AssetImage(q.target.picture, Modifier.weight(1f, fill = true).fillMaxWidth())
                }
            }
        }
        val options = @Composable { mod: Modifier ->
            Column(mod, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                q.options.chunked(2).forEach { pair ->
                    Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                header(Modifier.weight(0.8f).fillMaxHeight())
                options(Modifier.weight(1.2f).fillMaxHeight())
            }
        } else {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                header(Modifier.weight(0.9f).fillMaxWidth())
                options(Modifier.weight(1.3f).fillMaxWidth())
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
        if (state == OptionState.Right) 1.06f else 1f,
        spring(Spring.DampingRatioMediumBouncy), label = "pop",
    )
    Bouncy(onClick, modifier.graphicsLayer { scaleX = pop; scaleY = pop }, click = false) {
        Box(
            Modifier
                .fillMaxSize()
                .shadow(5.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(if (state == OptionState.Wrong) Color(0xFFFFE3E3) else Color.White)
                .border(5.dp, border, RoundedCornerShape(24.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (q.type) {
                QuestionType.PICK_PICTURE -> AssetImage(option.picture, Modifier.fillMaxSize())
                QuestionType.PICK_NAME -> Text(option.name, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = color, textAlign = TextAlign.Center)
                QuestionType.PICK_LETTER -> Text(option.letter ?: "", fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, color = color)
            }
        }
    }
}

@Composable
private fun ResultView(score: Int, total: Int, color: Color, onAgain: () -> Unit, onHome: () -> Unit) {
    val graph = LocalGraph.current
    val stars = QuizEngine.stars(score, total)
    LaunchedEffect(Unit) {
        graph.prefs.addStars(score)
        graph.sound.sfx("cheer")
    }
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i -> PopStar(filled = i < stars, delayMs = 250L * i) }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                when (stars) { 3 -> "Superstar!"; 2 -> "Great job!"; else -> "Good try!" },
                fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = color,
            )
            Text("You got $score of $total right", fontSize = 22.sp, color = Ink)
            Text("+$score ⭐", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Sun)
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BigButton("Play again", color, onAgain)
                BigButton("Done", Grape, onHome)
            }
        }
        if (stars >= 2) Confetti()
    }
}

@Composable
private fun PopStar(filled: Boolean, delayMs: Long) {
    var shown by remember { mutableStateOf(false) }
    val graph = LocalGraph.current
    LaunchedEffect(Unit) {
        delay(delayMs)
        shown = true
        if (filled) graph.sound.sfx("star")
    }
    val scale by animateFloatAsState(if (shown) 1f else 0f, spring(Spring.DampingRatioHighBouncy, Spring.StiffnessLow), label = "star")
    Text(
        if (filled) "⭐" else "☆",
        fontSize = 64.sp,
        color = Color.Gray,
        modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale },
    )
}

@Composable
fun BigButton(text: String, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Bouncy(onClick, modifier) {
        Text(
            text,
            Modifier.shadow(4.dp, RoundedCornerShape(50)).background(color, RoundedCornerShape(50)).padding(horizontal = 28.dp, vertical = 14.dp),
            color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold,
        )
    }
}
