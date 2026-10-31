package com.atozkids.learn.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atozkids.learn.LocalGraph

const val CONTACT_EMAIL = "atozkids2020@gmail.com"

private enum class Gated { Share, Contact }

@Composable
fun SettingsScreen(onBack: () -> Unit, onPrivacy: () -> Unit) {
    val graph = LocalGraph.current
    val prefs = graph.prefs
    val context = LocalContext.current
    var gated by remember { mutableStateOf<Gated?>(null) }
    val version = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull() ?: ""
    }

    KidScreen("Settings", Grape, onBack) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Panel {
                SwitchRow("🔔  Sound effects", prefs.sfx) { prefs.updateSfx(it) }
                SwitchRow("🎵  Music", prefs.music) { prefs.updateMusic(it); graph.sound.syncMusic() }
            }
            Panel {
                Heading("Voice")
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    VoiceChoice("👦", "Boy", !prefs.female, Modifier.weight(1f)) {
                        prefs.updateFemale(false)
                        graph.sound.speak("audio/items/alphabet_apple_m.mp3")
                    }
                    VoiceChoice("👧", "Girl", prefs.female, Modifier.weight(1f)) {
                        prefs.updateFemale(true)
                        graph.sound.speak("audio/items/alphabet_apple_f.mp3")
                    }
                }
            }
            Panel {
                LinkRow("📤  Share with a friend") { gated = Gated.Share }
                LinkRow("✉️  Contact us") { gated = Gated.Contact }
                LinkRow("🔒  Privacy policy", onPrivacy)
            }
            Text("AtoZ Kids  v$version", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.Gray)
        }
    }

    gated?.let { what ->
        ParentGate(onDismiss = { gated = null }) {
            gated = null
            when (what) {
                Gated.Share -> context.startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND).setType("text/plain")
                            .putExtra(Intent.EXTRA_SUBJECT, "AtoZ Kids")
                            .putExtra(Intent.EXTRA_TEXT, "Learn letters, numbers, animals and more with AtoZ Kids - a free app that works without internet!\nhttps://play.google.com/store/apps/details?id=${context.packageName}"),
                        "Share AtoZ Kids",
                    ),
                )
                Gated.Contact -> runCatching {
                    context.startActivity(
                        Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$CONTACT_EMAIL")).putExtra(Intent.EXTRA_SUBJECT, "AtoZ Kids"),
                    )
                }
            }
        }
    }
}

@Composable
private fun Panel(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(24.dp)).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        content = content,
    )
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
        Switch(checked, onChange, colors = SwitchDefaults.colors(checkedTrackColor = Mint))
    }
}

@Composable
private fun LinkRow(label: String, onClick: () -> Unit) {
    Bouncy(onClick, Modifier.fillMaxWidth()) {
        Text(label, Modifier.fillMaxWidth().padding(vertical = 12.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
    }
}

@Composable
private fun VoiceChoice(emoji: String, label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Bouncy(onClick, modifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(if (selected) Mint.copy(alpha = 0.18f) else Color.Transparent, RoundedCornerShape(20.dp))
                .border(3.dp, if (selected) Mint else Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(emoji, fontSize = 40.sp)
            Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink)
        }
    }
}

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    KidScreen("Privacy policy", Grape, onBack) {
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Last updated: 31 October 2026", color = Color.Gray, fontSize = 14.sp)
            PrivacyText.sections.forEach { (title, body) ->
                Heading(title)
                Text(body, fontSize = 16.sp, color = Ink, lineHeight = 23.sp)
            }
        }
    }
}

private object PrivacyText {
    val sections = listOf(
        "Our promise" to "AtoZ Kids is a learning app for young children. It works completely offline and does not collect, store or share any personal information about you or your child.",
        "No internet, no tracking" to "The app does not ask for internet access. It has no advertising, no analytics, no accounts, no push notifications and no third-party services. Nothing leaves your device.",
        "What stays on your device" to "The app remembers only your settings (sound, music and voice choice) and the number of stars your child has earned. This is stored privately on the device and is removed when you uninstall the app or clear its data.",
        "Permissions" to "The app does not need any permissions. When a grown-up chooses Share or Contact us, your device's own sharing or e-mail app opens. These two buttons are protected by a simple question for grown-ups.",
        "Children's privacy" to "Because we collect no data at all, we never knowingly collect information from children. The app is designed to be suitable for children and to comply with children's privacy rules such as COPPA and GDPR-K.",
        "Changes" to "If this policy ever changes, the new version will appear here in the app with a new date.",
        "Contact" to "Questions? E-mail us at $CONTACT_EMAIL.",
    )
}
