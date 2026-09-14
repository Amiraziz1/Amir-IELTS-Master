package com.example.ieltsmaster.ui.screens.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ieltsmaster.data.local.entities.SpeakingTaskEntity
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel
import com.example.ieltsmaster.utils.MarkdownUtils
import kotlinx.coroutines.delay

@Composable
fun SpeakingSection(viewModel: IeltsMasterViewModel) {
    val tasks by viewModel.filteredSpeakingTasks.collectAsStateWithLifecycle()
    val selectedPart by viewModel.selectedSpeakingPart.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopSpeakingPlayback()
            viewModel.stopSpeaking()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Part Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                0 to "All Parts",
                1 to "Part 1 (Intro)",
                2 to "Part 2 (Cue Card)",
                3 to "Part 3 (Discussion)"
            ).forEach { (part, label) ->
                FilterChip(
                    selected = selectedPart == part,
                    onClick = { viewModel.selectedSpeakingPart.value = part },
                    label = { Text(label) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(tasks, key = { it.taskId }) { task ->
                SpeakingTaskCard(task = task, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SpeakingTaskCard(
    task: SpeakingTaskEntity,
    viewModel: IeltsMasterViewModel
) {
    val isRecording by viewModel.isRecordingAudio.collectAsStateWithLifecycle()
    val isPlayingRecording by viewModel.isPlayingAudio.collectAsStateWithLifecycle()
    val recordingVersion by viewModel.recordingVersion.collectAsStateWithLifecycle()

    var showModelAnswer by remember { mutableStateOf(false) }

    // Preparation Timer (60s) for Part 2 Cue Cards
    var prepTimerSeconds by remember { mutableIntStateOf(task.prepTimeSeconds) }
    var isPrepTimerRunning by remember { mutableStateOf(false) }

    // Active Speaking Timer (120s)
    var speakTimerSeconds by remember { mutableIntStateOf(task.speakTimeSeconds) }
    var isSpeakTimerRunning by remember { mutableStateOf(false) }

    val hasAudioRecording = remember(recordingVersion, task.taskId) {
        viewModel.hasRecording(task.taskId)
    }

    LaunchedEffect(isPrepTimerRunning) {
        while (isPrepTimerRunning && prepTimerSeconds > 0) {
            delay(1000)
            prepTimerSeconds--
        }
        if (prepTimerSeconds == 0) {
            isPrepTimerRunning = false
        }
    }

    LaunchedEffect(isSpeakTimerRunning) {
        while (isSpeakTimerRunning && speakTimerSeconds > 0) {
            delay(1000)
            speakTimerSeconds--
        }
        if (speakTimerSeconds == 0) {
            isSpeakTimerRunning = false
            if (isRecording) {
                viewModel.stopSpeakingRecording(task.taskId)
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("speaking_card_${task.taskId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        "IELTS Speaking Part ${task.part} • ${task.topic}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                task.topic,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                MarkdownUtils.stripMarkdown(task.prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // If Cue Card (Part 2), show Timer Controls
            if (task.part == 2) {
                Spacer(Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("1-Min Prep Timer: ${prepTimerSeconds}s", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("2-Min Speak Timer: ${speakTimerSeconds}s", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    isPrepTimerRunning = !isPrepTimerRunning
                                    if (prepTimerSeconds == 0) prepTimerSeconds = 60
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(if (isPrepTimerRunning) "Pause Prep" else "Start Prep")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Real Voice Recording and Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (isRecording) {
                            viewModel.stopSpeakingRecording(task.taskId)
                            isSpeakTimerRunning = false
                        } else {
                            viewModel.startSpeakingRecording(task.taskId)
                            isSpeakTimerRunning = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("record_speaking_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (isRecording) "Stop Recording" else "Record Response")
                }

                if (hasAudioRecording) {
                    FilledTonalButton(
                        onClick = {
                            if (isPlayingRecording) {
                                viewModel.stopSpeakingPlayback()
                            } else {
                                viewModel.playSpeakingRecording(task.taskId)
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(if (isPlayingRecording) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(if (isPlayingRecording) "Stop" else "Listen")
                    }

                    IconButton(onClick = { viewModel.deleteSpeakingRecording(task.taskId) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { showModelAnswer = !showModelAnswer }) {
                    Text(if (showModelAnswer) "Hide Sample Response" else "View Band 8.5 Model")
                    Icon(if (showModelAnswer) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                }

                IconButton(onClick = { viewModel.speak(task.modelAnswer) }) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen to Model", tint = MaterialTheme.colorScheme.primary)
                }
            }

            AnimatedVisibility(visible = showModelAnswer) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "Band 8.5 Model Answer",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                MarkdownUtils.stripMarkdown(task.modelAnswer),
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 19.sp
                            )

                            if (task.vocabularyTips.isNotBlank()) {
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "High-Band Vocabulary & Idioms:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    MarkdownUtils.stripMarkdown(task.vocabularyTips),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (task.bandCriteriaTips.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Examiner Criteria Focus:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    MarkdownUtils.stripMarkdown(task.bandCriteriaTips),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
