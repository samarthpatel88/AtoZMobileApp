package com.atozkids.learn.quiz

import com.atozkids.learn.data.Item
import kotlin.random.Random

/** One face-down card; two cards with the same [pair] match. */
class MemoryCard(val id: Int, val item: Item) {
    val pair get() = item.id
}

object Memory {
    const val PAIRS = 6

    fun deal(items: List<Item>, random: Random = Random.Default, pairs: Int = PAIRS): List<MemoryCard> =
        items.shuffled(random).take(pairs)
            .flatMap { listOf(it, it) }
            .shuffled(random)
            .mapIndexed { i, item -> MemoryCard(i, item) }
}

/**
 * Checks tracing on a small square grid. The grid holds the letter's shape; finger touches
 * stamp cells around them. Finished means most of the letter was covered without scribbling
 * all over the page.
 */
class TraceGrid(val size: Int, private val shape: BooleanArray) {
    private val stamped = BooleanArray(shape.size)

    fun stamp(x: Float, y: Float) {
        val cx = x.toInt()
        val cy = y.toInt()
        for (row in cy - RADIUS..cy + RADIUS) for (col in cx - RADIUS..cx + RADIUS) {
            if (row in 0 until size && col in 0 until size) stamped[row * size + col] = true
        }
    }

    fun clear() = stamped.fill(false)

    val done: Boolean
        get() {
            val covered = shape.indices.count { shape[it] && stamped[it] }
            val outside = shape.indices.count { !shape[it] && stamped[it] }
            return covered >= shape.count { it } * COVER && outside <= covered * MAX_OUTSIDE
        }

    companion object {
        private const val RADIUS = 4
        private const val COVER = 0.75
        private const val MAX_OUTSIDE = 1.2
    }
}
