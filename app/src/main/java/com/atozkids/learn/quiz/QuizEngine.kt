package com.atozkids.learn.quiz

import com.atozkids.learn.data.Category
import com.atozkids.learn.data.Item
import kotlin.random.Random

enum class QuestionType {
    /** A word is spoken; tap the matching picture. */
    PICK_PICTURE,

    /** A picture is shown; tap its name. */
    PICK_NAME,

    /** Alphabet only: a picture is shown; tap the letter it starts with. */
    PICK_LETTER,
}

class Question(val type: QuestionType, val target: Item, val options: List<Item>)

/** Builds quizzes from a category's local items; nothing is stored or fetched. */
object QuizEngine {
    const val QUESTIONS = 10
    const val OPTIONS = 4

    fun build(category: Category, random: Random = Random.Default, size: Int = QUESTIONS): List<Question> {
        val targets = category.items.shuffled(random).take(size)
        return targets.mapIndexed { i, target ->
            val others = category.items.filter { it.id != target.id }.shuffled(random).take(OPTIONS - 1)
            val type = when {
                i % 2 == 0 -> QuestionType.PICK_PICTURE
                category.isAlphabet -> QuestionType.PICK_LETTER
                else -> QuestionType.PICK_NAME
            }
            Question(type, target, (others + target).shuffled(random))
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
