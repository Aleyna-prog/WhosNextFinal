package com.example.whosdaresample.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StartScreen(
    playerNames: List<String>,
    onAddName: (String) -> Unit,
    onRemoveName: (String) -> Unit,
    onStartGame: () -> Unit
) {
    var nameInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // 👈 Hintergrund tiefschwarz
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WelcomeCard()

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = {
                    Text("Enter name", color = Color.Cyan)
                },
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
                    if (nameInput.isNotBlank()) {
                        onAddName(nameInput.trim())
                        nameInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.Cyan
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
                            blurRadius = 10f
                        )
                    )
                )
            }
        }

        LazyColumn {
            items(playerNames) { name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = name,
                        color = Color.Cyan,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            shadow = Shadow(
                                color = Color.Cyan,
                                blurRadius = 8f
                            )
                        )
                    )
                    IconButton(onClick = { onRemoveName(name) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (playerNames.size >= 2) {
            NeonStartButton(onClick = onStartGame)
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A) // 👈 dunkler Hintergrund
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Welcome to Who's next!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.Magenta,
                    shadow = Shadow(
                        color = Color.Magenta,
                        blurRadius = 12f
                    )
                )
            )

            Text(
                text = "Enter all player names (min. 2) to begin. A random player will be chosen each round to decide: Truth or Dare.",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.LightGray)
            )

            Text(
                text = "\u23F3 No decision? Countdown is running... The system will decide!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Yellow
                )
            )

            Text(
                text = "Have fun!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan,
                    shadow = Shadow(
                        color = Color.Cyan,
                        offset = Offset(0f, 0f),
                        blurRadius = 12f
                    )
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
                    shadow = Shadow(
                        color = Color.Cyan,
                        offset = Offset(0f, 0f),
                        blurRadius = 20f
                    ),
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
