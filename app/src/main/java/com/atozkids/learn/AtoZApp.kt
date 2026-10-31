package com.atozkids.learn

import android.app.Application
import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import com.atozkids.learn.audio.Sound
import com.atozkids.learn.data.Content
import com.atozkids.learn.data.Prefs

/** The app's shared objects, created once and handed to screens through [LocalGraph]. */
class Graph(context: Context) {
    val prefs = Prefs(context)
    val content = Content.load(context)
    val sound = Sound(context, prefs)
}

val LocalGraph = staticCompositionLocalOf<Graph> { error("Graph not provided") }

class AtoZApp : Application() {
    lateinit var graph: Graph
        private set

    override fun onCreate() {
        super.onCreate()
        graph = Graph(this)
    }
}
