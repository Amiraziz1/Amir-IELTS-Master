package com.example.ieltsmaster.ui.screens.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ieltsmaster.data.local.entities.SpeakingCoachSessionEntity
import com.example.ieltsmaster.data.local.entities.SpeakingTaskEntity
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel
import com.example.ieltsmaster.utils.BandScoreCalculator
import com.example.ieltsmaster.utils.MarkdownUtils
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakingCoachScreen(
    viewModel: IeltsMasterViewModel,
    onBack: () -> Unit
) {
    val tasks by viewModel.allSpeakingTasks.collectAsStateWithLifecycle()
    val pastSessions by viewModel.speakingCoachSessions.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Practice Lab, 1: History
    var selectedTask by remember { mutableStateOf<SpeakingTaskEntity?>(null) }

    LaunchedEffect(tasks) {
        if (selectedTask == null && tasks.isNotEmpty()) {
            selectedTask = tasks.firstOrNull { it.part == 2 } ?: tasks.first()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopSpeakingPlayback()
            viewModel.stopSpeaking()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Speaking Coach Lab",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Timed Cue Cards & Examiner Rubrics",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Cue Card Simulator") },
                    icon = { Icon(Icons.Default.Mic, contentDescription = null) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Session History (${pastSessions.size})") },
                    icon = { Icon(Icons.Default.History, contentDescription = null) }
                )
            }

            if (selectedTabIndex == 0) {
                if (selectedTask != null) {
                    SpeakingCoachActiveLab(
                        task = selectedTask!!,
                        allTasks = tasks,
                        onSelectTask = { selectedTask = it },
                        viewModel = viewModel
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                SpeakingHistoryList(pastSessions = pastSessions)
            }
        }
    }
}

@Composable
fun SpeakingCoachActiveLab(
    task: SpeakingTaskEntity,
    allTasks: List<SpeakingTaskEntity>,
    onSelectTask: (SpeakingTaskEntity) -> Unit,
    viewModel: IeltsMasterViewModel
) {
    val isRecording by viewModel.isRecordingAudio.collectAsStateWithLifecycle()
    val isPlayingRecording by viewModel.isPlayingAudio.collectAsStateWithLifecycle()
    val recordingVersion by viewModel.recordingVersion.collectAsStateWithLifecycle()

    var showTaskSelector by remember { mutableStateOf(false) }
    var showEvaluationDialog by remember { mutableStateOf(false) }
    var showModelAnswer by remember { mutableStateOf(false) }

    // Preparation Timer (60s)
    var prepSecondsRemaining by remember(task.taskId) { mutableIntStateOf(task.prepTimeSeconds) }
    var isPrepRunning by remember(task.taskId) { mutableStateOf(false) }

    // Active Speaking Timer (120s)
    var speakSecondsRemaining by remember(task.taskId) { mutableIntStateOf(task.speakTimeSeconds) }
    var isSpeakRunning by remember(task.taskId) { mutableStateOf(false) }

    val hasRecording = remember(recordingVersion, task.taskId) {
        viewModel.hasRecording(task.taskId)
    }

    // Prep Timer Loop
    LaunchedEffect(isPrepRunning) {
        while (isPrepRunning && prepSecondsRemaining > 0) {
            delay(1000)
            prepSecondsRemaining--
        }
        if (prepSecondsRemaining == 0) {
            isPrepRunning = false
        }
    }

    // Speak Timer Loop
    LaunchedEffect(isSpeakRunning) {
        while (isSpeakRunning && speakSecondsRemaining > 0) {
            delay(1000)
            speakSecondsRemaining--
        }
        if (speakSecondsRemaining == 0) {
            isSpeakRunning = false
            if (isRecording) {
                viewModel.stopSpeakingRecording(task.taskId)
                showEvaluationDialog = true
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Topic Selector Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTaskSelector = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Part ${task.part} • ${task.topic}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Tap to switch between IELTS Cue Cards",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Switch Topic",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Cue Card Content Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Candidate Task Card",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        MarkdownUtils.stripMarkdown(task.prompt),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(12.dp))

                    if (task.vocabularyTips.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Key Lexical Resource & Collocations:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    MarkdownUtils.stripMarkdown(task.vocabularyTips),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dual Timers (Prep & Speak)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1-min Prep Box
                TimerGaugeCard(
                    title = "1-Min Prep",
                    secondsRemaining = prepSecondsRemaining,
                    totalSeconds = task.prepTimeSeconds,
                    isRunning = isPrepRunning,
                    onToggle = {
                        if (prepSecondsRemaining == 0) prepSecondsRemaining = task.prepTimeSeconds
                        isPrepRunning = !isPrepRunning
                    },
                    modifier = Modifier.weight(1f)
                )

                // 2-min Speaking Box
                TimerGaugeCard(
                    title = "2-Min Speaking",
                    secondsRemaining = speakSecondsRemaining,
                    totalSeconds = task.speakTimeSeconds,
                    isRunning = isSpeakRunning,
                    onToggle = {
                        if (speakSecondsRemaining == 0) speakSecondsRemaining = task.speakTimeSeconds
                        isSpeakRunning = !isSpeakRunning
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Voice Recording Actions
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isRecording) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (isRecording) "Recording your cue card response..."
                        else if (hasRecording) "Response recorded! Listen or evaluate below."
                        else "Ready to record your 2-minute spoken response",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (isRecording) {
                                    viewModel.stopSpeakingRecording(task.taskId)
                                    isSpeakRunning = false
                                    showEvaluationDialog = true
                                } else {
                                    viewModel.startSpeakingRecording(task.taskId)
                                    isSpeakRunning = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("coach_record_button")
                        ) {
                            Icon(
                                if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(if (isRecording) "Stop & Finish" else "Record Response")
                        }

                        if (hasRecording) {
                            FilledTonalButton(
                                onClick = {
                                    if (isPlayingRecording) {
                                        viewModel.stopSpeakingPlayback()
                                    } else {
                                        viewModel.playSpeakingRecording(task.taskId)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("coach_play_button")
                            ) {
                                Icon(
                                    if (isPlayingRecording) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(if (isPlayingRecording) "Stop" else "Listen")
                            }

                            OutlinedButton(
                                onClick = { showEvaluationDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("coach_evaluate_button")
                            ) {
                                Icon(Icons.Default.StarRate, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Rubric")
                            }
                        }
                    }
                }
            }
        }

        // Band 8.5 Model Answer & Criteria Notes
        item {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Band 8.5 Authentic Model",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            IconButton(onClick = { viewModel.speak(MarkdownUtils.stripMarkdown(task.modelAnswer)) }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Listen to Model", tint = MaterialTheme.colorScheme.primary)
                            }
                            TextButton(onClick = { showModelAnswer = !showModelAnswer }) {
                                Text(if (showModelAnswer) "Hide" else "Read")
                                Icon(
                                    if (showModelAnswer) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = showModelAnswer) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                MarkdownUtils.stripMarkdown(task.modelAnswer),
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Task Selector Bottom Sheet / Dialog
    if (showTaskSelector) {
        AlertDialog(
            onDismissRequest = { showTaskSelector = false },
            title = { Text("Choose Practice Cue Card") },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allTasks) { itemTask ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectTask(itemTask)
                                    showTaskSelector = false
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (itemTask.taskId == task.taskId) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Part ${itemTask.part} • ${itemTask.topic}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    MarkdownUtils.stripMarkdown(itemTask.prompt),
                                    fontSize = 11.sp,
                                    maxLines = 2,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTaskSelector = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Self-Evaluation & Rubric Scoring Dialog
    if (showEvaluationDialog) {
        RubricEvaluationDialog(
            task = task,
            onDismiss = { showEvaluationDialog = false },
            onSaveSession = { fluency, vocab, grammar, pron, notes ->
                viewModel.saveSpeakingCoachSession(
                    taskId = task.taskId,
                    taskTitle = task.topic,
                    part = task.part,
                    durationSeconds = 120,
                    transcript = "",
                    fluencyScore = fluency,
                    vocabScore = vocab,
                    grammarScore = grammar,
                    pronunciationScore = pron,
                    feedbackNotes = notes
                )
                showEvaluationDialog = false
            }
        )
    }
}

@Composable
fun TimerGaugeCard(
    title: String,
    secondsRemaining: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (secondsRemaining.toFloat() / totalSeconds.coerceAtLeast(1)).coerceIn(0f, 1f)
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(8.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = trackColor, style = Stroke(width = 6.dp.toPx()))
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text(
                    "${secondsRemaining}s",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            FilledTonalButton(
                onClick = onToggle,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isRunning) "Pause" else "Start", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RubricEvaluationDialog(
    task: SpeakingTaskEntity,
    onDismiss: () -> Unit,
    onSaveSession: (Float, Float, Float, Float, String) -> Unit
) {
    var fluency by remember { mutableFloatStateOf(7.0f) }
    var vocab by remember { mutableFloatStateOf(7.0f) }
    var grammar by remember { mutableFloatStateOf(7.0f) }
    var pron by remember { mutableFloatStateOf(7.0f) }
    var notes by remember { mutableStateOf("") }

    val overallBand = remember(fluency, vocab, grammar, pron) {
        BandScoreCalculator.calculateOverallBand(fluency, vocab, grammar, pron)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("IELTS Band Rubric", fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        "Band $overallBand",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 13.sp
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Evaluate your performance against the 4 official IELTS Speaking criteria:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    RubricScoreSlider(
                        label = "Fluency & Coherence",
                        description = "Smooth speech, minimal hesitations, effective discourse markers",
                        score = fluency,
                        onScoreChange = { fluency = it }
                    )
                }

                item {
                    RubricScoreSlider(
                        label = "Lexical Resource",
                        description = "Varied vocabulary, collocations, idiomatic expressions",
                        score = vocab,
                        onScoreChange = { vocab = it }
                    )
                }

                item {
                    RubricScoreSlider(
                        label = "Grammatical Range & Accuracy",
                        description = "Complex sentence structures, high accuracy, tense consistency",
                        score = grammar,
                        onScoreChange = { grammar = it }
                    )
                }

                item {
                    RubricScoreSlider(
                        label = "Pronunciation",
                        description = "Intonation, word stress, clarity of speech",
                        score = pron,
                        onScoreChange = { pron = it }
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Coach Notes / Self-Reflections") },
                        placeholder = { Text("e.g. Good vocabulary, but hesitated on cue card point 3...") },
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSaveSession(fluency, vocab, grammar, pron, notes) }) {
                Text("Save Assessment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RubricScoreSlider(
    label: String,
    description: String,
    score: Float,
    onScoreChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("${score}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
        }
        Text(description, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
        Slider(
            value = score,
            onValueChange = { onScoreChange((Math.round(it * 2) / 2f).coerceIn(4.0f, 9.0f)) },
            valueRange = 4.0f..9.0f,
            steps = 9
        )
    }
}

@Composable
fun SpeakingHistoryList(pastSessions: List<SpeakingCoachSessionEntity>) {
    if (pastSessions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.HistoryEdu,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "No Speaking Assessments Yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Complete a Cue Card practice session in the simulator to evaluate and track your speaking band score over time.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(pastSessions, key = { it.id }) { session ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Part ${session.part} • ${session.taskTitle}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    dateFormat.format(Date(session.timestamp)),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    "Band ${session.overallScore}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CriterionMiniScore("Fluency", session.fluencyScore)
                            CriterionMiniScore("Lexical", session.vocabScore)
                            CriterionMiniScore("Grammar", session.grammarScore)
                            CriterionMiniScore("Pronun.", session.pronunciationScore)
                        }

                        if (session.feedbackNotes.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Notes: ${session.feedbackNotes}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CriterionMiniScore(label: String, score: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
        Text("${score}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
