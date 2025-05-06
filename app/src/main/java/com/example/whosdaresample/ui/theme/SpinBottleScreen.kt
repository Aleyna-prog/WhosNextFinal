package com.example.whosdaresample.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.ui.text.font.FontWeight
import com.example.whosdaresample.R




@Composable
fun SpinBottleScreen(
    playerNames: List<String>,
    onPlayerChosen: (String) -> Unit
) {
    var isSpinning by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0f) }
    var chosenPlayer by remember { mutableStateOf<String?>(null) }

    val angle = remember { Animatable(0f) }

    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            chosenPlayer = null
            val newAngle = 360f * (5..10).random() + Random.nextFloat() * 360
            angle.animateTo(
                targetValue = newAngle,
                animationSpec = tween(
                    durationMillis = 3000,
                    easing = FastOutSlowInEasing
                )
            )
            delay(300) // kurze Pause
            rotation = newAngle % 360

            // Wähle zufällig einen Spieler
            val randomPlayer = playerNames.random()
            chosenPlayer = randomPlayer
            onPlayerChosen(randomPlayer)

            isSpinning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("Who's next?", color = Color.Cyan, style = MaterialTheme.typography.headlineMedium)

        Image(
            painter = painterResource(id = R.drawable.bottle),
            contentDescription = "Spinning Bottle",
            modifier = Modifier
                .size(220.dp)
                .rotate(angle.value)
        )

        Button(
            onClick = { if (!isSpinning) isSpinning = true },
            enabled = !isSpinning,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
        ) {
            Text("Spin!", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        if (chosenPlayer != null) {
            Text(
                text = "${chosenPlayer}, it's your turn!",
                color = Color.Yellow,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
