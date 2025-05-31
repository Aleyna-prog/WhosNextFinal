package com.example.whosdaresample.ui.theme

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.whosdaresample.ExitConfirmDialog
import com.example.whosdaresample.GameViewModel
import com.example.whosdaresample.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNextRound: () -> Unit,
    onEndGame: () -> Unit
) {
    var showExitDialog by remember { mutableStateOf(false) }

    val selectedOption = viewModel.selectedOption.value
    val currentTask = viewModel.currentTask.value
    val isShuffle by viewModel.isShuffleMode
    val currentPlayer by viewModel.currentPlayer

    val lifecycleOwner = LocalLifecycleOwner.current

    val totalDuration = 15_000L
    val remainingTime = rememberSaveable { mutableStateOf(15_000L) }
    val countdownActive = rememberSaveable { mutableStateOf(false) }
    var countdownJob by remember { mutableStateOf<Job?>(null) }

    val isRunning = remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf<Long?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Direkt nach dem roundStarted check:
    LaunchedEffect(viewModel.roundStarted.value) {
        if (viewModel.roundStarted.value) {
            if (isShuffle) {
                val type = listOf("Truth", "Dare").random()
                viewModel.selectedOption.value = type
                viewModel.currentTask.value = viewModel.getTask(type)
                viewModel.recordChoice(type)
                playBeep()
            } else {
                remainingTime.value = 15_000L
                countdownJob?.cancel()
                countdownJob = coroutineScope.startCountdown(remainingTime, viewModel, countdownActive)
            }
        }
    }


    // Lifecycle: Pause / Resume Countdown
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    countdownJob?.cancel()
                    countdownJob = null
                    countdownActive.value = false
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (remainingTime.value > 0 && selectedOption == null && !isShuffle && countdownJob == null) {
                        countdownJob = coroutineScope.startCountdown(remainingTime, viewModel, countdownActive)

                    }
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    // Countdown animation
    val scale by animateFloatAsState(
        targetValue = if (remainingTime.value <= 5000 && selectedOption == null && !isShuffle) 1.5f else 1f,
        animationSpec = tween(300),
        label = "Countdown Scale"
    )

    // Countdown Execution
    LaunchedEffect(isRunning.value) {
        while (isRunning.value && remainingTime.value > 0 && selectedOption == null && !isShuffle) {
            delay(1000)
            remainingTime.value -= 1000
        }

        if (remainingTime.value <= 0 && selectedOption == null && !isShuffle) {
            val type = listOf("Truth", "Dare").random()
            viewModel.selectedOption.value = type
            viewModel.currentTask.value = viewModel.getTask(type)
            viewModel.recordChoice(type)
            playBeep()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo1),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(start = 8.dp)
                        )
                        Button(
                            onClick = { showExitDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(32.dp)
                                .defaultMinSize(minWidth = 1.dp) // Verhindert riesige Breite
                        ) {
                            Text(
                                text = "End Game",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
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
                text = "$currentPlayer, it's your turn!",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium
            )

            if (selectedOption == null && !isShuffle) {
                Text(
                    text = "⏱️ ${remainingTime.value / 1000} s left to choose",
                    color = if (remainingTime.value <= 5000) Color.Red else MaterialTheme.colorScheme.onBackground,
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
                        modifier = Modifier
                            .weight(1f)
                            .border(3.dp, Color.Magenta, RoundedCornerShape(20.dp))
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
                        modifier = Modifier
                            .weight(1f)
                            .border(3.dp, Color.Yellow, RoundedCornerShape(20.dp))
                    )
                }
                if (!viewModel.hasUsedJoker(currentPlayer)) {
                    Button(
                        onClick = {
                            viewModel.useJoker(currentPlayer)
                            viewModel.resetRound()
                            remainingTime.value = totalDuration
                            onNextRound()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Use Joker", color = Color.Black)
                    }
                }

            } else if (selectedOption != null) {
                val taskLabel = if (viewModel.actualTaskType.value != null) {
                    "Your ${viewModel.actualTaskType.value} task:"
                } else if (selectedOption != null) {
                    "Your $selectedOption task:"
                } else {
                    ""
                }
                Text(
                    text = taskLabel,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = currentTask,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                if (!viewModel.hasUsedJoker(currentPlayer) && isShuffle) {
                    Button(
                        onClick = {
                            viewModel.useJoker(currentPlayer)
                            viewModel.resetRound()
                            remainingTime.value = totalDuration
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
                        remainingTime.value = totalDuration
                        onNextRound()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .height(55.dp)
                ) {
                    Text("Next Round", color = Color.Black, fontWeight = FontWeight.Medium)
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


fun playBeep() {
    val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    toneGen.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 300)
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

fun CoroutineScope.startCountdown(
    timeState: MutableState<Long>,
    viewModel: GameViewModel,
    isActive: MutableState<Boolean>
): Job {
    isActive.value = true
    return launch {
        while (timeState.value > 0 && viewModel.selectedOption.value == null && !viewModel.isShuffleMode.value) {
            delay(1000)
            timeState.value -= 1000
        }

        if (timeState.value <= 0 && viewModel.selectedOption.value == null && !viewModel.isShuffleMode.value) {
            val type = listOf("Truth", "Dare").random()
            viewModel.selectedOption.value = type
            viewModel.currentTask.value = viewModel.getTask(type)
            viewModel.recordChoice(type)
            playBeep()
        }

        isActive.value = false
    }
}