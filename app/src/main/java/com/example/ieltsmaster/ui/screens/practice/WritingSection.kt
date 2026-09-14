package com.example.ieltsmaster.ui.screens.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ieltsmaster.data.local.entities.WritingTaskEntity
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel
import com.example.ieltsmaster.utils.MarkdownUtils

@Composable
fun WritingSection(viewModel: IeltsMasterViewModel) {
    val tasks by viewModel.allWritingTasks.collectAsStateWithLifecycle()
    var selectedTaskForEditor by remember { mutableStateOf<WritingTaskEntity?>(null) }

    if (selectedTaskForEditor != null) {
        WritingEditorView(
            task = selectedTaskForEditor!!,
            onSave = { draft ->
                viewModel.saveWritingDraft(selectedTaskForEditor!!.taskId, draft)
                selectedTaskForEditor = null
            },
            onCancel = { selectedTaskForEditor = null }
        )
    } else {
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EditNote, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "IELTS Writing Studio (Task 1 & Task 2)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Study Band 9 authentic model essays, master the 4 assessment criteria, and compose your own essay drafts with real-time word counting.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            items(tasks, key = { it.taskId }) { task ->
                WritingTaskCard(
                    task = task,
                    onOpenEditor = { selectedTaskForEditor = task }
                )
            }
        }
    }
}

@Composable
fun WritingTaskCard(
    task: WritingTaskEntity,
    onOpenEditor: () -> Unit
) {
    var showModelAnswer by remember { mutableStateOf(false) }
    val minWords = if (task.taskType.contains("TASK_1")) 150 else 250

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("writing_card_${task.taskId}"),
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
                        task.taskType.replace("_", " "),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    "Min $minWords words",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                MarkdownUtils.stripMarkdown(task.prompt),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (task.scenarioOrDataDescription.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    MarkdownUtils.stripMarkdown(task.scenarioOrDataDescription),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { showModelAnswer = !showModelAnswer }) {
                    Text(if (showModelAnswer) "Hide Band 9 Model" else "Read Band 9 Model")
                    Icon(
                        if (showModelAnswer) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }

                Button(
                    onClick = onOpenEditor,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (task.userSavedDraft.isNotBlank()) "Edit Draft" else "Write Essay")
                }
            }

            AnimatedVisibility(visible = showModelAnswer) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "Band 9 Authentic Sample Answer",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                MarkdownUtils.stripMarkdown(task.sampleBand9Answer),
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 20.sp
                            )

                            if (task.structureChecklist.isNotBlank()) {
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "Structure & Criteria Checklist:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = MarkdownUtils.parseMarkdownToAnnotatedString(task.structureChecklist),
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

@Composable
fun WritingEditorView(
    task: WritingTaskEntity,
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var draftText by remember { mutableStateOf(task.userSavedDraft) }
    val minWords = if (task.taskType.contains("TASK_1")) 150 else 250
    val wordCount = remember(draftText) {
        if (draftText.isBlank()) 0
        else draftText.trim().split("\\s+".toRegex()).size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
            Text(
                "${task.taskType.replace("_", " ")} Editor",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { onSave(draftText) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_writing_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Save")
            }
        }

        Spacer(Modifier.height(12.dp))

        // Prompt Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    task.prompt,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (task.scenarioOrDataDescription.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        task.scenarioOrDataDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Live Word Counter Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Words: $wordCount / $minWords",
                fontWeight = FontWeight.Bold,
                color = if (wordCount >= minWords) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Text(
                if (wordCount < minWords) "Minimum word count not yet met" else "Word count requirement satisfied!",
                fontSize = 12.sp,
                color = if (wordCount >= minWords) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = draftText,
            onValueChange = { draftText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .testTag("writing_draft_input"),
            placeholder = { Text("Start typing your essay or letter draft here...") },
            shape = RoundedCornerShape(12.dp)
        )
    }
}
