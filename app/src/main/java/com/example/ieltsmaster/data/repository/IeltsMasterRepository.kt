package com.example.ieltsmaster.data.repository

import com.example.ieltsmaster.data.local.database.AppDatabase
import com.example.ieltsmaster.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IeltsMasterRepository(private val database: AppDatabase) {

    // User Profile
    val userProfile: Flow<UserProfile?> = database.userProfileDao().getUserProfile()

    suspend fun getUserProfileOnce(): UserProfile? {
        return database.userProfileDao().getUserProfileOnce()
    }

    suspend fun saveProfile(profile: UserProfile) {
        database.userProfileDao().insertOrUpdateProfile(profile)
    }

    suspend fun addXp(xp: Int) {
        database.userProfileDao().addXp(xp)
    }

    // Lessons
    val allLessons: Flow<List<LessonEntity>> = database.lessonDao().getAllLessons()

    fun getLessonsByCategory(category: String): Flow<List<LessonEntity>> =
        database.lessonDao().getLessonsByCategory(category)

    fun getLessonsByLevel(level: String): Flow<List<LessonEntity>> =
        database.lessonDao().getLessonsByLevel(level)

    suspend fun getLessonById(id: String): LessonEntity? =
        database.lessonDao().getLessonById(id)

    suspend fun completeLesson(lessonId: String, score: Int) {
        database.lessonDao().updateLessonProgress(lessonId, completed = true, score = score)
        database.userProfileDao().addXp(50)
        logStudyMinutes(15, completedLesson = true, learnedWord = false)
    }

    // Vocabulary
    val allWords: Flow<List<VocabularyWordEntity>> = database.vocabularyDao().getAllWords()

    fun getWordsByTopic(topic: String): Flow<List<VocabularyWordEntity>> =
        database.vocabularyDao().getWordsByTopic(topic)

    fun getWordsByStatus(status: String): Flow<List<VocabularyWordEntity>> =
        database.vocabularyDao().getWordsByStatus(status)

    fun countWordsByStatus(status: String): Flow<Int> =
        database.vocabularyDao().countWordsByStatus(status)

    suspend fun updateWordMastery(word: String, status: String) {
        val nextReview = when (status) {
            "KNOWN" -> System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L) // 7 days
            "LEARNING" -> System.currentTimeMillis() + (1 * 24 * 60 * 60 * 1000L) // 1 day
            "REVIEW" -> System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000L)
            else -> System.currentTimeMillis()
        }
        database.vocabularyDao().updateWordMastery(word, status, nextReview)
        if (status == "KNOWN" || status == "LEARNING") {
            database.userProfileDao().addXp(15)
            logStudyMinutes(5, completedLesson = false, learnedWord = true)
        }
    }

    // Grammar
    val allGrammarTopics: Flow<List<GrammarTopicEntity>> = database.grammarDao().getAllTopics()

    fun getGrammarTopicsByCategory(category: String): Flow<List<GrammarTopicEntity>> =
        database.grammarDao().getTopicsByCategory(category)

    suspend fun getGrammarTopicById(topicId: String): GrammarTopicEntity? =
        database.grammarDao().getTopicById(topicId)

    suspend fun setTopicMastery(topicId: String, isMastered: Boolean) {
        database.grammarDao().updateTopicMastery(topicId, isMastered)
        if (isMastered) {
            database.userProfileDao().addXp(30)
        }
    }

    // Questions & Practice
    fun getQuestionsByModule(module: String): Flow<List<QuestionEntity>> =
        database.questionDao().getQuestionsByModule(module)

    suspend fun getQuestionsForTopic(topicId: String): List<QuestionEntity> =
        database.questionDao().getQuestionsForTopic(topicId)

    // Speaking
    val allSpeakingTasks: Flow<List<SpeakingTaskEntity>> = database.speakingTaskDao().getAllSpeakingTasks()

    fun getSpeakingTasksByPart(part: Int): Flow<List<SpeakingTaskEntity>> =
        database.speakingTaskDao().getSpeakingTasksByPart(part)

    // Writing
    val allWritingTasks: Flow<List<WritingTaskEntity>> = database.writingTaskDao().getAllWritingTasks()

    fun getWritingTasksByType(type: String): Flow<List<WritingTaskEntity>> =
        database.writingTaskDao().getWritingTasksByType(type)

    suspend fun saveWritingDraft(taskId: String, draft: String) {
        database.writingTaskDao().saveDraft(taskId, draft)
        database.userProfileDao().addXp(20)
    }

    // Mock Tests
    val allMockTests: Flow<List<MockTestEntity>> = database.mockTestDao().getAllMockTests()
    val allMockAttempts: Flow<List<MockTestAttemptEntity>> = database.mockTestDao().getAllAttempts()

    suspend fun getMockTestById(testId: String): MockTestEntity? =
        database.mockTestDao().getMockTestById(testId)

    suspend fun recordMockAttempt(attempt: MockTestAttemptEntity) {
        database.mockTestDao().insertAttempt(attempt)
        database.userProfileDao().addXp(100)
        logStudyMinutes(45, completedLesson = true, learnedWord = false)
    }

    // Achievements
    val allAchievements: Flow<List<AchievementEntity>> = database.achievementDao().getAllAchievements()

    suspend fun unlockAchievement(code: String) {
        database.achievementDao().unlockAchievement(code)
    }

    // Daily Activity
    val recentActivities: Flow<List<DailyActivityEntity>> = database.dailyActivityDao().getRecentActivities()

    private suspend fun logStudyMinutes(minutes: Int, completedLesson: Boolean, learnedWord: Boolean) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val currentActivity = database.dailyActivityDao().getActivityForDate(today)
        val updatedActivity = if (currentActivity != null) {
            currentActivity.copy(
                minutesStudied = currentActivity.minutesStudied + minutes,
                lessonsCompleted = currentActivity.lessonsCompleted + (if (completedLesson) 1 else 0),
                wordsLearned = currentActivity.wordsLearned + (if (learnedWord) 1 else 0),
                xpEarned = currentActivity.xpEarned + (minutes * 2)
            )
        } else {
            DailyActivityEntity(
                date = today,
                minutesStudied = minutes,
                lessonsCompleted = if (completedLesson) 1 else 0,
                wordsLearned = if (learnedWord) 1 else 0,
                xpEarned = minutes * 2
            )
        }
        database.dailyActivityDao().recordActivity(updatedActivity)
    }

    // AI Conversation Messages
    fun getConversationMessages(sessionId: String): Flow<List<AiConversationMessageEntity>> =
        database.conversationDao().getMessagesForSession(sessionId)

    suspend fun saveConversationMessage(message: AiConversationMessageEntity): Long {
        val id = database.conversationDao().insertMessage(message)
        if (message.isUser) {
            database.userProfileDao().addXp(10)
            logStudyMinutes(2, completedLesson = false, learnedWord = false)
        }
        return id
    }

    suspend fun clearConversation(sessionId: String) {
        database.conversationDao().clearSession(sessionId)
    }

    // Speaking Coach Sessions
    val allSpeakingCoachSessions: Flow<List<SpeakingCoachSessionEntity>> =
        database.speakingCoachDao().getAllSessions()

    suspend fun saveSpeakingCoachSession(session: SpeakingCoachSessionEntity): Long {
        val id = database.speakingCoachDao().insertSession(session)
        database.userProfileDao().addXp(40)
        logStudyMinutes(5, completedLesson = false, learnedWord = false)
        return id
    }

    // Saved Useful Phrases & Idioms
    val allSavedPhrases: Flow<List<SavedPhraseEntity>> =
        database.savedPhraseDao().getAllSavedPhrases()

    suspend fun saveUsefulPhrase(
        phrase: String,
        meaning: String,
        contextSentence: String = "",
        category: String = "Conversation"
    ): Long {
        val entity = SavedPhraseEntity(
            phrase = phrase,
            meaning = meaning,
            contextSentence = contextSentence,
            category = category
        )
        return database.savedPhraseDao().insertPhrase(entity)
    }

    suspend fun deleteSavedPhrase(id: Long) {
        database.savedPhraseDao().deletePhrase(id)
    }

    suspend fun initializeIfEmpty() {
        val profile = database.userProfileDao().getUserProfileOnce()
        if (profile == null) {
            AppDatabase.populateDatabase(database)
        }
    }
}
