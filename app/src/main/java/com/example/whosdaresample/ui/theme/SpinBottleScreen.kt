package com.example.whosdaresample.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whosdaresample.GameViewModel
import com.example.whosdaresample.R
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SpinBottleScreen(
    viewModel: GameViewModel,
    onPlayerChosen: () -> Unit
) {
    var isSpinning by remember { mutableStateOf(false) }
    var showChosen by remember { mutableStateOf(false) }
    val angle = remember { Animatable(0f) }

    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            showChosen = false
            val newAngle = 360f * (5..10).random() + Random.nextFloat() * 360
            angle.animateTo(
                targetValue = newAngle,
                animationSpec = tween(
                    durationMillis = 3000,
                    easing = FastOutSlowInEasing
                )
            )
            delay(300)
            viewModel.pickRandomPlayer()
            showChosen = true

            // ⏳ WICHTIG: Gib dem Nutzer Zeit, den Namen zu lesen!
            delay(2000)

            isSpinning = false
            onPlayerChosen()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("Spin the bottle...", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineMedium)

        Image(
            painter = painterResource(id = R.drawable.bottle1),
            contentDescription = "Spinning Bottle",
            modifier = Modifier
                .size(320.dp)
                .rotate(angle.value)
        )

        Button(
            onClick = { if (!isSpinning) isSpinning = true },
            enabled = !isSpinning,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Spin!", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        if (showChosen && viewModel.currentPlayer.value.isNotBlank()) {
            Text(
                text = "${viewModel.currentPlayer.value}, it's your turn!",
                color = Color.Magenta,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
