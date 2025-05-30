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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
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
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (viewModel.isLightTheme.value) "Light Mode" else "Dark Mode",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = viewModel.isLightTheme.value,
                onCheckedChange = { viewModel.isLightTheme.value = it },
                modifier = Modifier.scale(0.8f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Cyan,
                    uncheckedThumbColor = Color.Gray
                )
            )
        }

        Image(
            painter = painterResource(id = R.drawable.logo1),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                //.padding(top = 16.dp)
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
                label = { Text("Enter name", color = MaterialTheme.colorScheme.primary) },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
        ) {
            val scrollState = rememberLazyListState()

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 12.dp, start = 8.dp, top = 8.dp, bottom = 8.dp),
                    state = scrollState,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(viewModel.playerNames) { name ->
                        val emoji = viewModel.getEmojiForPlayer(name)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (!emoji.isNullOrBlank()) "$emoji $name" else name,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    shadow = Shadow(
                                        color = MaterialTheme.colorScheme.primary,
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

                // Dynamisch Scrollbar berechnen
                val totalItems = scrollState.layoutInfo.totalItemsCount
                val visibleItems = scrollState.layoutInfo.visibleItemsInfo.size
                val fractionVisible = if (totalItems > 0) visibleItems.toFloat() / totalItems else 1f
                val fractionScrolled = if (totalItems > 0) scrollState.firstVisibleItemIndex.toFloat() / totalItems else 0f

                val containerHeightPx = with(LocalDensity.current) { 150.dp.toPx() }
                val scrollbarHeightPx = containerHeightPx * fractionVisible
                val scrollbarOffsetPx = containerHeightPx * fractionScrolled
                val showScrollbar = scrollState.layoutInfo.totalItemsCount > scrollState.layoutInfo.visibleItemsInfo.size


                // Scrollbar
                if (showScrollbar) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(6.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp, top = 12.dp, bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(with(LocalDensity.current) { scrollbarHeightPx.toDp() })
                            .offset {
                                IntOffset(x = 0, y = scrollbarOffsetPx.toInt())
                            }
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(50)
                            )
                    )
                } }
            }
        }



        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp, start = 8.dp)
        ) {
            Checkbox(
                checked = viewModel.isShuffleMode.value,
                onCheckedChange = { viewModel.isShuffleMode.value = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text("Shuffle Mode", color = MaterialTheme.colorScheme.primary)
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
            Text("✏️ Custom Tasks", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .border(2.dp, Color.Magenta, RoundedCornerShape(20.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Welcome to Who's Next!",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.Magenta,
                    shadow = Shadow(color = Color.Magenta, blurRadius = 12f)
                )
            )
            Text(
                text = "Enter all player names (min. 2) to begin. A random player will be chosen each round to decide: Truth or Dare.",
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
            )
            Text(
                text = "\u23F3 No decision? Countdown is running... The system will decide!",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Yellow)
            )
            Text(
                text = "Have fun!",
                style = MaterialTheme.typography.bodyMedium.copy(
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
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
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
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
