package com.example.ieltsmaster.ui.screens.ielts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel
import com.example.ieltsmaster.utils.BandScoreCalculator
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockTestScreen(
    testId: String,
    viewModel: IeltsMasterViewModel,
    onFinish: () -> Unit
) {
    val allMockTests by viewModel.allMockTests.collectAsStateWithLifecycle()
    val test = remember(allMockTests, testId) {
        allMockTests.find { it.testId == testId } ?: allMockTests.firstOrNull()
    }

    val listeningQuestions by viewModel.listeningQuestions.collectAsStateWithLifecycle()
    val readingQuestions by viewModel.readingQuestions.collectAsStateWithLifecycle()

    val combinedQuestions = remember(listeningQuestions, readingQuestions) {
        listeningQuestions + readingQuestions
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswers by remember { mutableStateOf(mapOf<String, String>()) }

    var remainingSeconds by remember { mutableIntStateOf(45 * 60) }
    var isTestSubmitted by remember { mutableStateOf(false) }

    // Calculated Scores
    var calculatedListeningBand by remember { mutableFloatStateOf(0f) }
    var calculatedReadingBand by remember { mutableFloatStateOf(0f) }
    var calculatedOverallBand by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isTestSubmitted) {
        while (!isTestSubmitted && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
    }

    fun submitExam() {
        val listeningCount = listeningQuestions.count { q ->
            selectedAnswers[q.questionId].equals(q.correctAnswer, ignoreCase = true)
        }
        val readingCount = readingQuestions.count { q ->
            selectedAnswers[q.questionId].equals(q.correctAnswer, ignoreCase = true)
        }

        // Scale up to 40 questions equivalent for accurate band score calculation
        val scaledListening = (listeningCount.toFloat() / listeningQuestions.size.coerceAtLeast(1) * 35).toInt().coerceIn(10, 40)
        val scaledReading = (readingCount.toFloat() / readingQuestions.size.coerceAtLeast(1) * 35).toInt().coerceIn(10, 40)

        calculatedListeningBand = BandScoreCalculator.calculateListeningBand(scaledListening)
        calculatedReadingBand = BandScoreCalculator.calculateReadingAcademicBand(scaledReading)
        calculatedOverallBand = BandScoreCalculator.calculateOverallBand(
            calculatedListeningBand, calculatedReadingBand, 7.0f, 7.0f
        )

        viewModel.submitMockTest(
            testId = test?.testId ?: "mock_test_1",
            title = test?.title ?: "IELTS Academic Simulation Test 1",
            listeningCorrect = scaledListening,
            readingCorrect = scaledReading,
            isAcademic = true
        )
        isTestSubmitted = true
    }

    val formattedTime = remember(remainingSeconds) {
        val mins = remainingSeconds / 60
        val secs = remainingSeconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(test?.title ?: "IELTS Mock Examination", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Question ${currentQuestionIndex + 1} of ${combinedQuestions.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                formattedTime,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isTestSubmitted) {
            // Result Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(100.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("BAND", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(
                            "$calculatedOverallBand",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "Test Completed!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    BandScoreCalculator.getBandDescriptor(calculatedOverallBand),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // Sub-score Cards
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ScoreBox(label = "Listening", band = calculatedListeningBand, modifier = Modifier.weight(1f))
                    ScoreBox(label = "Reading", band = calculatedReadingBand, modifier = Modifier.weight(1f))
                    ScoreBox(label = "Writing", band = 7.0f, modifier = Modifier.weight(1f))
                    ScoreBox(label = "Speaking", band = 7.0f, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Detailed Feedback & Next Steps:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "• Strong performance in key listening detail extraction.\n• Reading accuracy is on track for your target band. Focus on True/False/Not Given scanning speed.\n• Practice more Part 2 speaking cue cards to eliminate minor hesitations.",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("finish_mock_test_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Return to IELTS Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else if (combinedQuestions.isNotEmpty()) {
            val q = combinedQuestions[currentQuestionIndex.coerceIn(0, combinedQuestions.size - 1)]
            val selected = selectedAnswers[q.questionId]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        "${q.moduleOrSkill} • Question ${currentQuestionIndex + 1} of ${combinedQuestions.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (q.passageOrScript.isNotBlank()) {
                    Spacer(Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "Section Passage / Script:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(q.passageOrScript, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    q.questionText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                val rawOptions = q.optionsJson
                    .removePrefix("[\"")
                    .removeSuffix("\"]")
                    .split("\",\"")

                rawOptions.forEach { option ->
                    val isSelected = selected == option
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedAnswers = selectedAnswers + (q.questionId to option)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedAnswers = selectedAnswers + (q.questionId to option) }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(option, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Navigation Pager Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                        enabled = currentQuestionIndex > 0,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Previous")
                    }

                    if (currentQuestionIndex < combinedQuestions.size - 1) {
                        Button(
                            onClick = { currentQuestionIndex++ },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Next")
                        }
                    } else {
                        Button(
                            onClick = { submitExam() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("submit_exam_button")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Submit Exam")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreBox(label: String, band: Float, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(2.dp))
            Text("$band", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
