// Datei: GameScreen.kt
package com.example.whosdaresample.ui.theme

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whosdaresample.ExitConfirmDialog
import com.example.whosdaresample.GameViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNextRound: () -> Unit,
    onEndGame: () -> Unit
) {
    var countdown by remember { mutableStateOf(15) }
    var showExitDialog by remember { mutableStateOf(false) }

    val selectedOption = viewModel.selectedOption
    val currentTask = viewModel.currentTask
    val isShuffle = viewModel.isShuffleMode.value
    val scale by animateFloatAsState(
        targetValue = if (countdown <= 5 && selectedOption.value == null && !isShuffle) 1.5f else 1f,
        animationSpec = tween(300), label = "Countdown Scale"
    )

    LaunchedEffect(Unit) {
        if (selectedOption.value == null) {
            if (isShuffle) {
                val type = listOf("Truth", "Dare").random()
                viewModel.selectedOption.value = type
                viewModel.currentTask.value = viewModel.getTask(type)
                viewModel.recordChoice(type)
                delay(300)
                playBeep()
            } else {
                countdown = 15
                while (countdown > 0) {
                    delay(1000L)
                    countdown--
                }
                if (selectedOption.value == null) {
                    val type = if ((0..1).random() == 0) "Truth" else "Dare"
                    viewModel.selectedOption.value = type
                    viewModel.currentTask.value = viewModel.getTask(type)
                    viewModel.recordChoice(type)
                    playBeep()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Truth or Dare") },
                actions = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Exit",
                            tint = Color.Red
                        )
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${viewModel.currentPlayer.value}, it's your turn!",
                color = Color.Cyan,
                style = MaterialTheme.typography.headlineMedium
            )

            if (selectedOption.value == null && !isShuffle) {
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
                    TaskBox(
                        label = "TRUTH",
                        color = Color.Magenta,
                        onClick = {
                            viewModel.selectedOption.value = "Truth"
                            viewModel.currentTask.value = viewModel.getTask("Truth")
                            viewModel.recordChoice("Truth")
                            playBeep()
                        },
                        modifier = Modifier.weight(1f)
                    )

                    TaskBox(
                        label = "DARE",
                        color = Color.Yellow,
                        onClick = {
                            viewModel.selectedOption.value = "Dare"
                            viewModel.currentTask.value = viewModel.getTask("Dare")
                            viewModel.recordChoice("Dare")
                            playBeep()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else if (selectedOption.value != null) {
                Text("Your ${selectedOption.value} task:", color = Color.White)
                Text(
                    text = currentTask.value,
                    color = Color.LightGray,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                val player = viewModel.currentPlayer.value
                if (!viewModel.hasUsedJoker(player)) {
                    Button(
                        onClick = {
                            viewModel.useJoker(player)
                            onNextRound()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Use Joker", color = Color.Black)
                    }
                }

                Button(
                    onClick = {
                        viewModel.resetRound()
                        onNextRound()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .height(55.dp)
                ) {
                    Text("Next Round", color = Color.Black, fontWeight = FontWeight.Medium)
                }

                TextButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text("End Game", color = Color.Red)
                }
            }
        }

        if (showExitDialog) {
            ExitConfirmDialog(
                onConfirm = onEndGame,
                onDismiss = { showExitDialog = false }
            )
        }
    }
}

@Composable
fun TaskBox(label: String, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.DarkGray)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = color,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

fun playBeep() {
    val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 300)
}

