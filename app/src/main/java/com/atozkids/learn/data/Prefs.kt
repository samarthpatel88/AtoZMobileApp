package com.atozkids.learn.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Small settings store. Values are Compose state, so screens update when they change. */
class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("atoz", Context.MODE_PRIVATE)

    var sfx by mutableStateOf(sp.getBoolean("sfx", true))
        private set
    var music by mutableStateOf(sp.getBoolean("music", true))
        private set
    var female by mutableStateOf(sp.getBoolean("female", false))
        private set
    var stars by mutableStateOf(sp.getInt("stars", 0))
        private set

    private val best = mutableStateMapOf<String, Int>()
    var traced by mutableStateOf(sp.getString("traced", "").orEmpty())
        private set

    /** Most stars (0 to 3) earned in one quiz of this category. */
    fun bestStars(categoryId: String) = best[categoryId] ?: sp.getInt("best_$categoryId", 0)

    fun recordQuiz(categoryId: String, stars: Int) {
        if (stars <= bestStars(categoryId)) return
        best[categoryId] = stars
        sp.edit().putInt("best_$categoryId", stars).apply()
    }

    fun markTraced(symbol: Char) {
        if (symbol in traced) return
        traced += symbol
        sp.edit().putString("traced", traced).apply()
    }

    fun updateSfx(on: Boolean) { sfx = on; sp.edit().putBoolean("sfx", on).apply() }
    fun updateMusic(on: Boolean) { music = on; sp.edit().putBoolean("music", on).apply() }
    fun updateFemale(on: Boolean) { female = on; sp.edit().putBoolean("female", on).apply() }
    fun addStars(n: Int) { stars += n; sp.edit().putInt("stars", stars).apply() }
}
