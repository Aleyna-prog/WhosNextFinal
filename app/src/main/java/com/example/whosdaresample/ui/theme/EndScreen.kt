package com.example.whosdaresample.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whosdaresample.GameViewModel
import kotlinx.coroutines.launch

@Composable
fun EndScreen(
    viewModel: GameViewModel,
    onRestart: () -> Unit

) {
    val scope = rememberCoroutineScope()
    var statsMap by remember { mutableStateOf<Map<String, Pair<Int, Int>>>(emptyMap()) }

    // Statistiken laden bei erster Anzeige
    LaunchedEffect(Unit) {
        viewModel.getStatsGroupedByPlayer { statsMap = it }
    }

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
            text = "Statistik",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Magenta
        )

        if (statsMap.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                statsMap.forEach { (name, pair) ->
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                            elevation = CardDefaults.cardElevation(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = name,
                                    color = Color.Cyan,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Truth: ${pair.first}",
                                    color = Color.Magenta,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Dare: ${pair.second}",
                                    color = Color.Yellow,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text("No data available.", color = Color.Gray)
        }

        Button(
            onClick = {
                viewModel.resetRound()
                viewModel.roundStarted.value = false
                onRestart()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
        ) {
            Text("Restart", color = Color.Black, fontWeight = FontWeight.Bold)
        }

    }
}
