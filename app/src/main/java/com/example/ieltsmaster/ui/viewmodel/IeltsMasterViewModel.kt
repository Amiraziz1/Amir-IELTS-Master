package com.example.ieltsmaster.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ieltsmaster.data.local.database.AppDatabase
import com.example.ieltsmaster.data.local.entities.*
import com.example.ieltsmaster.data.repository.IeltsMasterRepository
import com.example.ieltsmaster.utils.AudioRecorderHelper
import com.example.ieltsmaster.utils.BandScoreCalculator
import com.example.ieltsmaster.utils.ConversationalPartner
import com.example.ieltsmaster.utils.GeminiApiService
import com.example.ieltsmaster.utils.LocalConversationEngine
import com.example.ieltsmaster.utils.NotificationHelper
import com.example.ieltsmaster.utils.SpeechRecognizerHelper
import com.example.ieltsmaster.utils.TtsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class IeltsMasterViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = IeltsMasterRepository(database)

    val ttsHelper = TtsHelper(application)
    val audioRecorderHelper = AudioRecorderHelper(application)
    val speechRecognizerHelper = SpeechRecognizerHelper(application)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.initializeIfEmpty()
        }
    }

    // Profile & Onboarding State
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Lessons
    val allLessons: StateFlow<List<LessonEntity>> = repository.allLessons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedLessonCategory = MutableStateFlow("ALL")
    val selectedLessonLevel = MutableStateFlow("All")

    val filteredLessons: StateFlow<List<LessonEntity>> = combine(
        allLessons,
        selectedLessonCategory,
        selectedLessonLevel
    ) { lessons, category, level ->
        lessons.filter { lesson ->
            (category == "ALL" || lesson.category.equals(category, ignoreCase = true)) &&
            (level == "All" || lesson.level.equals(level, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Vocabulary
    val allWords: StateFlow<List<VocabularyWordEntity>> = repository.allWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedVocabTopic = MutableStateFlow("All")
    val selectedVocabDifficulty = MutableStateFlow("All")

    val filteredWords: StateFlow<List<VocabularyWordEntity>> = combine(
        allWords,
        selectedVocabTopic,
        selectedVocabDifficulty
    ) { words, topic, diff ->
        words.filter { word ->
            (topic == "All" || word.topic.equals(topic, ignoreCase = true)) &&
            (diff == "All" || word.difficulty.equals(diff, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Grammar
    val allGrammarTopics: StateFlow<List<GrammarTopicEntity>> = repository.allGrammarTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedGrammarCategory = MutableStateFlow("All")
    val filteredGrammarTopics: StateFlow<List<GrammarTopicEntity>> = combine(
        allGrammarTopics,
        selectedGrammarCategory
    ) { topics, category ->
        if (category == "All") topics else topics.filter { it.category.equals(category, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Speaking
    val allSpeakingTasks: StateFlow<List<SpeakingTaskEntity>> = repository.allSpeakingTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedSpeakingPart = MutableStateFlow(0) // 0 = all, 1, 2, 3
    val filteredSpeakingTasks: StateFlow<List<SpeakingTaskEntity>> = combine(
        allSpeakingTasks,
        selectedSpeakingPart
    ) { tasks, part ->
        if (part == 0) tasks else tasks.filter { it.part == part }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Writing
    val allWritingTasks: StateFlow<List<WritingTaskEntity>> = repository.allWritingTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mock Tests & Attempts
    val allMockTests: StateFlow<List<MockTestEntity>> = repository.allMockTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMockAttempts: StateFlow<List<MockTestAttemptEntity>> = repository.allMockAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Questions for quiz
    val listeningQuestions: StateFlow<List<QuestionEntity>> = repository.getQuestionsByModule("LISTENING")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readingQuestions: StateFlow<List<QuestionEntity>> = repository.getQuestionsByModule("READING")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Achievements
    val allAchievements: StateFlow<List<AchievementEntity>> = repository.allAchievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daily Activities
    val recentActivities: StateFlow<List<DailyActivityEntity>> = repository.recentActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Audio recording state tracker for UI reactivity
    val isRecordingAudio = MutableStateFlow(false)
    val isPlayingAudio = MutableStateFlow(false)
    val recordingVersion = MutableStateFlow(0) // used to invalidate UI cache on new recording

    // AI Conversation & Human Persona States
    val selectedConversationMode = MutableStateFlow(LocalConversationEngine.Mode.DAILY)
    val selectedPartner = MutableStateFlow(ConversationalPartner.EMMA)
    val speechSpeed = MutableStateFlow(1.0f)
    val isAiThinking = MutableStateFlow(false)
    val autoSpeakResponses = MutableStateFlow(true)
    val isSpeechRecognizing = MutableStateFlow(false)
    val speechRmsLevel = MutableStateFlow(0f)
    val speechErrorState = MutableStateFlow<String?>(null)

    val suggestedReplies = MutableStateFlow<List<String>>(
        listOf(
            "I'd love to hear more about that!",
            "To be honest, I completely relate to that.",
            "Could you give me an example or tip?"
        )
    )
    val currentIdiomHighlight = MutableStateFlow<String>("")

    // Live Voice Call Mode States
    val isVoiceCallActive = MutableStateFlow(false)
    val isVoiceCallMuted = MutableStateFlow(false)
    val voiceCallState = MutableStateFlow("IDLE") // IDLE, CONNECTING, LISTENING, THINKING, SPEAKING, PAUSED
    val voiceCallPartnerText = MutableStateFlow("")
    val voiceCallUserText = MutableStateFlow("")

    val conversationMessages: StateFlow<List<AiConversationMessageEntity>> = selectedConversationMode
        .flatMapLatest { mode ->
            repository.getConversationMessages(mode.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved Useful Phrases & Idioms
    val savedPhrases: StateFlow<List<SavedPhraseEntity>> = repository.allSavedPhrases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Speaking Coach Sessions
    val speakingCoachSessions: StateFlow<List<SpeakingCoachSessionEntity>> = repository.allSpeakingCoachSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User Actions
    fun completeOnboarding(
        name: String,
        level: String,
        targetBand: Float,
        examType: String,
        targetDate: String,
        dailyMinutes: Int,
        weakness: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = UserProfile(
                id = 1,
                name = name.ifBlank { "Learner" },
                currentLevel = level,
                targetBand = targetBand,
                examType = examType,
                targetExamDate = targetDate,
                dailyStudyTimeMinutes = dailyMinutes,
                mainWeakness = weakness,
                isOnboarded = true,
                streakDays = 1,
                totalXp = 200,
                lastActiveDate = "2026-09-14"
            )
            repository.saveProfile(updated)
            repository.unlockAchievement("first_step")
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveProfile(profile)
        }
    }

    fun completeLesson(lessonId: String, score: Int = 100) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.completeLesson(lessonId, score)
        }
    }

    fun updateWordMastery(word: String, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWordMastery(word, status)
        }
    }

    fun toggleGrammarMastery(topicId: String, currentStatus: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setTopicMastery(topicId, !currentStatus)
        }
    }

    fun saveWritingDraft(taskId: String, draft: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveWritingDraft(taskId, draft)
        }
    }

    fun submitMockTest(
        testId: String,
        title: String,
        listeningCorrect: Int,
        readingCorrect: Int,
        isAcademic: Boolean = true
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val listeningBand = BandScoreCalculator.calculateListeningBand(listeningCorrect)
            val readingBand = if (isAcademic) {
                BandScoreCalculator.calculateReadingAcademicBand(readingCorrect)
            } else {
                BandScoreCalculator.calculateReadingGeneralBand(readingCorrect)
            }
            val writingBand = 7.0f
            val speakingBand = 7.0f
            val overallBand = BandScoreCalculator.calculateOverallBand(
                listeningBand, readingBand, writingBand, speakingBand
            )
            val scorePct = ((listeningCorrect + readingCorrect).toFloat() / 80f * 100f).toInt().coerceIn(10, 100)

            val attempt = MockTestAttemptEntity(
                testId = testId,
                testTitle = title,
                overallBand = overallBand,
                listeningBand = listeningBand,
                readingBand = readingBand,
                writingBand = writingBand,
                speakingBand = speakingBand,
                scorePercentage = scorePct,
                feedback = "Practice Estimate: Strongest in Listening (Band $listeningBand). Focus on Reading time management and detailed heading matching to reach your target."
            )
            repository.recordMockAttempt(attempt)
            if (overallBand >= 7.0f) {
                repository.unlockAchievement("band7_challenge")
            }
        }
    }

    fun triggerStudyReminder() {
        NotificationHelper.showDailyReminder(getApplication())
    }

    // TTS
    fun speak(text: String, onDone: (() -> Unit)? = null) {
        ttsHelper.configureForPartner(selectedPartner.value)
        ttsHelper.setSpeechSpeed(speechSpeed.value)
        ttsHelper.speak(text, onDone)
    }

    fun setSpeechSpeed(speed: Float) {
        speechSpeed.value = speed
        ttsHelper.setSpeechSpeed(speed)
    }

    fun stopSpeaking() {
        ttsHelper.stop()
    }

    // Audio Recording
    fun startSpeakingRecording(taskId: String) {
        val success = audioRecorderHelper.startRecording(taskId)
        if (success) {
            isRecordingAudio.value = true
        }
    }

    fun stopSpeakingRecording(taskId: String) {
        audioRecorderHelper.stopRecording()
        isRecordingAudio.value = false
        recordingVersion.value += 1
        viewModelScope.launch(Dispatchers.IO) {
            repository.unlockAchievement("speaking_starter")
            repository.addXp(30)
        }
    }

    fun playSpeakingRecording(taskId: String) {
        isPlayingAudio.value = true
        audioRecorderHelper.startPlayback(taskId) {
            isPlayingAudio.value = false
        }
    }

    fun stopSpeakingPlayback() {
        audioRecorderHelper.stopPlayback()
        isPlayingAudio.value = false
    }

    fun deleteSpeakingRecording(taskId: String) {
        audioRecorderHelper.deleteRecording(taskId)
        recordingVersion.value += 1
    }

    fun hasRecording(taskId: String): Boolean {
        // Observe recordingVersion to make Compose re-read
        return audioRecorderHelper.hasRecording(taskId)
    }

    fun resetAllProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.populateDatabase(database)
        }
    }

    // AI Conversation Engine Actions
    fun selectPartner(partner: ConversationalPartner) {
        selectedPartner.value = partner
        ttsHelper.configureForPartner(partner)
        viewModelScope.launch(Dispatchers.IO) {
            checkAndSeedConversationGreeting(selectedConversationMode.value, partner)
        }
    }

    fun setConversationMode(mode: LocalConversationEngine.Mode) {
        selectedConversationMode.value = mode
        viewModelScope.launch(Dispatchers.IO) {
            checkAndSeedConversationGreeting(mode, selectedPartner.value)
        }
    }

    fun checkAndSeedConversationGreeting(
        mode: LocalConversationEngine.Mode,
        partner: ConversationalPartner = selectedPartner.value
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = repository.getConversationMessages(mode.id).first()
            if (existing.isEmpty()) {
                val profile = userProfile.value
                val greeting = LocalConversationEngine.getInitialGreeting(
                    mode = mode,
                    userName = profile?.name ?: "Learner",
                    targetBand = profile?.targetBand ?: 7.5f,
                    partner = partner
                )
                val initialMsg = AiConversationMessageEntity(
                    sessionId = mode.id,
                    mode = mode.id,
                    isUser = false,
                    text = greeting,
                    timestamp = System.currentTimeMillis()
                )
                repository.saveConversationMessage(initialMsg)
            }
        }
    }

    fun sendConversationMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank()) return

        val currentMode = selectedConversationMode.value
        val currentPartner = selectedPartner.value
        val profile = userProfile.value
        val level = profile?.currentLevel ?: "Intermediate"
        val userName = profile?.name ?: "Learner"

        viewModelScope.launch(Dispatchers.IO) {
            // 1. Save user message
            val userMsg = AiConversationMessageEntity(
                sessionId = currentMode.id,
                mode = currentMode.id,
                isUser = true,
                text = trimmed,
                timestamp = System.currentTimeMillis()
            )
            repository.saveConversationMessage(userMsg)

            // Update call text if voice call is running
            if (isVoiceCallActive.value) {
                voiceCallUserText.value = trimmed
                voiceCallState.value = "THINKING"
            }

            // 2. Short natural pause
            isAiThinking.value = true
            delay(400)

            // 3. Process via Gemini (if online with key) or rich local human engine
            val history = repository.getConversationMessages(currentMode.id).first()
            val geminiResponse = GeminiApiService.generatePartnerResponse(
                partner = currentPartner,
                mode = currentMode,
                userName = userName,
                userLevel = level,
                userMessage = trimmed,
                conversationHistory = history
            )

            val finalResponse = geminiResponse ?: LocalConversationEngine.processMessage(
                userInput = trimmed,
                mode = currentMode,
                userLevel = level,
                partner = currentPartner,
                history = history
            )

            isAiThinking.value = false

            // Update suggestions & idiom highlight
            if (finalResponse.suggestedReplies.isNotEmpty()) {
                suggestedReplies.value = finalResponse.suggestedReplies
            }
            if (finalResponse.idiomHighlight.isNotBlank()) {
                currentIdiomHighlight.value = finalResponse.idiomHighlight
            }

            // 4. Save AI reply with feedback
            val aiMsg = AiConversationMessageEntity(
                sessionId = currentMode.id,
                mode = currentMode.id,
                isUser = false,
                text = finalResponse.replyText,
                timestamp = System.currentTimeMillis(),
                feedbackVocabulary = finalResponse.feedback?.vocabularyTip ?: "",
                feedbackGrammar = finalResponse.feedback?.grammarTip ?: "",
                feedbackFluency = finalResponse.feedback?.fluencyObservation ?: "",
                betterVersion = finalResponse.feedback?.betterVersion ?: ""
            )
            repository.saveConversationMessage(aiMsg)

            // 5. Voice Call or Auto-speak handling
            if (isVoiceCallActive.value) {
                voiceCallPartnerText.value = finalResponse.replyText
                voiceCallState.value = "SPEAKING"
                launch(Dispatchers.Main) {
                    speak(finalResponse.replyText) {
                        if (isVoiceCallActive.value && voiceCallState.value != "PAUSED") {
                            launch(Dispatchers.Main) {
                                startVoiceCallListening()
                            }
                        }
                    }
                }
            } else if (autoSpeakResponses.value) {
                launch(Dispatchers.Main) {
                    speak(finalResponse.replyText)
                }
            }
        }
    }

    // Live Hands-Free Voice Call Controls
    fun startVoiceCall() {
        isVoiceCallActive.value = true
        isVoiceCallMuted.value = false
        voiceCallState.value = "CONNECTING"
        voiceCallUserText.value = ""

        val partner = selectedPartner.value
        val profile = userProfile.value
        val greeting = LocalConversationEngine.getInitialGreeting(
            mode = selectedConversationMode.value,
            userName = profile?.name ?: "Learner",
            targetBand = profile?.targetBand ?: 7.5f,
            partner = partner
        )
        voiceCallPartnerText.value = greeting

        viewModelScope.launch(Dispatchers.Main) {
            delay(500)
            voiceCallState.value = "SPEAKING"
            speak(greeting) {
                if (isVoiceCallActive.value && voiceCallState.value != "PAUSED") {
                    startVoiceCallListening()
                }
            }
        }
    }

    fun endVoiceCall() {
        isVoiceCallActive.value = false
        voiceCallState.value = "IDLE"
        ttsHelper.stop()
        speechRecognizerHelper.stopListening()
        isSpeechRecognizing.value = false
    }

    fun toggleVoiceCallMute() {
        isVoiceCallMuted.value = !isVoiceCallMuted.value
        if (isVoiceCallMuted.value) {
            speechRecognizerHelper.stopListening()
            isSpeechRecognizing.value = false
            voiceCallState.value = "PAUSED"
        } else {
            startVoiceCallListening()
        }
    }

    fun startVoiceCallListening() {
        if (!isVoiceCallActive.value || isVoiceCallMuted.value) return
        voiceCallState.value = "LISTENING"
        startListeningToSpeech { recognizedText ->
            if (isVoiceCallActive.value && recognizedText.isNotBlank()) {
                sendConversationMessage(recognizedText)
            }
        }
    }

    fun clearConversationHistory() {
        val currentMode = selectedConversationMode.value
        val currentPartner = selectedPartner.value
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearConversation(currentMode.id)
            val profile = userProfile.value
            val greeting = LocalConversationEngine.getInitialGreeting(
                mode = currentMode,
                userName = profile?.name ?: "Learner",
                targetBand = profile?.targetBand ?: 7.5f,
                partner = currentPartner
            )
            val initialMsg = AiConversationMessageEntity(
                sessionId = currentMode.id,
                mode = currentMode.id,
                isUser = false,
                text = greeting,
                timestamp = System.currentTimeMillis()
            )
            repository.saveConversationMessage(initialMsg)
        }
    }

    // Phrasebook & Idioms
    fun saveUsefulPhrase(phrase: String, meaning: String, context: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveUsefulPhrase(phrase, meaning, context)
            repository.unlockAchievement("vocab_champ")
        }
    }

    fun deleteSavedPhrase(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSavedPhrase(id)
        }
    }

    // Voice Input via SpeechRecognizer
    fun startListeningToSpeech(onRecognized: (String) -> Unit) {
        speechErrorState.value = null
        isSpeechRecognizing.value = true
        speechRecognizerHelper.startListening(
            onResult = { text ->
                isSpeechRecognizing.value = false
                speechRmsLevel.value = 0f
                onRecognized(text)
            },
            onError = { errorMsg ->
                isSpeechRecognizing.value = false
                speechRmsLevel.value = 0f
                speechErrorState.value = errorMsg
                if (isVoiceCallActive.value) {
                    // Retry listening after a brief pause
                    viewModelScope.launch(Dispatchers.Main) {
                        delay(2000)
                        if (isVoiceCallActive.value && voiceCallState.value == "LISTENING") {
                            startVoiceCallListening()
                        }
                    }
                }
            },
            onRmsChanged = { rms ->
                speechRmsLevel.value = rms
            }
        )
    }

    fun stopListeningToSpeech() {
        speechRecognizerHelper.stopListening()
        isSpeechRecognizing.value = false
        speechRmsLevel.value = 0f
    }

    // Speaking Coach Session Saving
    fun saveSpeakingCoachSession(
        taskId: String,
        taskTitle: String,
        part: Int,
        durationSeconds: Int,
        transcript: String,
        fluencyScore: Float,
        vocabScore: Float,
        grammarScore: Float,
        pronunciationScore: Float,
        feedbackNotes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val overall = BandScoreCalculator.calculateOverallBand(
                fluencyScore, vocabScore, grammarScore, pronunciationScore
            )
            val session = SpeakingCoachSessionEntity(
                taskId = taskId,
                taskTitle = taskTitle,
                part = part,
                durationSeconds = durationSeconds,
                userTranscript = transcript,
                fluencyScore = fluencyScore,
                vocabScore = vocabScore,
                grammarScore = grammarScore,
                pronunciationScore = pronunciationScore,
                overallScore = overall,
                feedbackNotes = feedbackNotes
            )
            repository.saveSpeakingCoachSession(session)
        }
    }

    fun logoutOrResetProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val resetProfile = UserProfile(
                id = 1,
                name = "",
                currentLevel = "Intermediate",
                targetBand = 7.5f,
                examType = "Academic",
                targetExamDate = "",
                dailyStudyTimeMinutes = 30,
                mainWeakness = "Speaking",
                isOnboarded = false,
                streakDays = 1,
                totalXp = 100,
                lastActiveDate = ""
            )
            repository.saveProfile(resetProfile)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
        audioRecorderHelper.release()
        speechRecognizerHelper.destroy()
    }
}
