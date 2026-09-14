package com.example.ieltsmaster.ui.screens.ielts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ieltsmaster.data.local.entities.MockTestAttemptEntity
import com.example.ieltsmaster.data.local.entities.MockTestEntity
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel
import com.example.ieltsmaster.utils.BandScoreCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IeltsScreen(
    viewModel: IeltsMasterViewModel,
    onStartMockTest: (String) -> Unit
) {
    val mockTests by viewModel.allMockTests.collectAsStateWithLifecycle()
    val attempts by viewModel.allMockAttempts.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var selectedBandGuide by remember { mutableFloatStateOf(7.5f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "IELTS Exam Centre",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Official format simulations & Band 7+ master roadmap",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Target Band Roadmap Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    "Target Roadmap: Band $selectedBandGuide",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    BandScoreCalculator.getBandDescriptor(selectedBandGuide),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f).forEach { band ->
                                FilterChip(
                                    selected = selectedBandGuide == band,
                                    onClick = { selectedBandGuide = band },
                                    label = { Text("$band") }
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "Requirements for Band $selectedBandGuide:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    when (selectedBandGuide) {
                                        in 8.0f..9.0f -> "• Listening & Reading: 35-40/40 correct\n• Writing: Full paragraph development, error-free complex syntax, natural academic collocations\n• Speaking: Effortless fluency, rare self-correction, native-level idiomatic precision"
                                        in 7.0f..7.5f -> "• Listening & Reading: 30-34/40 correct\n• Writing: Clear position throughout, varied cohesive linkers, flexible vocabulary with few lexical slips\n• Speaking: Speaks at length without noticeable effort, uses complex grammar structures with occasional minor slips"
                                        else -> "• Listening & Reading: 23-29/40 correct\n• Writing: Addresses all parts of the prompt, clear overall progression, adequate vocabulary\n• Speaking: Willing to speak at length, simple sentences mostly accurate"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Available Mock Tests Section
            item {
                Text(
                    "Full Mock Tests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(mockTests, key = { it.testId }) { test ->
                MockTestItemCard(test = test, onStart = { onStartMockTest(test.testId) })
            }

            // Past Attempts History
            if (attempts.isNotEmpty()) {
                item {
                    Text(
                        "Past Exam Performance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(attempts, key = { it.attemptId }) { attempt ->
                    MockAttemptCard(attempt = attempt)
                }
            }
        }
    }
}

@Composable
fun MockTestItemCard(
    test: MockTestEntity,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mock_test_card_${test.testId}"),
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
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        test.testType,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text("⏱ ${test.durationMinutes} minutes", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                test.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                test.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${test.totalQuestions} Questions • Audio + Reading",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Take Test")
                }
            }
        }
    }
}

@Composable
fun MockAttemptCard(attempt: MockTestAttemptEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(54.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Band",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        "${attempt.overallBand}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    attempt.testTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    "Listening: ${attempt.listeningBand} • Reading: ${attempt.readingBand} • Writing: ${attempt.writingBand} • Speaking: ${attempt.speakingBand}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    attempt.feedback,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
