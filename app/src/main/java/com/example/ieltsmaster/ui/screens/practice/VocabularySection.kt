package com.example.ieltsmaster.ui.screens.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.ieltsmaster.data.local.entities.VocabularyWordEntity
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel

@Composable
fun VocabularySection(viewModel: IeltsMasterViewModel) {
    val words by viewModel.filteredWords.collectAsStateWithLifecycle()
    val allWords by viewModel.allWords.collectAsStateWithLifecycle()
    val selectedTopic by viewModel.selectedVocabTopic.collectAsStateWithLifecycle()
    val selectedDifficulty by viewModel.selectedVocabDifficulty.collectAsStateWithLifecycle()

    val topics = listOf("All", "Environment", "Technology", "Health", "Education", "Society", "Government", "Science", "Media")
    val difficulties = listOf("All", "Beginner", "Intermediate", "Advanced", "Academic")

    var selectedWordForDetail by remember { mutableStateOf<VocabularyWordEntity?>(null) }
    var isFlashcardMode by remember { mutableStateOf(false) }
    var flashcardIndex by remember { mutableIntStateOf(0) }
    var isAnswerRevealed by remember { mutableStateOf(false) }

    val knownCount = remember(allWords) { allWords.count { it.masteryStatus == "KNOWN" } }
    val learningCount = remember(allWords) { allWords.count { it.masteryStatus == "LEARNING" } }
    val reviewCount = remember(allWords) { allWords.count { it.masteryStatus == "REVIEW" } }
    val newCount = remember(allWords) { allWords.count { it.masteryStatus == "NEW" } }

    Column(modifier = Modifier.fillMaxSize()) {
        // Spaced Repetition Stats Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatusCounter(label = "New", count = newCount, color = MaterialTheme.colorScheme.secondary)
                StatusCounter(label = "Learning", count = learningCount, color = MaterialTheme.colorScheme.tertiary)
                StatusCounter(label = "Review", count = reviewCount, color = MaterialTheme.colorScheme.error)
                StatusCounter(label = "Known", count = knownCount, color = MaterialTheme.colorScheme.primary)
            }
        }

        // Mode Switch (List vs Flashcards)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (isFlashcardMode) "Flashcard Mode (${flashcardIndex + 1}/${words.size.coerceAtLeast(1)})" else "Word List (${words.size})",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            FilledTonalButton(
                onClick = {
                    isFlashcardMode = !isFlashcardMode
                    isAnswerRevealed = false
                },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(if (isFlashcardMode) Icons.Default.List else Icons.Default.Style, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (isFlashcardMode) "Word List" else "Flashcard Review")
            }
        }

        // Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            topics.forEach { topic ->
                FilterChip(
                    selected = selectedTopic == topic,
                    onClick = { viewModel.selectedVocabTopic.value = topic },
                    label = { Text(topic) },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        if (isFlashcardMode && words.isNotEmpty()) {
            val currentWord = words[flashcardIndex.coerceIn(0, words.size - 1)]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable { isAnswerRevealed = !isAnswerRevealed }
                        .testTag("flashcard_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                currentWord.word,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                currentWord.phonetic,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            IconButton(onClick = { viewModel.speak(currentWord.word) }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = MaterialTheme.colorScheme.primary)
                            }

                            Spacer(Modifier.height(16.dp))

                            if (!isAnswerRevealed) {
                                Text(
                                    "Tap to reveal definition & example",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            } else {
                                Text(
                                    currentWord.meaning,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "\"${currentWord.exampleSentence}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Spaced Repetition Response Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.updateWordMastery(currentWord.word, "REVIEW")
                            isAnswerRevealed = false
                            if (flashcardIndex < words.size - 1) flashcardIndex++ else flashcardIndex = 0
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Review")
                    }

                    Button(
                        onClick = {
                            viewModel.updateWordMastery(currentWord.word, "KNOWN")
                            isAnswerRevealed = false
                            if (flashcardIndex < words.size - 1) flashcardIndex++ else flashcardIndex = 0
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("I Know This (+15 XP)")
                    }
                }
            }
        } else {
            // Word List Mode
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(words, key = { it.word }) { word ->
                    WordItemCard(
                        word = word,
                        onSpeak = { viewModel.speak(word.word) },
                        onClick = { selectedWordForDetail = word }
                    )
                }
            }
        }
    }

    // Word Detail Dialog
    selectedWordForDetail?.let { word ->
        AlertDialog(
            onDismissRequest = { selectedWordForDetail = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(word.word, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("${word.phonetic} • ${word.partOfSpeech}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { viewModel.speak(word.word) }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Definition", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Text(word.meaning, style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(10.dp))
                    Text("Example Sentence", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    Text("\"${word.exampleSentence}\"", style = MaterialTheme.typography.bodyMedium)

                    if (word.collocations.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Text("Collocations", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Text(word.collocations, style = MaterialTheme.typography.bodySmall)
                    }

                    if (word.ieltsRelevance.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Text("IELTS Band Relevance", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.tertiary)
                        Text(word.ieltsRelevance, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateWordMastery(word.word, "KNOWN")
                    selectedWordForDetail = null
                }) {
                    Text("Mark Known (+15 XP)")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedWordForDetail = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun StatusCounter(label: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count.toString(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun WordItemCard(
    word: VocabularyWordEntity,
    onSpeak: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("word_card_${word.word}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(word.word, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(word.phonetic, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    word.meaning,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "${word.topic} • ${word.difficulty}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            IconButton(onClick = onSpeak) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
