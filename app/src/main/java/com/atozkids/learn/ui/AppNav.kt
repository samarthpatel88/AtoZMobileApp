package com.atozkids.learn.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val back = { nav.popBackStack(); Unit }
    NavHost(nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onLearn = { nav.navigate("categories/learn") },
                onQuiz = { nav.navigate("categories/quiz") },
                onRhymes = { nav.navigate("rhymes") },
                onPlay = { nav.navigate("play") },
                onSettings = { nav.navigate("settings") },
            )
        }
        composable("categories/{mode}", listOf(navArgument("mode") { type = NavType.StringType })) { entry ->
            val mode = entry.arguments!!.getString("mode")!!
            CategoriesScreen(mode, onBack = back, onPick = { nav.navigate("$mode/$it") })
        }
        composable("learn/{id}") { LearnScreen(it.arguments!!.getString("id")!!, back) }
        composable("quiz/{id}") { QuizScreen(it.arguments!!.getString("id")!!, back) }
        composable("play") {
            PlayScreen(back, onMemory = { nav.navigate("categories/memory") }, onTrace = { nav.navigate("trace") })
        }
        composable("memory/{id}") { MemoryScreen(it.arguments!!.getString("id")!!, back) }
        composable("trace") { TraceScreen(back) }
        composable("rhymes") { RhymesScreen(back) { id -> nav.navigate("rhyme/$id") } }
        composable("rhyme/{id}") { RhymePlayerScreen(it.arguments!!.getString("id")!!, back) }
        composable("settings") { SettingsScreen(back) { nav.navigate("privacy") } }
        composable("privacy") { PrivacyScreen(back) }
    }
}
