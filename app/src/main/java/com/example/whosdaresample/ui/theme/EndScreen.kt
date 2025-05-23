package com.example.whosdaresample.ui.theme


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whosdaresample.GameViewModel

@Composable
fun EndScreen(
    viewModel: GameViewModel,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Game Over!",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Cyan
        )

        Text(
            text = "Round Summary",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Magenta
        )

        if (viewModel.gameStats.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .padding(horizontal = 12.dp)
                    .background(Color.DarkGray, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                items(viewModel.gameStats) { stat ->
                    Text(text = stat, color = Color.White)
                }
            }
        } else {
            Text("No data available.", color = Color.Gray)
        }

        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
        ) {
            Text("Restart", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
