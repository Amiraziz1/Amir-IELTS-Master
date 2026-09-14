package com.example.ieltsmaster.ui.screens.practice

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ieltsmaster.data.local.entities.AiConversationMessageEntity
import com.example.ieltsmaster.data.local.entities.SavedPhraseEntity
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel
import com.example.ieltsmaster.utils.ConversationalPartner
import com.example.ieltsmaster.utils.LocalConversationEngine
import com.example.ieltsmaster.utils.MarkdownUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiConversationScreen(
    viewModel: IeltsMasterViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentMode by viewModel.selectedConversationMode.collectAsStateWithLifecycle()
    val selectedPartner by viewModel.selectedPartner.collectAsStateWithLifecycle()
    val speechSpeed by viewModel.speechSpeed.collectAsStateWithLifecycle()
    val messages by viewModel.conversationMessages.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
    val autoSpeak by viewModel.autoSpeakResponses.collectAsStateWithLifecycle()
    val isSpeechRecognizing by viewModel.isSpeechRecognizing.collectAsStateWithLifecycle()
    val speechRms by viewModel.speechRmsLevel.collectAsStateWithLifecycle()
    val speechError by viewModel.speechErrorState.collectAsStateWithLifecycle()
    val suggestedReplies by viewModel.suggestedReplies.collectAsStateWithLifecycle()
    val idiomHighlight by viewModel.currentIdiomHighlight.collectAsStateWithLifecycle()
    val isVoiceCallActive by viewModel.isVoiceCallActive.collectAsStateWithLifecycle()
    val voiceCallState by viewModel.voiceCallState.collectAsStateWithLifecycle()
    val voiceCallPartnerText by viewModel.voiceCallPartnerText.collectAsStateWithLifecycle()
    val voiceCallUserText by viewModel.voiceCallUserText.collectAsStateWithLifecycle()
    val isVoiceCallMuted by viewModel.isVoiceCallMuted.collectAsStateWithLifecycle()
    val savedPhrases by viewModel.savedPhrases.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }
    var showPartnerSelector by remember { mutableStateOf(false) }
    var showSavedPhrasesSheet by remember { mutableStateOf(false) }
    var shadowingTargetSentence by remember { mutableStateOf<String?>(null) }
    var showSpeedMenu by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Ensure conversation has greeting on open
    LaunchedEffect(currentMode, selectedPartner) {
        viewModel.checkAndSeedConversationGreeting(currentMode, selectedPartner)
    }

    // Scroll to bottom when new messages arrive or when thinking
    LaunchedEffect(messages.size, isAiThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Stop speaking when navigating away
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopSpeaking()
            viewModel.stopListeningToSpeech()
            if (viewModel.isVoiceCallActive.value) {
                viewModel.endVoiceCall()
            }
        }
    }

    // Audio Permission Launcher for normal mic
    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListeningToSpeech { recognizedText ->
                inputText = recognizedText
            }
        } else {
            Toast.makeText(context, "Microphone permission is required for voice chat.", Toast.LENGTH_SHORT).show()
        }
    }

    // Audio Permission Launcher for Voice Call
    val voiceCallPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceCall()
        } else {
            Toast.makeText(context, "Microphone permission is needed to start a voice call.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showPartnerSelector = true }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(selectedPartner.colorHex).copy(alpha = 0.2f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(selectedPartner.avatarEmoji, fontSize = 20.sp)
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    selectedPartner.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch partner",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                "${selectedPartner.accent} • ${currentMode.title}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Live Voice Call Action Button
                    IconButton(
                        onClick = {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasPermission) {
                                viewModel.startVoiceCall()
                            } else {
                                voiceCallPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        modifier = Modifier.testTag("start_voice_call_btn")
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Live Voice Call",
                            tint = Color(0xFF2E7D32)
                        )
                    }

                    // Saved Phrases Notebook Button
                    IconButton(
                        onClick = { showSavedPhrasesSheet = true },
                        modifier = Modifier.testTag("saved_phrases_btn")
                    ) {
                        Icon(
                            Icons.Default.Bookmarks,
                            contentDescription = "Saved Phrases",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // TTS Speed Control
                    Box {
                        IconButton(onClick = { showSpeedMenu = true }) {
                            Icon(
                                Icons.Default.Speed,
                                contentDescription = "Playback Speed",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showSpeedMenu,
                            onDismissRequest = { showSpeedMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("0.8x (Slow & Clear)") },
                                onClick = {
                                    viewModel.setSpeechSpeed(0.8f)
                                    showSpeedMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("1.0x (Normal Pace)") },
                                onClick = {
                                    viewModel.setSpeechSpeed(1.0f)
                                    showSpeedMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("1.2x (Fast & Natural)") },
                                onClick = {
                                    viewModel.setSpeechSpeed(1.2f)
                                    showSpeedMenu = false
                                }
                            )
                        }
                    }

                    // Toggle Auto-Speak
                    IconButton(
                        onClick = { viewModel.autoSpeakResponses.value = !autoSpeak },
                        modifier = Modifier.testTag("toggle_tts_button")
                    ) {
                        Icon(
                            imageVector = if (autoSpeak) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (autoSpeak) "TTS enabled" else "TTS disabled",
                            tint = if (autoSpeak) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }

                    // Clear Chat
                    IconButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.testTag("clear_chat_button")
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Chat")
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
            // Conversational Partner Info Bar (Tappable Banner)
            Surface(
                color = Color(selectedPartner.colorHex).copy(alpha = 0.08f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPartnerSelector = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Speaking with ${selectedPartner.role}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        "Change Partner",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Mode Selector Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(LocalConversationEngine.Mode.entries) { mode ->
                    val isSelected = mode == currentMode
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                viewModel.setConversationMode(mode)
                            }
                        },
                        label = { Text(mode.title, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Error Banner if Speech Recognizer reported error
            AnimatedVisibility(visible = speechError != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            speechError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.speechErrorState.value = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Chat Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubbleItem(
                        message = msg,
                        partner = selectedPartner,
                        onReplayAudio = { text -> viewModel.speak(text) },
                        onShadowPractice = { text -> shadowingTargetSentence = text },
                        onSavePhrase = { phrase, meaning ->
                            viewModel.saveUsefulPhrase(phrase, meaning, msg.text)
                            Toast.makeText(context, "Saved to Phrasebook!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                if (isAiThinking) {
                    item {
                        AiThinkingBubble(partner = selectedPartner)
                    }
                }
            }

            // Bottom Section: Quick Response Suggestion Chips & Text Input
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 8.dp)
                ) {
                    // Suggested Quick Response Chips ("What to say next")
                    if (suggestedReplies.isNotEmpty()) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 2.dp)
                            ) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFFF57C00),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Suggested replies (tap to speak or edit):",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                contentPadding = PaddingValues(vertical = 2.dp)
                            ) {
                                items(suggestedReplies) { replySuggestion ->
                                    SuggestionChip(
                                        onClick = { inputText = replySuggestion },
                                        label = {
                                            Text(
                                                replySuggestion,
                                                fontSize = 12.sp,
                                                maxLines = 1
                                            )
                                        },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Recording pulsing indicator if speech recognizer is active
                    if (isSpeechRecognizing) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            PulsingMicWave(rms = speechRms)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Listening... Speak naturally in English",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Text & Voice Input Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Microphone Button
                        IconButton(
                            onClick = {
                                if (isSpeechRecognizing) {
                                    viewModel.stopListeningToSpeech()
                                } else {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        viewModel.startListeningToSpeech { recognized ->
                                            inputText = recognized
                                        }
                                    } else {
                                        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSpeechRecognizing) MaterialTheme.colorScheme.errorContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .testTag("mic_input_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeechRecognizing) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = if (isSpeechRecognizing) "Stop listening" else "Voice input",
                                tint = if (isSpeechRecognizing) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Text Field Input
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_text_input"),
                            placeholder = {
                                Text(
                                    currentMode.promptHint,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            },
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 3
                        )

                        // Send Button
                        FilledIconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    val textToSend = inputText
                                    inputText = ""
                                    viewModel.sendConversationMessage(textToSend)
                                }
                            },
                            enabled = inputText.isNotBlank(),
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("chat_send_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send message")
                        }
                    }
                }
            }
        }
    }

    // Hands-Free "Live Voice Call Mode" Overlay
    if (isVoiceCallActive) {
        LiveVoiceCallOverlay(
            partner = selectedPartner,
            callState = voiceCallState,
            partnerSpeech = voiceCallPartnerText,
            userSpeech = voiceCallUserText,
            isMuted = isVoiceCallMuted,
            speechRms = speechRms,
            onToggleMute = { viewModel.toggleVoiceCallMute() },
            onEndCall = { viewModel.endVoiceCall() }
        )
    }

    // Partner Selection Sheet / Dialog
    if (showPartnerSelector) {
        PartnerSelectionDialog(
            currentPartner = selectedPartner,
            onSelect = { newPartner ->
                viewModel.selectPartner(newPartner)
                showPartnerSelector = false
            },
            onDismiss = { showPartnerSelector = false }
        )
    }

    // Saved Useful Phrases Sheet
    if (showSavedPhrasesSheet) {
        SavedPhrasesSheet(
            phrases = savedPhrases,
            onPlayAudio = { text -> viewModel.speak(text) },
            onDelete = { id -> viewModel.deleteSavedPhrase(id) },
            onDismiss = { showSavedPhrasesSheet = false }
        )
    }

    // Pronunciation & Shadowing Drill Dialog
    shadowingTargetSentence?.let { targetSentence ->
        ShadowingPracticeDialog(
            targetSentence = targetSentence,
            viewModel = viewModel,
            onDismiss = { shadowingTargetSentence = null }
        )
    }

    // Confirmation dialog for clearing chat
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Conversation?") },
            text = { Text("This will remove message history for ${currentMode.title} and reset the conversation.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearConversationHistory()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatBubbleItem(
    message: AiConversationMessageEntity,
    partner: ConversationalPartner,
    onReplayAudio: (String) -> Unit,
    onShadowPractice: (String) -> Unit,
    onSavePhrase: (String, String) -> Unit
) {
    var showFeedbackDetails by remember { mutableStateOf(false) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    val hasFeedback = message.feedbackVocabulary.isNotBlank() ||
            message.feedbackGrammar.isNotBlank() ||
            message.feedbackFluency.isNotBlank() ||
            message.betterVersion.isNotBlank()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        // Label with partner name for assistant messages
        if (!message.isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            ) {
                Text(partner.avatarEmoji, fontSize = 13.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    partner.displayName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier.widthIn(max = 330.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 16.dp
                ),
                color = if (message.isUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = if (message.isUser) 2.dp else 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Text(
                        text = MarkdownUtils.stripMarkdown(message.text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (message.isUser) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 21.sp
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // AI Bubble Interactive Actions (Listen, Shadow, Save)
                        if (!message.isUser) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Audio Replay
                                Icon(
                                    Icons.Default.VolumeUp,
                                    contentDescription = "Listen to audio",
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { onReplayAudio(MarkdownUtils.stripMarkdown(message.text)) },
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                // Practice Speaking / Shadowing
                                Icon(
                                    Icons.Default.RecordVoiceOver,
                                    contentDescription = "Practice speaking this",
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { onShadowPractice(MarkdownUtils.stripMarkdown(message.text)) },
                                    tint = Color(0xFF00897B)
                                )

                                // Bookmark phrase
                                Icon(
                                    Icons.Default.BookmarkAdd,
                                    contentDescription = "Save phrase",
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            val clean = MarkdownUtils.stripMarkdown(message.text)
                                            val shortPhrase = clean.take(60)
                                            onSavePhrase(shortPhrase, "Conversational dialogue from ${partner.displayName}")
                                        },
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        } else {
                            Spacer(Modifier.width(1.dp))
                        }

                        // Timestamp
                        Text(
                            formattedTime,
                            fontSize = 10.sp,
                            color = (if (message.isUser) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Expandable Language Coaching Badge for User Messages
        if (hasFeedback) {
            Spacer(Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
                modifier = Modifier
                    .widthIn(max = 330.dp)
                    .clickable { showFeedbackDetails = !showFeedbackDetails }
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "English Coaching & Analysis",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Icon(
                            if (showFeedbackDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (showFeedbackDetails) {
                        Spacer(Modifier.height(6.dp))

                        if (message.feedbackGrammar.isNotBlank()) {
                            Text(
                                "Grammar: " + MarkdownUtils.stripMarkdown(message.feedbackGrammar),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(Modifier.height(4.dp))
                        }

                        if (message.feedbackVocabulary.isNotBlank()) {
                            Text(
                                "Vocabulary: " + MarkdownUtils.stripMarkdown(message.feedbackVocabulary),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(Modifier.height(4.dp))
                        }

                        if (message.feedbackFluency.isNotBlank()) {
                            Text(
                                "Fluency: " + MarkdownUtils.stripMarkdown(message.feedbackFluency),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(Modifier.height(4.dp))
                        }

                        if (message.betterVersion.isNotBlank()) {
                            Text(
                                "Natural Phrasing: " + MarkdownUtils.stripMarkdown(message.betterVersion),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiThinkingBubble(partner: ConversationalPartner) {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)) {
            Text(partner.avatarEmoji, fontSize = 12.sp)
            Spacer(Modifier.width(4.dp))
            Text(
                "${partner.displayName} is formulating a reply...",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = dot1Alpha))
                )
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = dot2Alpha))
                )
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = dot3Alpha))
                )
            }
        }
    }
}

// Hands-Free Full-Screen Voice Call Overlay
@Composable
fun LiveVoiceCallOverlay(
    partner: ConversationalPartner,
    callState: String,
    partnerSpeech: String,
    userSpeech: String,
    isMuted: Boolean,
    speechRms: Float,
    onToggleMute: () -> Unit,
    onEndCall: () -> Unit
) {
    Dialog(
        onDismissRequest = onEndCall,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF101418)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse_rings")
            val ringScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.35f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            val statusColor by animateColorAsState(
                targetValue = when (callState) {
                    "SPEAKING" -> Color(0xFFAB47BC)
                    "LISTENING" -> Color(0xFF4CAF50)
                    "THINKING" -> Color(0xFFFFA726)
                    else -> Color(0xFF78909C)
                },
                label = "statusColor"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Call Header
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = when (callState) {
                                    "SPEAKING" -> "${partner.displayName} is speaking..."
                                    "LISTENING" -> "Listening to you... Speak now!"
                                    "THINKING" -> "${partner.displayName} is thinking..."
                                    "PAUSED" -> "Call Paused"
                                    else -> "Connecting audio..."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        partner.displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "${partner.role} • ${partner.accent}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Middle: Animated Soundwave Avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(220.dp)
                ) {
                    // Outer pulse ring
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val extraRms = (speechRms.coerceIn(0f, 10f) / 10f) * 20f
                        val baseRadius = (size.minDimension / 2.8f) * ringScale + extraRms
                        drawCircle(
                            color = statusColor.copy(alpha = 0.2f),
                            radius = baseRadius,
                            center = center
                        )
                        drawCircle(
                            color = statusColor.copy(alpha = 0.35f),
                            radius = baseRadius * 0.85f,
                            center = center
                        )
                    }

                    // Main Avatar Circle
                    Surface(
                        shape = CircleShape,
                        color = Color(partner.colorHex),
                        border = BorderStroke(3.dp, Color.White.copy(alpha = 0.4f)),
                        modifier = Modifier.size(110.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(partner.avatarEmoji, fontSize = 54.sp)
                        }
                    }
                }

                // Spoken Transcript Preview
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (callState == "LISTENING" && userSpeech.isNotBlank()) {
                                "You: \"$userSpeech\""
                            } else if (partnerSpeech.isNotBlank()) {
                                "\"${MarkdownUtils.stripMarkdown(partnerSpeech)}\""
                            } else {
                                "Hands-free continuous conversation mode is active. Speak into your microphone."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Bottom Call Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute / Pause Button
                    FilledIconButton(
                        onClick = onToggleMute,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isMuted) Color(0xFFE53935) else Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isMuted) "Unmute" else "Mute",
                            tint = Color.White
                        )
                    }

                    // Hang Up Button
                    FilledIconButton(
                        onClick = onEndCall,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color(0xFFD32F2F)
                        ),
                        modifier = Modifier.size(68.dp)
                    ) {
                        Icon(
                            Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

// Partner Selection Dialog
@Composable
fun PartnerSelectionDialog(
    currentPartner: ConversationalPartner,
    onSelect: (ConversationalPartner) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Choose Conversational Partner",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    "Each partner has a distinct accent, conversational style, and role for spoken English practice.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ConversationalPartner.entries.forEach { partner ->
                    val isSelected = partner == currentPartner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(partner) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(partner.avatarEmoji, fontSize = 28.sp)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    partner.displayName,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    partner.role,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    partner.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// Saved Useful Phrases Sheet / Dialog
@Composable
fun SavedPhrasesSheet(
    phrases: List<SavedPhraseEntity>,
    onPlayAudio: (String) -> Unit,
    onDelete: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bookmarks, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("My Saved Phrases & Idioms", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            if (phrases.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No saved phrases yet! Tap the bookmark icon on any AI response during conversation to save useful collocations and idioms.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 380.dp)
                ) {
                    items(phrases, key = { it.id }) { phraseItem ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        phraseItem.phrase,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (phraseItem.meaning.isNotBlank()) {
                                        Text(
                                            phraseItem.meaning,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Row {
                                    IconButton(
                                        onClick = { onPlayAudio(phraseItem.phrase) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.VolumeUp,
                                            contentDescription = "Listen",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDelete(phraseItem.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

// Pronunciation & Shadowing Drill Dialog
@Composable
fun ShadowingPracticeDialog(
    targetSentence: String,
    viewModel: IeltsMasterViewModel,
    onDismiss: () -> Unit
) {
    var recognizedSpokenText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var matchScore by remember { mutableStateOf<Int?>(null) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color(0xFF00897B))
                Spacer(Modifier.width(8.dp))
                Text("Pronunciation Shadowing Drill", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Listen to the sentence, then tap the microphone and repeat it aloud to test your clarity and pronunciation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "\"$targetSentence\"",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Listen Button
                    Button(
                        onClick = { viewModel.speak(targetSentence) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Listen First")
                    }
                }

                // User attempt result
                if (recognizedSpokenText.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                "You said: \"$recognizedSpokenText\"",
                                style = MaterialTheme.typography.bodySmall
                            )
                            matchScore?.let { score ->
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Clarity & Accuracy: $score% match!",
                                    fontWeight = FontWeight.Bold,
                                    color = if (score >= 75) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            // Record Button
            Button(
                onClick = {
                    if (isListening) {
                        viewModel.stopListeningToSpeech()
                        isListening = false
                    } else {
                        val hasPerm = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPerm) {
                            isListening = true
                            viewModel.startListeningToSpeech { spoken ->
                                isListening = false
                                recognizedSpokenText = spoken
                                matchScore = calculateSentenceMatch(targetSentence, spoken)
                            }
                        } else {
                            Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(if (isListening) "Listening..." else "Record Repeat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// Sentence similarity comparison for pronunciation shadowing
private fun calculateSentenceMatch(reference: String, spoken: String): Int {
    val cleanRef = reference.lowercase().replace("[^a-z0-9 ]".toRegex(), "").split("\\s+".toRegex()).toSet()
    val cleanSpoken = spoken.lowercase().replace("[^a-z0-9 ]".toRegex(), "").split("\\s+".toRegex()).toSet()
    if (cleanRef.isEmpty()) return 100
    val common = cleanRef.intersect(cleanSpoken).size
    return ((common.toFloat() / cleanRef.size.toFloat()) * 100).toInt().coerceIn(10, 100)
}

@Composable
fun PulsingMicWave(rms: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = Modifier.size(24.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val dynamicRadius = (size.minDimension / 2.5f) * (pulseScale + (rms.coerceIn(0f, 10f) / 20f))

        drawCircle(
            color = primaryColor.copy(alpha = 0.25f),
            radius = dynamicRadius,
            center = center
        )
        drawCircle(
            color = primaryColor,
            radius = size.minDimension / 4f,
            center = center
        )
    }
}
