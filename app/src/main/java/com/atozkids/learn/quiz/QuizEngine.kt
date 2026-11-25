package com.atozkids.learn.quiz

import com.atozkids.learn.data.Category
import com.atozkids.learn.data.Item
import kotlin.random.Random

enum class QuestionType {
    /** A word is shown; tap the matching picture. */
    PICK_PICTURE,

    /** A picture is shown; tap its name. */
    PICK_NAME,

    /** Alphabet only: a picture is shown; tap the letter it starts with. */
    PICK_LETTER,

    /** Months and days only: one is shown; tap the one that comes after it. */
    PICK_NEXT,
}

/** [cue] is the picture shown with the question: the target itself, or for [QuestionType.PICK_NEXT] the one before it. */
class Question(val type: QuestionType, val target: Item, val options: List<Item>, val prompt: String, val cue: Item = target)

/** Builds quizzes from a category's local items; nothing is stored or fetched. */
object QuizEngine {
    const val QUESTIONS = 10
    const val OPTIONS = 4

    /** Categories in a fixed cycle. Their pictures show the name, so asking for it would be too easy. */
    private val ORDERED = setOf("months", "weekdays")

    fun build(category: Category, random: Random = Random.Default, size: Int = QUESTIONS): List<Question> {
        val items = category.items
        val targets = items.shuffled(random).take(size)
        return targets.mapIndexed { i, target ->
            val type = when {
                i % 2 == 0 -> QuestionType.PICK_PICTURE
                category.isAlphabet -> QuestionType.PICK_LETTER
                category.id in ORDERED -> QuestionType.PICK_NEXT
                else -> QuestionType.PICK_NAME
            }
            // After December comes January again.
            val cue = if (type == QuestionType.PICK_NEXT) items[(items.indexOf(target) - 1 + items.size) % items.size] else target
            val others = items.filter { it.id != target.id && it.id != cue.id }.shuffled(random).take(OPTIONS - 1)
            Question(type, target, (others + target).shuffled(random), prompt(type, target, cue, category), cue)
        }
    }

    private fun prompt(type: QuestionType, target: Item, cue: Item, category: Category) = when (type) {
        QuestionType.PICK_LETTER -> "Which letter does ${target.name} start with?"
        QuestionType.PICK_NEXT -> "Which ${if (category.id == "months") "month" else "day"} comes after ${cue.name}?"
        QuestionType.PICK_PICTURE -> when (category.id) {
            "colors" -> "Find the colour ${target.name}"
            "numbers" -> "Find the number ${target.name}"
            "months", "weekdays", "sports" -> "Find ${target.name}"
            else -> "Find the ${target.name}"
        }
        QuestionType.PICK_NAME -> when (category.id) {
            "colors" -> "What colour is this?"
            "numbers" -> "What number is this?"
            "shapes" -> "What shape is this?"
            "sports" -> "What sport is this?"
            "helpers" -> "Who is this?"
            "lines" -> "What line is this?"
            else -> "What is this?"
        }
    }

    /** 1 to 3 stars for a finished quiz. */
    fun stars(score: Int, total: Int): Int {
        val ratio = if (total == 0) 0.0 else score.toDouble() / total
        return when {
            ratio >= 0.9 -> 3
            ratio >= 0.6 -> 2
            else -> 1
        }
    }
}
