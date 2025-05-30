package com.example.whosdaresample.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.whosdaresample.CustomTask
import com.example.whosdaresample.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTaskScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Truth") }
    var newTaskText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Custom Tasks", color = MaterialTheme.colorScheme.primary) },
            )

        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tab Switch
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = { selectedTab = "Truth" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == "Truth") Color.Magenta else Color.DarkGray
                    )
                ) {
                    Text("Truth", color = Color.White)
                }
                Button(
                    onClick = { selectedTab = "Dare" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == "Dare") Color.Yellow else Color.DarkGray
                    )
                ) {
                    Text("Dare", color = Color.Black)
                }
            }

            // Input
            OutlinedTextField(
                value = newTaskText,
                onValueChange = { newTaskText = it },
                label = { Text("New $selectedTab task", color = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Button(
                onClick = {
                    viewModel.addCustomTask(selectedTab, newTaskText.trim())
                    newTaskText = ""
                },
                enabled = newTaskText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add", color = Color.Black)
            }

            // List
            Text("Your $selectedTab Tasks", color = MaterialTheme.colorScheme.primary)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val filtered = viewModel.customTasks.filter { it.type == selectedTab }
                items(filtered) { task ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(task.text, color = MaterialTheme.colorScheme.onBackground)
                            IconButton(onClick = { viewModel.removeCustomTask(task) }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                            }
                        }
                    }
                }
            }

            // Zurück-Button
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Back to Menu", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
