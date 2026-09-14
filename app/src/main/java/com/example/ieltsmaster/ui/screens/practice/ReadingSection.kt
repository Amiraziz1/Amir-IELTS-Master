package com.example.ieltsmaster.ui.screens.practice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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

@Composable
fun ReadingSection(viewModel: IeltsMasterViewModel) {
    val questions by viewModel.readingQuestions.collectAsStateWithLifecycle()
    var selectedAnswers by remember { mutableStateOf(mapOf<String, String>()) }
    var evaluatedQuestions by remember { mutableStateOf(setOf<String>()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoStories, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "IELTS Academic Reading Module",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Analyze authentic academic passages and test True/False/Not Given and comprehension questions. All answers include comprehensive rationale.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        items(questions, key = { it.questionId }) { question ->
            val selected = selectedAnswers[question.questionId]
            val isEvaluated = evaluatedQuestions.contains(question.questionId)
            val isCorrect = selected.equals(question.correctAnswer, ignoreCase = true)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reading_question_${question.questionId}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            "${question.questionType.replace("_", " ")} • Target Band ${question.targetBand}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (question.passageOrScript.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Text(
                                question.passageOrScript,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        question.questionText,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    val rawOptions = question.optionsJson
                        .removePrefix("[\"")
                        .removeSuffix("\"]")
                        .split("\",\"")

                    rawOptions.forEach { option ->
                        val isThisSelected = selected == option
                        val optionColor = when {
                            !isEvaluated && isThisSelected -> MaterialTheme.colorScheme.primaryContainer
                            isEvaluated && option.equals(question.correctAnswer, ignoreCase = true) -> MaterialTheme.colorScheme.primaryContainer
                            isEvaluated && isThisSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    if (!isEvaluated) {
                                        selectedAnswers = selectedAnswers + (question.questionId to option)
                                    }
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = optionColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isThisSelected,
                                    onClick = {
                                        if (!isEvaluated) {
                                            selectedAnswers = selectedAnswers + (question.questionId to option)
                                        }
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    option,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (!isEvaluated) {
                        Button(
                            onClick = {
                                if (selected != null) {
                                    evaluatedQuestions = evaluatedQuestions + question.questionId
                                }
                            },
                            enabled = selected != null,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Check Answer")
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCorrect) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        if (isCorrect) "Correct! (+15 XP)" else "Incorrect. Correct: ${question.correctAnswer}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "Why this is correct: ${question.explanation}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
