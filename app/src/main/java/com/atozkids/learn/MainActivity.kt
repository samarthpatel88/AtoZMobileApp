package com.atozkids.learn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.atozkids.learn.ui.AppNav
import com.atozkids.learn.ui.AtoZTheme

class MainActivity : ComponentActivity() {
    private val graph get() = (application as AtoZApp).graph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalGraph provides graph) {
                AtoZTheme { AppNav() }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        graph.sound.onForeground()
    }

    override fun onStop() {
        super.onStop()
        graph.sound.onBackground()
    }
}
