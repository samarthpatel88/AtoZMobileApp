package com.atozkids.learn.ui

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.atozkids.learn.LocalGraph
import com.atozkids.learn.R
import com.atozkids.learn.quiz.Memory
import com.atozkids.learn.quiz.MemoryCard
import com.atozkids.learn.quiz.TraceGrid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlayScreen(onBack: () -> Unit, onMemory: () -> Unit, onTrace: () -> Unit) {
    KidScreen("Play", Orange, onBack) {
        BoxWithConstraints(Modifier.fillMaxSize().padding(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 14.dp)) {
            val cards = @Composable { mod: Modifier ->
                GameCard("🃏", "Memory match", "Find the pairs", Orange, onMemory, mod)
                GameCard("✏️", "Trace", "Letters and numbers", Sky, onTrace, mod)
            }
            if (maxWidth > maxHeight) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) { cards(Modifier.weight(1f).fillMaxHeight()) }
            } else {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) { cards(Modifier.weight(1f).fillMaxWidth()) }
            }
        }
    }
}

@Composable
private fun GameCard(emoji: String, title: String, detail: String, color: Color, onClick: () -> Unit, modifier: Modifier) {
    Bouncy(onClick, modifier) {
        Column(
            Modifier.fillMaxSize().kidCard(26.dp).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(emoji, fontSize = 64.sp)
            Text(title, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(detail, fontSize = 15.sp, color = Muted)
        }
    }
}

@Composable
fun MemoryScreen(categoryId: String, onBack: () -> Unit) {
    val graph = LocalGraph.current
    val category = remember(categoryId) { graph.content.category(categoryId) }
    val color = CategoryColors[graph.content.categories.indexOf(category) % CategoryColors.size]
    val scope = rememberCoroutineScope()

    var round by remember { mutableIntStateOf(0) }
    val cards = remember(round) { Memory.deal(category.items) }
    val open = remember(round) { mutableStateListOf<Int>() }
    val matched = remember(round) { mutableStateListOf<Int>() }
    var moves by remember(round) { mutableIntStateOf(0) }
    val finished = matched.size == cards.size
    // Six moves is the fewest possible, so three stars leaves room for a few misses.
    val stars = if (moves <= Memory.PAIRS + 3) 3 else if (moves <= Memory.PAIRS * 2 + 2) 2 else 1

    fun flip(card: MemoryCard) {
        if (open.size == 2 || card.id in open || card.id in matched) return
        open += card.id
        if (open.size < 2) return
        moves++
        val first = cards[open[0]]
        scope.launch {
            if (first.pair == card.pair) {
                graph.sound.speak(card.item.audio(graph.prefs.female))
                delay(350)
                matched += open
            } else {
                delay(900)
            }
            open.clear()
        }
    }

    KidScreen(
        "${category.title} match", color, onBack,
        actions = { if (!finished) Text("$moves moves", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink) },
    ) {
        if (finished) {
            LaunchedEffect(Unit) {
                graph.prefs.addStars(stars)
                graph.sound.sfx("cheer")
            }
            ResultPanel("You found them all!", "$moves moves", stars, color, onAgain = { round++ }, onDone = onBack)
        } else {
            BoxWithConstraints(Modifier.fillMaxSize().padding(start = 14.dp, end = 14.dp, bottom = 14.dp)) {
                val columns = if (maxWidth > maxHeight) 4 else 3
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cards.chunked(columns).forEach { row ->
                        Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { card ->
                                FlipCard(card, card.id in open || card.id in matched, card.id in matched, color, Modifier.weight(1f).fillMaxHeight()) { flip(card) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlipCard(card: MemoryCard, faceUp: Boolean, matched: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    val turn by animateFloatAsState(if (faceUp) 180f else 0f, label = "flip")
    Bouncy(onClick, modifier.graphicsLayer { rotationY = turn; cameraDistance = 16 * density }, click = false) {
        if (turn < 90f) {
            Box(Modifier.fillMaxSize().kidCard(16.dp, color), contentAlignment = Alignment.Center) {
                Text("?", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f; alpha = if (matched) 0.55f else 1f }
                    .kidCard(16.dp)
                    .padding(6.dp),
            ) { AssetImage(card.item.picture, Modifier.fillMaxSize()) }
        }
    }
}

private val TraceSymbols = (('A'..'Z') + ('0'..'9')).toList()
private const val GRID = 72

private fun tracePaint(context: android.content.Context, color: Int, gridSize: Float) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    typeface = ResourcesCompat.getFont(context, R.font.baloo2_bold)
    textAlign = Paint.Align.CENTER
    textSize = gridSize * 0.92f
    this.color = color
}

/** Draws [symbol] centred on a square canvas of side [size]; the page and the checker share this. */
private fun android.graphics.Canvas.drawSymbol(symbol: Char, size: Float, paint: Paint) {
    val metrics = paint.fontMetrics
    drawText(symbol.toString(), size / 2, size / 2 - (metrics.ascent + metrics.descent) / 2 - size * 0.04f, paint)
}

private fun traceGrid(context: android.content.Context, symbol: Char): TraceGrid {
    val bitmap = Bitmap.createBitmap(GRID, GRID, Bitmap.Config.ARGB_8888)
    android.graphics.Canvas(bitmap).drawSymbol(symbol, GRID.toFloat(), tracePaint(context, android.graphics.Color.BLACK, GRID.toFloat()))
    val pixels = IntArray(GRID * GRID)
    bitmap.getPixels(pixels, 0, GRID, 0, 0, GRID, GRID)
    bitmap.recycle()
    return TraceGrid(GRID, BooleanArray(pixels.size) { (pixels[it] ushr 24) > 128 })
}

@Composable
fun TraceScreen(onBack: () -> Unit) {
    val graph = LocalGraph.current
    val prefs = graph.prefs
    val context = LocalContext.current
    var index by remember { mutableIntStateOf(0) }
    val symbol = TraceSymbols[index]
    val strip = rememberLazyListState()
    LaunchedEffect(index) { strip.animateScrollToItem((index - 2).coerceAtLeast(0)) }

    KidScreen("Trace", Sky, onBack) {
        LazyRow(
            state = strip,
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(TraceSymbols) { s ->
                val selected = s == symbol
                Bouncy({ index = TraceSymbols.indexOf(s) }) {
                    Box(
                        Modifier
                            .size(36.dp)
                            .background(if (selected) Sky else if (s in prefs.traced) Mint else Color.White, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("$s", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = if (selected || s in prefs.traced) Color.White else Ink)
                    }
                }
            }
        }
        val grid = remember(symbol) { traceGrid(context, symbol) }
        val strokes = remember(symbol) { mutableStateListOf<List<Offset>>() }
        var current by remember(symbol) { mutableStateOf(emptyList<Offset>()) }
        var done by remember(symbol) { mutableStateOf(false) }

        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth().padding(14.dp), contentAlignment = Alignment.Center) {
            Canvas(
                Modifier
                    .size(minOf(maxWidth, maxHeight))
                    .kidCard(28.dp)
                    .pointerInput(symbol) {
                        val cell = size.width / GRID.toFloat()
                        fun touch(at: Offset) {
                            current = current + at
                            grid.stamp(at.x / cell, at.y / cell)
                        }
                        detectDragGestures(
                            onDragStart = { touch(it) },
                            onDrag = { change, _ -> touch(change.position) },
                            onDragEnd = {
                                strokes += current
                                current = emptyList()
                                if (!done && grid.done) {
                                    done = true
                                    if (symbol !in prefs.traced) prefs.addStars(1)
                                    prefs.markTraced(symbol)
                                    graph.sound.sfx("star")
                                }
                            },
                        )
                    },
            ) {
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawSymbol(symbol, size.width, tracePaint(context, (if (done) Mint else Color(0xFFDDE3F0)).copy(alpha = if (done) 0.35f else 1f).toArgb(), size.width))
                }
                val pen = Stroke(size.width * 0.05f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                (strokes + listOf(current)).filter { it.isNotEmpty() }.forEach { line ->
                    val path = Path().apply {
                        moveTo(line[0].x, line[0].y)
                        line.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(path, if (done) Mint else Sky, style = pen)
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoundButton(Icons.AutoMirrored.Filled.KeyboardArrowLeft, Sky, { index-- }, size = 52.dp, enabled = index > 0)
            RoundButton(Icons.Filled.Refresh, Orange, {
                strokes.clear()
                grid.clear()
                done = false
            }, size = 52.dp)
            RoundButton(Icons.AutoMirrored.Filled.KeyboardArrowRight, Sky, { index++ }, size = 52.dp, enabled = index < TraceSymbols.size - 1)
        }
    }
}
