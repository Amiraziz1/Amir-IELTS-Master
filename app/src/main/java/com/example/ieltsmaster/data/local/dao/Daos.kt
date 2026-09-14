package com.example.ieltsmaster.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ieltsmaster.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET totalXp = totalXp + :xp WHERE id = 1")
    suspend fun addXp(xp: Int)

    @Query("UPDATE user_profile SET streakDays = :streak WHERE id = 1")
    suspend fun updateStreak(streak: Int)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY orderIndex ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE category = :category ORDER BY orderIndex ASC")
    fun getLessonsByCategory(category: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE level = :level ORDER BY orderIndex ASC")
    fun getLessonsByLevel(level: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getLessonById(id: String): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("UPDATE lessons SET isCompleted = :completed, score = :score WHERE id = :id")
    suspend fun updateLessonProgress(id: String, completed: Boolean, score: Int)
}

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary_words ORDER BY word ASC")
    fun getAllWords(): Flow<List<VocabularyWordEntity>>

    @Query("SELECT * FROM vocabulary_words WHERE topic = :topic ORDER BY word ASC")
    fun getWordsByTopic(topic: String): Flow<List<VocabularyWordEntity>>

    @Query("SELECT * FROM vocabulary_words WHERE difficulty = :difficulty ORDER BY word ASC")
    fun getWordsByDifficulty(difficulty: String): Flow<List<VocabularyWordEntity>>

    @Query("SELECT * FROM vocabulary_words WHERE masteryStatus = :status ORDER BY word ASC")
    fun getWordsByStatus(status: String): Flow<List<VocabularyWordEntity>>

    @Query("SELECT COUNT(*) FROM vocabulary_words WHERE masteryStatus = :status")
    fun countWordsByStatus(status: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWords(words: List<VocabularyWordEntity>)

    @Query("UPDATE vocabulary_words SET masteryStatus = :status, reviewCount = reviewCount + 1, nextReviewTimestamp = :nextReview WHERE word = :word")
    suspend fun updateWordMastery(word: String, status: String, nextReview: Long)
}

@Dao
interface GrammarDao {
    @Query("SELECT * FROM grammar_topics ORDER BY topicId ASC")
    fun getAllTopics(): Flow<List<GrammarTopicEntity>>

    @Query("SELECT * FROM grammar_topics WHERE category = :category")
    fun getTopicsByCategory(category: String): Flow<List<GrammarTopicEntity>>

    @Query("SELECT * FROM grammar_topics WHERE topicId = :id")
    suspend fun getTopicById(id: String): GrammarTopicEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTopics(topics: List<GrammarTopicEntity>)

    @Query("UPDATE grammar_topics SET isMastered = :mastered WHERE topicId = :topicId")
    suspend fun updateTopicMastery(topicId: String, mastered: Boolean)
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE moduleOrSkill = :module")
    fun getQuestionsByModule(module: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE topicOrLessonId = :topicOrLessonId")
    suspend fun getQuestionsForTopic(topicOrLessonId: String): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)
}

@Dao
interface SpeakingTaskDao {
    @Query("SELECT * FROM speaking_tasks ORDER BY part ASC, taskId ASC")
    fun getAllSpeakingTasks(): Flow<List<SpeakingTaskEntity>>

    @Query("SELECT * FROM speaking_tasks WHERE part = :part")
    fun getSpeakingTasksByPart(part: Int): Flow<List<SpeakingTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSpeakingTasks(tasks: List<SpeakingTaskEntity>)
}

@Dao
interface WritingTaskDao {
    @Query("SELECT * FROM writing_tasks ORDER BY taskId ASC")
    fun getAllWritingTasks(): Flow<List<WritingTaskEntity>>

    @Query("SELECT * FROM writing_tasks WHERE taskType = :taskType")
    fun getWritingTasksByType(taskType: String): Flow<List<WritingTaskEntity>>

    @Query("UPDATE writing_tasks SET userSavedDraft = :draft WHERE taskId = :taskId")
    suspend fun saveDraft(taskId: String, draft: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWritingTasks(tasks: List<WritingTaskEntity>)
}

@Dao
interface MockTestDao {
    @Query("SELECT * FROM mock_tests")
    fun getAllMockTests(): Flow<List<MockTestEntity>>

    @Query("SELECT * FROM mock_tests WHERE testId = :testId")
    suspend fun getMockTestById(testId: String): MockTestEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMockTests(tests: List<MockTestEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: MockTestAttemptEntity)

    @Query("SELECT * FROM mock_test_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<MockTestAttemptEntity>>
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, code ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Query("UPDATE achievements SET isUnlocked = 1, progress = maxProgress WHERE code = :code")
    suspend fun unlockAchievement(code: String)
}

@Dao
interface DailyActivityDao {
    @Query("SELECT * FROM daily_activity ORDER BY date DESC LIMIT 7")
    fun getRecentActivities(): Flow<List<DailyActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordActivity(activity: DailyActivityEntity)

    @Query("SELECT * FROM daily_activity WHERE date = :date")
    suspend fun getActivityForDate(date: String): DailyActivityEntity?
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM ai_conversation_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<AiConversationMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AiConversationMessageEntity): Long

    @Query("DELETE FROM ai_conversation_messages WHERE sessionId = :sessionId")
    suspend fun clearSession(sessionId: String)
}

@Dao
interface SpeakingCoachDao {
    @Query("SELECT * FROM speaking_coach_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<SpeakingCoachSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SpeakingCoachSessionEntity): Long

    @Query("DELETE FROM speaking_coach_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)
}

@Dao
interface SavedPhraseDao {
    @Query("SELECT * FROM saved_phrases ORDER BY timestamp DESC")
    fun getAllSavedPhrases(): Flow<List<SavedPhraseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhrase(phrase: SavedPhraseEntity): Long

    @Query("DELETE FROM saved_phrases WHERE id = :id")
    suspend fun deletePhrase(id: Long)
}


