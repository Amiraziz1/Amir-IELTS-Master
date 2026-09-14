package com.example.ieltsmaster.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: IeltsMasterViewModel,
    onNavigateToLearn: () -> Unit,
    onNavigateToPractice: (String) -> Unit, // skill: "VOCABULARY", "GRAMMAR", "SPEAKING", "LISTENING", "READING", "WRITING"
    onNavigateToIelts: () -> Unit,
    onOpenLesson: (String) -> Unit,
    onNavigateToConversation: () -> Unit,
    onNavigateToSpeakingCoach: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val allLessons by viewModel.allLessons.collectAsStateWithLifecycle()
    val mockAttempts by viewModel.allMockAttempts.collectAsStateWithLifecycle()

    val nextLesson = remember(allLessons) {
        allLessons.firstOrNull { !it.isCompleted } ?: allLessons.firstOrNull()
    }

    val estimatedBand = remember(mockAttempts) {
        if (mockAttempts.isNotEmpty()) {
            mockAttempts.first().overallBand
        } else {
            6.5f
        }
    }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "$greeting, ${userProfile?.name ?: "Learner"}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Target: IELTS Band ${userProfile?.targetBand ?: 7.5f} (${userProfile?.examType ?: "Academic"})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Streak Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${userProfile?.streakDays ?: 1} Days",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }

                    // Notification trigger for study reminder
                    IconButton(
                        onClick = { viewModel.triggerStudyReminder() },
                        modifier = Modifier.testTag("notification_reminder_button")
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Study Reminder")
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
            // Target & Performance Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Estimated Band",
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        "Band $estimatedBand",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        "Practice Estimate",
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                        fontSize = 11.sp
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            "Target",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            "${userProfile?.targetBand ?: 7.5f}",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Today's Progress: 65%",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { 0.65f },
                                        modifier = Modifier.width(160.dp).height(6.dp).clip(CircleShape),
                                        color = MaterialTheme.colorScheme.tertiary,
                                        trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        "${userProfile?.totalXp ?: 200} XP",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick AI Conversation & Speaking Coach Launchers
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // AI English Conversation Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToConversation() }
                            .testTag("home_ai_conversation_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Chat,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        "Offline AI",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                "AI Conversation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "Voice & text chat with instant language feedback in 9 modes",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                                lineHeight = 15.sp,
                                maxLines = 2
                            )
                        }
                    }

                    // Speaking Coach Cue Card Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToSpeakingCoach() }
                            .testTag("home_speaking_coach_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.RecordVoiceOver,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onTertiary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        "Lab & Rubric",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                "Speaking Coach",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                "Timed cue cards, recording, model answers & 4-criteria rubric",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                                lineHeight = 15.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // Continue Lesson Action Card
            if (nextLesson != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenLesson(nextLesson.id) }
                            .testTag("continue_lesson_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Continue: ${nextLesson.category} – ${nextLesson.level}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    nextLesson.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "~${nextLesson.estimatedMinutes} mins • Boost your grammar and fluency",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Personalized Today's Study Plan
            item {
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
                            Text(
                                "Today's Study Plan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${userProfile?.dailyStudyTimeMinutes ?: 30} mins goal",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        DailyPlanItem(title = "Academic Vocabulary (10 Words)", duration = "10 mins", isDone = true)
                        DailyPlanItem(title = "Grammar: Advanced Conditionals", duration = "10 mins", isDone = true)
                        DailyPlanItem(
                            title = "Speaking Practice (Part 2 Cue Card)",
                            duration = "10 mins",
                            isDone = false,
                            highlight = userProfile?.mainWeakness.equals("Speaking", ignoreCase = true)
                        )
                    }
                }
            }

            // Quick Skill Navigation Grid
            item {
                Text(
                    "English & IELTS Skills",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SkillGridItem(
                        icon = Icons.Default.MenuBook,
                        title = "Vocabulary",
                        subtitle = "Spaced Repetition",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToPractice("VOCABULARY") }
                    )
                    SkillGridItem(
                        icon = Icons.Default.Spellcheck,
                        title = "Grammar",
                        subtitle = "Rules & Exercises",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToPractice("GRAMMAR") }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SkillGridItem(
                        icon = Icons.Default.Headphones,
                        title = "Listening",
                        subtitle = "Audio & Questions",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToPractice("LISTENING") }
                    )
                    SkillGridItem(
                        icon = Icons.Default.AutoStories,
                        title = "Reading",
                        subtitle = "T/F/NG & Headings",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToPractice("READING") }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SkillGridItem(
                        icon = Icons.Default.EditNote,
                        title = "Writing",
                        subtitle = "Task 1 & 2 Models",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToPractice("WRITING") }
                    )
                    SkillGridItem(
                        icon = Icons.Default.Mic,
                        title = "Speaking",
                        subtitle = "Cue Cards & Audio",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToPractice("SPEAKING") }
                    )
                }
            }

            // Weakest Skill Focus Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPractice(userProfile?.mainWeakness?.uppercase() ?: "SPEAKING") },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Weakest Skill Focus: ${userProfile?.mainWeakness ?: "Speaking"}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                "Spend an extra 10 minutes recording responses to elevate fluency to Band 7.5.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Mock Exam Full Simulation Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToIelts() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "IELTS Mock Examination",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "Practice under timed test conditions and calculate your estimated band score.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Button(
                            onClick = { onNavigateToIelts() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Start")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyPlanItem(title: String, duration: String, isDone: Boolean, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Text(
            duration,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SkillGridItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
                subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
