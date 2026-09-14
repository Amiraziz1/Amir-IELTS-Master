package com.example.ieltsmaster.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ieltsmaster.data.local.entities.AchievementEntity
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(viewModel: IeltsMasterViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val achievements by viewModel.allAchievements.collectAsStateWithLifecycle()
    val dailyActivities by viewModel.recentActivities.collectAsStateWithLifecycle()
    val allLessons by viewModel.allLessons.collectAsStateWithLifecycle()
    val allWords by viewModel.allWords.collectAsStateWithLifecycle()

    val completedLessonsCount = remember(allLessons) { allLessons.count { it.isCompleted } }
    val knownWordsCount = remember(allWords) { allWords.count { it.masteryStatus == "KNOWN" } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Analytics & Progress",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Track your growth toward IELTS Band ${userProfile?.targetBand ?: 7.5f}",
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
            // Overall Stats Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Total Experience",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    "${userProfile?.totalXp ?: 200} XP",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "Level: ${userProfile?.currentLevel ?: "Intermediate"}",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Progress Metric Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricBox(label = "Streak", value = "${userProfile?.streakDays ?: 1} Days")
                            MetricBox(label = "Lessons Done", value = "$completedLessonsCount / ${allLessons.size}")
                            MetricBox(label = "Words Mastered", value = "$knownWordsCount")
                        }
                    }
                }
            }

            // Skill Proficiency Breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Skill Mastery Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(14.dp))

                        SkillProgressBar(skill = "Listening (Detail extraction & Flow)", progress = 0.78f, bandEstimate = "7.5")
                        SkillProgressBar(skill = "Reading (Academic comprehension & T/F/NG)", progress = 0.72f, bandEstimate = "7.0")
                        SkillProgressBar(skill = "Writing (Grammatical range & Task 2)", progress = 0.65f, bandEstimate = "6.5")
                        SkillProgressBar(skill = "Speaking (Fluency & Cue Card response)", progress = 0.70f, bandEstimate = "7.0")
                        SkillProgressBar(skill = "Grammar & Sentence Complexity", progress = 0.85f, bandEstimate = "8.0")
                        SkillProgressBar(skill = "Academic Vocabulary (Collocations)", progress = 0.80f, bandEstimate = "7.5")
                    }
                }
            }

            // Weekly Study Activity Bar Chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Weekly Study Activity (Minutes)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(16.dp))

                        val days = listOf("Mon" to 35, "Tue" to 40, "Wed" to 25, "Thu" to 50, "Fri" to 30, "Sat" to 45, "Sun" to 60)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            days.forEach { (day, minutes) ->
                                val barHeightFraction = (minutes / 60f).coerceIn(0.1f, 1f)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("${minutes}m", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                    Spacer(Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(18.dp)
                                            .fillMaxHeight(barHeightFraction)
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(day, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            // Gamification Achievements Showcase
            item {
                Text(
                    "Achievements & Badges",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(achievements, key = { it.code }) { achievement ->
                AchievementItemCard(achievement = achievement)
            }
        }
    }
}

@Composable
private fun MetricBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f))
    }
}

@Composable
private fun SkillProgressBar(skill: String, progress: Float, bandEstimate: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(skill, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("Band $bandEstimate", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun AchievementItemCard(achievement: AchievementEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (achievement.isUnlocked) MaterialTheme.colorScheme.tertiaryContainer
                else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (achievement.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (achievement.isUnlocked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    achievement.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    achievement.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (achievement.isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    "+${achievement.xpReward} XP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (achievement.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}
