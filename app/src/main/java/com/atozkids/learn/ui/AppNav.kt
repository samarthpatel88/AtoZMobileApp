package com.atozkids.learn.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNav() {
    val nav = rememberNavController()
    // Taps that land while a screen is still sliding in are ignored, so a quick double tap on back
    // cannot pop the home screen and leave the app blank, and a double tap cannot open a screen twice.
    fun settled() = nav.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED
    val back = { if (settled() && nav.previousBackStackEntry != null) nav.popBackStack(); Unit }
    val go = { route: String -> if (settled()) nav.navigate(route) }
    NavHost(nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onLearn = { go("categories/learn") },
                onQuiz = { go("categories/quiz") },
                onRhymes = { go("rhymes") },
                onPlay = { go("play") },
                onSettings = { go("settings") },
            )
        }
        composable("categories/{mode}", listOf(navArgument("mode") { type = NavType.StringType })) { entry ->
            val mode = entry.arguments!!.getString("mode")!!
            CategoriesScreen(mode, onBack = back, onPick = { go("$mode/$it") })
        }
        composable("learn/{id}") { LearnScreen(it.arguments!!.getString("id")!!, back) }
        composable("quiz/{id}") { QuizScreen(it.arguments!!.getString("id")!!, back) }
        composable("play") {
            PlayScreen(back, onMemory = { go("categories/memory") }, onTrace = { go("trace") })
        }
        composable("memory/{id}") { MemoryScreen(it.arguments!!.getString("id")!!, back) }
        composable("trace") { TraceScreen(back) }
        composable("rhymes") { RhymesScreen(back) { id -> go("rhyme/$id") } }
        composable("rhyme/{id}") { RhymePlayerScreen(it.arguments!!.getString("id")!!, back) }
        composable("settings") { SettingsScreen(back) { go("privacy") } }
        composable("privacy") { PrivacyScreen(back) }
    }
}
