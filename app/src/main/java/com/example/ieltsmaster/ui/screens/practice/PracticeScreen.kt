package com.example.ieltsmaster.ui.screens.practice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    viewModel: IeltsMasterViewModel,
    initialTab: String = "VOCABULARY"
) {
    val tabs = listOf("AI CHAT", "SPEAKING LAB", "VOCABULARY", "GRAMMAR", "LISTENING", "READING", "WRITING", "SPEAKING")
    var selectedTabIndex by remember(initialTab) {
        val idx = tabs.indexOfFirst {
            it.equals(initialTab, ignoreCase = true) ||
            (initialTab.equals("CONVERSATION", ignoreCase = true) && it == "AI CHAT") ||
            (initialTab.equals("COACH", ignoreCase = true) && it == "SPEAKING LAB")
        }
        mutableIntStateOf(if (idx != -1) idx else 2)
    }

    val isDedicatedScreenTab = selectedTabIndex in listOf(0, 1)

    Scaffold(
        topBar = {
            if (!isDedicatedScreenTab) {
                TopAppBar(
                    title = {
                        Text(
                            "Skills Practice Hub",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, tabName ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                tabName.lowercase().split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTabIndex) {
                    0 -> AiConversationScreen(viewModel = viewModel, onBack = { selectedTabIndex = 2 })
                    1 -> SpeakingCoachScreen(viewModel = viewModel, onBack = { selectedTabIndex = 2 })
                    2 -> VocabularySection(viewModel = viewModel)
                    3 -> GrammarSection(viewModel = viewModel)
                    4 -> ListeningSection(viewModel = viewModel)
                    5 -> ReadingSection(viewModel = viewModel)
                    6 -> WritingSection(viewModel = viewModel)
                    7 -> SpeakingSection(viewModel = viewModel)
                }
            }
        }
    }
}
