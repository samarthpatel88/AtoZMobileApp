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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atozkids.learn.LocalGraph
import com.atozkids.learn.R
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
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    enabled: Boolean = true,
) {
    Bouncy(onClick, modifier.size(size), enabled) {
        Box(
            Modifier
                .fillMaxSize()
                .background(if (enabled) color else Color(0xFFDADCE4), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, Modifier.size(size * 0.58f), tint = Color.White)
        }
    }
}

val PauseIcon @Composable get() = ImageVector.vectorResource(R.drawable.ic_pause)
val VolumeIcon @Composable get() = ImageVector.vectorResource(R.drawable.ic_volume)

/** The white rounded card used across the app. */
fun Modifier.kidCard(radius: Dp = 22.dp, color: Color = Color.White): Modifier {
    val shape = RoundedCornerShape(radius)
    return shadow(3.dp, shape, ambientColor = Ink.copy(alpha = 0.25f), spotColor = Ink.copy(alpha = 0.25f))
        .clip(shape)
        .background(color)
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
            .background(Brush.verticalGradient(0f to color.copy(alpha = 0.22f), 0.45f to Cream))
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (onBack != null) RoundButton(Icons.AutoMirrored.Filled.ArrowBack, color, onBack)
            Text(
                title,
                Modifier.weight(1f),
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                fontSize = 24.sp,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            actions()
        }
        content()
    }
}

@Composable
fun StarsChip(count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier.background(Color.White, CircleShape).padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(Icons.Filled.Star, contentDescription = null, Modifier.size(20.dp), tint = Sun)
        Text("$count", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
    }
}

/** A row of [total] stars, the first [filled] of them gold. */
@Composable
fun StarRow(filled: Int, modifier: Modifier = Modifier, total: Int = 3, size: Dp = 16.dp) {
    Row(modifier) {
        repeat(total) { i ->
            Icon(Icons.Filled.Star, contentDescription = null, Modifier.size(size), tint = if (i < filled) Sun else Color(0xFFE4E5EC))
        }
    }
}

/** Pill-shaped text button. */
@Composable
fun BigButton(text: String, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Bouncy(onClick, modifier) {
        Text(
            text,
            Modifier.background(color, CircleShape).padding(horizontal = 24.dp, vertical = 9.dp),
            color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold,
        )
    }
}

/** End-of-game panel with stars, used by the quiz and the memory game. */
@Composable
fun ResultPanel(title: String, detail: String, stars: Int, color: Color, onAgain: () -> Unit, onDone: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            StarRow(stars, size = 60.dp)
            Spacer(Modifier.size(6.dp))
            Text(title, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(detail, fontSize = 18.sp, color = Ink, textAlign = TextAlign.Center)
            Spacer(Modifier.size(18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BigButton("Play again", color, onAgain)
                BigButton("Done", Grape, onDone)
            }
        }
        if (stars >= 2) Confetti()
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
    Text(text, modifier, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Ink, textAlign = TextAlign.Start)
}
