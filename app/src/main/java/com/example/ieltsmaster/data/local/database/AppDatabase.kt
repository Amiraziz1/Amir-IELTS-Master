package com.example.ieltsmaster.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ieltsmaster.data.local.dao.*
import com.example.ieltsmaster.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        LessonEntity::class,
        VocabularyWordEntity::class,
        GrammarTopicEntity::class,
        QuestionEntity::class,
        SpeakingTaskEntity::class,
        WritingTaskEntity::class,
        MockTestEntity::class,
        MockTestAttemptEntity::class,
        AchievementEntity::class,
        DailyActivityEntity::class,
        AiConversationMessageEntity::class,
        SpeakingCoachSessionEntity::class,
        SavedPhraseEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun lessonDao(): LessonDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun grammarDao(): GrammarDao
    abstract fun questionDao(): QuestionDao
    abstract fun speakingTaskDao(): SpeakingTaskDao
    abstract fun writingTaskDao(): WritingTaskDao
    abstract fun mockTestDao(): MockTestDao
    abstract fun achievementDao(): AchievementDao
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun conversationDao(): ConversationDao
    abstract fun speakingCoachDao(): SpeakingCoachDao
    abstract fun savedPhraseDao(): SavedPhraseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ielts_master_database.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            database.userProfileDao().insertOrUpdateProfile(DataPreloader.getInitialProfile())
            database.lessonDao().insertLessons(DataPreloader.getInitialLessons())
            database.vocabularyDao().insertWords(DataPreloader.getInitialVocabulary())
            database.grammarDao().insertTopics(DataPreloader.getInitialGrammarTopics())
            database.questionDao().insertQuestions(DataPreloader.getInitialQuestions())
            database.speakingTaskDao().insertSpeakingTasks(DataPreloader.getInitialSpeakingTasks())
            database.writingTaskDao().insertWritingTasks(DataPreloader.getInitialWritingTasks())
            database.mockTestDao().insertMockTests(DataPreloader.getInitialMockTests())
            database.achievementDao().insertAchievements(DataPreloader.getInitialAchievements())
            for (act in DataPreloader.getInitialDailyActivities()) {
                database.dailyActivityDao().recordActivity(act)
            }
        }
    }
}
