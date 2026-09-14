package com.example.ieltsmaster.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Learner",
    val currentLevel: String = "Intermediate", // Beginner, Elementary, Intermediate, Upper Intermediate, Advanced
    val targetBand: Float = 7.5f,
    val examType: String = "Academic", // Academic, General Training
    val targetExamDate: String = "",
    val dailyStudyTimeMinutes: Int = 30,
    val mainWeakness: String = "Speaking",
    val isOnboarded: Boolean = false,
    val streakDays: Int = 1,
    val totalXp: Int = 150,
    val lastActiveDate: String = ""
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val level: String, // Beginner, Elementary, Intermediate, Upper Intermediate, Advanced, IELTS
    val category: String, // FOUNDATION, SPOKEN, GRAMMAR, VOCABULARY, LISTENING, READING, WRITING, SPEAKING, PRONUNCIATION
    val estimatedMinutes: Int = 15,
    val orderIndex: Int = 0,
    val contentMarkdown: String = "",
    val isCompleted: Boolean = false,
    val score: Int = 0
)

@Entity(tableName = "vocabulary_words")
data class VocabularyWordEntity(
    @PrimaryKey val word: String,
    val phonetic: String,
    val partOfSpeech: String,
    val meaning: String,
    val exampleSentence: String,
    val difficulty: String, // Beginner, Intermediate, Advanced, Academic, IELTS
    val topic: String, // Education, Technology, Environment, Health, Business, Work, Society, Crime, Tourism, etc.
    val synonyms: String = "",
    val antonyms: String = "",
    val collocations: String = "",
    val ieltsRelevance: String = "",
    val masteryStatus: String = "NEW", // NEW, LEARNING, KNOWN, REVIEW
    val nextReviewTimestamp: Long = 0L,
    val reviewCount: Int = 0
)

@Entity(tableName = "grammar_topics")
data class GrammarTopicEntity(
    @PrimaryKey val topicId: String,
    val category: String, // Tenses, Modals, Conditionals, Passive Voice, Relative Clauses, Reported Speech, etc.
    val title: String,
    val level: String,
    val explanation: String,
    val formulaOrStructure: String,
    val examplesJson: String,
    val commonMistakesJson: String,
    val isMastered: Boolean = false
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val questionId: String,
    val moduleOrSkill: String, // LISTENING, READING, GRAMMAR, VOCABULARY, MOCK_TEST
    val topicOrLessonId: String,
    val questionType: String, // MULTIPLE_CHOICE, TRUE_FALSE_NOT_GIVEN, FILL_BLANK, MATCHING
    val questionText: String,
    val passageOrScript: String = "",
    val optionsJson: String = "", // JSON array of options
    val correctAnswer: String,
    val explanation: String,
    val targetBand: Float = 7.0f
)

@Entity(tableName = "speaking_tasks")
data class SpeakingTaskEntity(
    @PrimaryKey val taskId: String,
    val part: Int, // 1, 2, 3
    val topic: String,
    val prompt: String,
    val bulletPointsJson: String = "",
    val prepTimeSeconds: Int = 60,
    val speakTimeSeconds: Int = 120,
    val modelAnswer: String = "",
    val vocabularyTips: String = "",
    val bandCriteriaTips: String = ""
)

@Entity(tableName = "writing_tasks")
data class WritingTaskEntity(
    @PrimaryKey val taskId: String,
    val taskType: String, // ACADEMIC_TASK_1, ACADEMIC_TASK_2, GENERAL_TASK_1
    val prompt: String,
    val scenarioOrDataDescription: String,
    val sampleBand9Answer: String,
    val keyVocabulary: String,
    val structureChecklist: String,
    val userSavedDraft: String = ""
)

@Entity(tableName = "mock_tests")
data class MockTestEntity(
    @PrimaryKey val testId: String,
    val title: String,
    val testType: String, // Academic, General Training, Full Simulation, Skill Focus
    val durationMinutes: Int = 60,
    val totalQuestions: Int = 40,
    val description: String,
    val targetBandLevel: String = "Band 7.0 - 8.5"
)

@Entity(tableName = "mock_test_attempts")
data class MockTestAttemptEntity(
    @PrimaryKey(autoGenerate = true) val attemptId: Long = 0,
    val testId: String,
    val testTitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val overallBand: Float,
    val listeningBand: Float = 0f,
    val readingBand: Float = 0f,
    val writingBand: Float = 0f,
    val speakingBand: Float = 0f,
    val scorePercentage: Int,
    val feedback: String
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val code: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1,
    val xpReward: Int = 50
)

@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val minutesStudied: Int = 0,
    val lessonsCompleted: Int = 0,
    val wordsLearned: Int = 0,
    val xpEarned: Int = 0
)

@Entity(tableName = "ai_conversation_messages")
data class AiConversationMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String = "default",
    val mode: String = "DAILY", // BEGINNER, DAILY, JOB_INTERVIEW, TRAVEL, UNIVERSITY, GENERAL, IELTS_PART_1, IELTS_PART_2, IELTS_PART_3
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val feedbackVocabulary: String = "",
    val feedbackGrammar: String = "",
    val feedbackFluency: String = "",
    val betterVersion: String = ""
)

@Entity(tableName = "speaking_coach_sessions")
data class SpeakingCoachSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: String,
    val taskTitle: String,
    val part: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 120,
    val audioPath: String = "",
    val userTranscript: String = "",
    val fluencyScore: Float = 7.0f,
    val vocabScore: Float = 7.0f,
    val grammarScore: Float = 7.0f,
    val pronunciationScore: Float = 7.0f,
    val overallScore: Float = 7.0f,
    val feedbackNotes: String = ""
)

@Entity(tableName = "saved_phrases")
data class SavedPhraseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phrase: String,
    val meaning: String,
    val contextSentence: String = "",
    val category: String = "Conversation",
    val timestamp: Long = System.currentTimeMillis()
)


