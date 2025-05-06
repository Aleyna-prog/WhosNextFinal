// Datei: GameScreen.kt
package com.example.whosdaresample.ui.theme

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.clip

// Sound abspielen
fun playBeep() {
    val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 300)
}

@Composable
fun GameScreen(
    currentPlayer: String,
    onNextRound: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var task by remember { mutableStateOf("") }
    var countdown by remember { mutableStateOf(15) }
    var autoSelected by remember { mutableStateOf(false) }
    var systemPickMessage by remember { mutableStateOf("") }

    val truthTasks = listOf("What is your biggest fear?", "Have you ever cheated?")
    val dareTasks = listOf("Do a silly dance!", "Speak like a robot for 1 minute!")

    // Countdown mit automatischer Auswahl
    LaunchedEffect(key1 = selectedOption) {
        if (selectedOption == null) {
            countdown = 15
            autoSelected = false
            systemPickMessage = ""
            while (countdown > 0 && selectedOption == null) {
                delay(1000L)
                countdown--
            }
            if (selectedOption == null && !autoSelected) {
                autoSelected = true
                val choice = if ((0..1).random() == 0) {
                    selectedOption = "Truth"
                    task = truthTasks.random()
                    "Truth"
                } else {
                    selectedOption = "Dare"
                    task = dareTasks.random()
                    "Dare"
                }
                systemPickMessage = "⏰ Time's up! The system picked $choice for you!"
                playBeep()
            }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (countdown <= 5 && selectedOption == null) 1.5f else 1f,
        animationSpec = androidx.compose.animation.core.tween(300)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$currentPlayer, it's your turn!",
            color = Color.Cyan,
            style = MaterialTheme.typography.headlineMedium
        )

        if (selectedOption == null) {
            Text(
                text = "⏱️ $countdown s left to choose",
                color = if (countdown <= 5) Color.Red else Color.White,
                fontSize = (20 * scale).sp,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.DarkGray)
                        .clickable {
                            selectedOption = "Truth"
                            task = truthTasks.random()
                            playBeep()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "TRUTH",
                        color = Color.Magenta,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.DarkGray)
                        .clickable {
                            selectedOption = "Dare"
                            task = dareTasks.random()
                            playBeep()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "DARE",
                        color = Color.Yellow,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        } else {
            if (systemPickMessage.isNotEmpty()) {
                Text(
                    text = systemPickMessage,
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            } else {
                Text("Your $selectedOption task:", color = Color.White)
            }

            Text(
                text = task,
                color = Color.LightGray,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = {
                    selectedOption = null
                    task = ""
                    onNextRound()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .height(55.dp)
            ) {
                Text("Next Round", color = Color.Black, fontWeight = FontWeight.Medium)
            }
        }
    }
}
