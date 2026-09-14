package com.example.ieltsmaster.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: IeltsMasterViewModel,
    onComplete: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("Intermediate") }
    var selectedExamType by remember { mutableStateOf("Academic") }
    var targetBand by remember { mutableFloatStateOf(7.5f) }
    var dailyMinutes by remember { mutableIntStateOf(30) }
    var mainWeakness by remember { mutableStateOf("Speaking") }
    var targetExamDate by remember { mutableStateOf("In 3 Months") }

    val levels = listOf("Beginner", "Elementary", "Intermediate", "Upper Intermediate", "Advanced")
    val bandOptions = listOf(6.0f, 6.5f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f)
    val studyTimes = listOf(15, 30, 45, 60)
    val weaknesses = listOf("Speaking", "Writing", "Listening", "Reading", "Grammar", "Vocabulary", "Pronunciation")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Personalize Your IELTS Journey",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    if (step > 1) {
                        IconButton(onClick = { step-- }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step Indicator
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in 1..3) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (step == i) 32.dp else 12.dp)
                            .clip(CircleShape)
                            .background(
                                if (step >= i) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }

            AnimatedContent(targetState = step, label = "OnboardingStepAnimation") { targetStep ->
                when (targetStep) {
                    1 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.School,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Welcome to IELTS Master",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "From foundational English to IELTS Band 7+",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("What is your name?") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("onboarding_name_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(Modifier.height(24.dp))
                            Text(
                                "Select your current English proficiency:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))

                            levels.forEach { level ->
                                val isSelected = selectedLevel == level
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { selectedLevel = level },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            level,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isSelected) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Your IELTS Target",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Tailoring mock exams and curriculum for your goals",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                            )

                            Text(
                                "Exam Module:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                listOf("Academic", "General Training").forEach { type ->
                                    val isSelected = selectedExamType == type
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedExamType = type },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                type,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))
                            Text(
                                "Target IELTS Band Score: $targetBand",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                bandOptions.take(4).forEach { band ->
                                    FilterChip(
                                        selected = targetBand == band,
                                        onClick = { targetBand = band },
                                        label = { Text("Band $band") }
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                bandOptions.drop(4).forEach { band ->
                                    FilterChip(
                                        selected = targetBand == band,
                                        onClick = { targetBand = band },
                                        label = { Text("Band $band") }
                                    )
                                }
                            }

                            Spacer(Modifier.height(24.dp))
                            Text(
                                "Target Exam Date:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            listOf("In 1 Month", "In 3 Months", "In 6 Months", "Flexible / Long-term").forEach { timeframe ->
                                val isSelected = targetExamDate == timeframe
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { targetExamDate = timeframe },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(timeframe, modifier = Modifier.weight(1f))
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Study Plan & Focus",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "We will allocate daily practice according to your weakness",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                            )

                            Text(
                                "Daily Study Commitment:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                studyTimes.forEach { mins ->
                                    FilterChip(
                                        selected = dailyMinutes == mins,
                                        onClick = { dailyMinutes = mins },
                                        label = { Text("$mins mins") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Spacer(Modifier.height(24.dp))
                            Text(
                                "What is your main weakness?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))

                            weaknesses.forEach { weakness ->
                                val isSelected = mainWeakness == weakness
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { mainWeakness = weakness },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(weakness, modifier = Modifier.weight(1f))
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (step < 3) {
                        step++
                    } else {
                        viewModel.completeOnboarding(
                            name = name,
                            level = selectedLevel,
                            targetBand = targetBand,
                            examType = selectedExamType,
                            targetDate = targetExamDate,
                            dailyMinutes = dailyMinutes,
                            weakness = mainWeakness
                        )
                        onComplete()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_continue_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (step < 3) "Next Step" else "Generate My Learning Plan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    if (step < 3) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Star,
                    contentDescription = null
                )
            }
        }
    }
}
