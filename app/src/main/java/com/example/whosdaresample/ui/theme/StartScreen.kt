package com.example.whosdaresample.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whosdaresample.GameViewModel
import com.example.whosdaresample.R


@Composable
fun StartScreen(
    viewModel: GameViewModel,
    onStartGame: () -> Unit,
    onOpenCustomTasks: () -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.tap) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        WelcomeCard()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Enter name", color = Color.Cyan) },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Cyan,
                    unfocusedBorderColor = Color.Cyan,
                    focusedLabelColor = Color.Cyan,
                    unfocusedLabelColor = Color.Cyan
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    mediaPlayer.start()
                    viewModel.addPlayer(nameInput.trim())
                    nameInput = ""
                },
                enabled = nameInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.Cyan,
                    disabledContainerColor = Color.Black,
                    disabledContentColor = Color.DarkGray
                ),
                elevation = ButtonDefaults.buttonElevation(10.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(60.dp)
            ) {
                Text(
                    "Add",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Cyan,
                            offset = Offset(0f, 0f),
                        )
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
        ) {
            val scrollState = rememberLazyListState()

            Row(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    state = scrollState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.playerNames) { name ->
                        val emoji = viewModel.getEmojiForPlayer(name)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (!emoji.isNullOrBlank()) "$emoji $name" else name,
                                color = Color.Cyan,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    shadow = Shadow(
                                        color = Color.Cyan,
                                        blurRadius = 8f
                                    )
                                )
                            )
                            IconButton(onClick = { viewModel.removePlayer(name) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp, start = 8.dp)
        ) {
            Checkbox(
                checked = viewModel.isShuffleMode.value,
                onCheckedChange = { viewModel.isShuffleMode.value = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Cyan,
                    uncheckedColor = Color.Gray
                )
            )
            Text("Shuffle Mode", color = Color.Cyan)
        }

        Spacer(modifier = Modifier.weight(1f))

        if (viewModel.playerNames.size >= 2) {
            NeonStartButton {
                mediaPlayer.start()
                onStartGame()
            }
        }

        TextButton(
            onClick = onOpenCustomTasks,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("✏️ Custom Tasks", color = Color.LightGray)
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(3.dp, Color.Magenta, RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Welcome to Who's Next!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.Magenta,
                    shadow = Shadow(color = Color.Magenta, blurRadius = 12f)
                )
            )
            Text(
                text = "Enter all player names (min. 2) to begin. A random player will be chosen each round to decide: Truth or Dare.",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.LightGray)
            )
            Text(
                text = "\u23F3 No decision? Countdown is running... The system will decide!",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Yellow)
            )
            Text(
                text = "Have fun!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan,
                    shadow = Shadow(color = Color.Cyan, offset = Offset(0f, 0f), blurRadius = 12f)
                )
            )
        }
    }
}

@Composable
fun NeonStartButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.Cyan
        ),
        elevation = ButtonDefaults.buttonElevation(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "START GAME",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color.Cyan,
                    shadow = Shadow(color = Color.Cyan, offset = Offset(0f, 0f), blurRadius = 20f),
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.Cyan
            )
        }
    }
}
