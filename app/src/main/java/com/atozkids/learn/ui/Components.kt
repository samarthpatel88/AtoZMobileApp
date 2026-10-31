package com.atozkids.learn.ui

import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atozkids.learn.LocalGraph
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Decoded asset images, kept in memory (up to ~24 MB) so paging back and forth is instant. */
object ImageCache {
    private val cache = object : LruCache<String, ImageBitmap>(24 * 1024 * 1024) {
        override fun sizeOf(key: String, value: ImageBitmap) = value.width * value.height * 4
    }

    fun peek(path: String): ImageBitmap? = cache.get(path)

    fun load(context: android.content.Context, path: String): ImageBitmap? =
        cache.get(path) ?: runCatching {
            context.assets.open(path).use { BitmapFactory.decodeStream(it) }?.asImageBitmap()
        }.getOrNull()?.also { cache.put(path, it) }
}

@Composable
fun AssetImage(path: String, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Fit) {
    val context = LocalContext.current
    val bitmap by produceState(ImageCache.peek(path), path) {
        value = withContext(Dispatchers.IO) { ImageCache.load(context, path) }
    }
    bitmap?.let { Image(it, contentDescription = null, modifier = modifier, contentScale = contentScale) }
        ?: Box(modifier)
}

/** Anything tappable in the app: springs down when pressed and plays the click sound. */
@Composable
fun Bouncy(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    click: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val sound = LocalGraph.current.sound
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) 0.92f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press",
    )
    Box(
        modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = source, indication = null, enabled = enabled) {
                if (click) sound.sfx("click")
                onClick()
            },
        content = content,
    )
}

@Composable
fun RoundButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    enabled: Boolean = true,
) {
    Bouncy(onClick, modifier.size(size), enabled) {
        Box(
            Modifier
                .fillMaxSize()
                .background(if (enabled) color else Color(0xFFCFCFCF), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(label, color = Color.White, fontSize = (size.value * 0.46f).sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

/** Common page frame: tinted gradient, safe-area padding, optional back button and title. */
@Composable
fun KidScreen(
    title: String,
    color: Color,
    onBack: (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.40f), Cream)))
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                RoundButton("←", color, onBack)
                Spacer(Modifier.width(14.dp))
            }
            Text(
                title,
                Modifier.weight(1f),
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                color = Ink,
                maxLines = 1,
            )
            actions()
        }
        content()
    }
}

@Composable
fun StarsChip(count: Int, modifier: Modifier = Modifier) {
    Box(modifier.background(Color.White, CircleShape).padding(horizontal = 14.dp, vertical = 6.dp)) {
        Text("⭐ $count", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
    }
}

private class Piece(val x: Float, val y0: Float, val speed: Float, val sway: Float, val phase: Float, val size: Float, val color: Color, val spin: Float)

/** Falling confetti, drawn on a Canvas. Put it over the content and let it run. */
@Composable
fun Confetti(modifier: Modifier = Modifier) {
    val palette = listOf(Coral, Sky, Sun, Mint, Grape, Pink, Orange)
    val pieces = remember {
        val r = Random(42)
        List(70) {
            Piece(r.nextFloat(), r.nextFloat(), 0.5f + r.nextFloat() * 0.9f, 0.5f + r.nextFloat() * 2f,
                r.nextFloat() * 6.28f, 10f + r.nextFloat() * 12f, palette[r.nextInt(palette.size)], 200f + r.nextFloat() * 400f)
        }
    }
    val t by rememberInfiniteTransition(label = "confetti").animateFloat(
        0f, 1f, infiniteRepeatable(tween(4500, easing = LinearEasing), RepeatMode.Restart), label = "t",
    )
    Canvas(modifier.fillMaxSize()) {
        for (p in pieces) {
            val y = ((p.y0 + t * p.speed) % 1f) * (size.height + 40f) - 20f
            val x = p.x * size.width + sin(t * 6.28f * p.sway + p.phase) * 36f
            val angle = t * p.spin
            // Rotate each piece around its own centre.
            val cx = x + p.size / 2
            val cy = y + p.size / 3
            rotate(angle, Offset(cx, cy)) {
                drawRect(p.color, Offset(x, y), Size(p.size, p.size * 0.6f))
            }
        }
    }
}

/**
 * Asks a simple sum before anything that leaves the app (sharing, e-mail), so a small child
 * cannot wander out by accident.
 */
@Composable
fun ParentGate(onDismiss: () -> Unit, onPass: () -> Unit) {
    val (a, b) = remember { Random.nextInt(6, 10) to Random.nextInt(6, 10) }
    val options = remember {
        val answer = a + b
        generateSequence { answer + Random.nextInt(-4, 5) }.filter { it != answer && it > 0 }
            .distinct().take(3).toList().plus(answer).shuffled()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("For grown-ups", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("What is $a + $b ?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.size(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEach { n ->
                        androidx.compose.material3.Button(onClick = { if (n == a + b) onPass() else onDismiss() }) {
                            Text("$n", fontSize = 20.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onDismiss) { Text("Cancel") } },
    )
}

@Composable
fun Heading(text: String, modifier: Modifier = Modifier) {
    Text(text, modifier, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Ink, textAlign = TextAlign.Start)
}
