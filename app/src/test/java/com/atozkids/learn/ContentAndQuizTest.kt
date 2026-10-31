package com.atozkids.learn

import com.atozkids.learn.data.Content
import com.atozkids.learn.quiz.Memory
import com.atozkids.learn.quiz.QuestionType
import com.atozkids.learn.quiz.QuizEngine
import com.atozkids.learn.quiz.TraceGrid
import java.io.File
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentAndQuizTest {
    private val assets = File("src/main/assets")
    private val content = Content.parse(File(assets, Content.FILE).readText())

    @Test
    fun everyReferencedAssetExists() {
        val paths = content.categories.flatMap { c ->
            listOf(c.icon) + c.items.flatMap { listOf(it.tile, it.picture, it.audioMale, it.audioFemale) }
        } + content.rhymes.flatMap { listOf(it.image, it.audio) }
        val missing = paths.filter { !File(assets, it).let { f -> f.isFile && f.length() > 0 } }
        assertTrue("Missing assets: $missing", missing.isEmpty())
    }

    @Test
    fun contentHasExpectedShape() {
        assertEquals(8, content.categories.size)
        assertTrue(content.categories.all { it.items.size >= 4 })
        assertEquals(26, content.category("alphabet").items.size)
        assertTrue(content.category("alphabet").items.all { it.letter != null })
        assertEquals(content.categories.sumOf { it.items.size }, content.categories.flatMap { it.items }.map { it.id }.toSet().size)
    }

    @Test
    fun quizQuestionsHaveOneCorrectDistinctOption() {
        for (cat in content.categories) {
            val qs = QuizEngine.build(cat, Random(1))
            assertEquals(minOf(QuizEngine.QUESTIONS, cat.items.size), qs.size)
            for (q in qs) {
                assertEquals(QuizEngine.OPTIONS, q.options.size)
                assertEquals(q.options.size, q.options.map { it.id }.toSet().size)
                assertEquals(1, q.options.count { it.id == q.target.id })
                assertTrue(q.type != QuestionType.PICK_LETTER || cat.isAlphabet)
            }
            assertEquals(qs.size, qs.map { it.target.id }.toSet().size)
        }
    }

    @Test
    fun quizPromptsAreWrittenOutAndLettersMatch() {
        for (cat in content.categories) for (q in QuizEngine.build(cat, Random(2))) {
            assertTrue(q.prompt.isNotBlank())
            if (q.type == QuestionType.PICK_PICTURE) assertTrue(q.prompt, q.prompt.endsWith(q.target.name))
        }
        assertTrue(content.category("alphabet").items.all { it.name.startsWith(it.letter!!) })
    }

    @Test
    fun memoryDealsEachPictureTwice() {
        for (cat in content.categories) {
            val cards = Memory.deal(cat.items, Random(3))
            assertEquals(Memory.PAIRS * 2, cards.size)
            assertTrue(cards.groupBy { it.pair }.values.all { it.size == 2 })
            assertEquals(cards.indices.toList(), cards.map { it.id })
        }
    }

    @Test
    fun tracingNeedsTheShapeAndNotAScribble() {
        // A vertical bar nine cells wide down the middle of a 30 by 30 grid.
        val size = 30
        val bar = BooleanArray(size * size) { (it % size) in 11..19 }

        val traced = TraceGrid(size, bar)
        assertFalse(traced.done)
        for (y in 0 until size) traced.stamp(15f, y.toFloat())
        assertTrue(traced.done)

        val partly = TraceGrid(size, bar)
        for (y in 0 until 12) partly.stamp(15f, y.toFloat())
        assertFalse(partly.done)

        val scribble = TraceGrid(size, bar)
        for (y in 0 until size) for (x in 0 until size) scribble.stamp(x.toFloat(), y.toFloat())
        assertFalse(scribble.done)

        traced.clear()
        assertFalse(traced.done)
    }

    @Test
    fun starsScale() {
        assertEquals(3, QuizEngine.stars(10, 10))
        assertEquals(2, QuizEngine.stars(7, 10))
        assertEquals(1, QuizEngine.stars(2, 10))
    }
}
