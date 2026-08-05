package com.youyangzhao.sourcesense.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.youyangzhao.sourcesense.data.local.dao.EvaluationAttemptDao
import com.youyangzhao.sourcesense.data.local.dao.SourceReviewDao
import com.youyangzhao.sourcesense.data.local.entity.EvaluationAnswerEntity
import com.youyangzhao.sourcesense.data.local.entity.EvaluationAttemptEntity
import com.youyangzhao.sourcesense.data.local.entity.SourceReviewEntity

@Database(
    entities = [
        EvaluationAttemptEntity::class,
        EvaluationAnswerEntity::class,
        SourceReviewEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SourceSenseDatabase : RoomDatabase() {

    abstract fun evaluationAttemptDao():
            EvaluationAttemptDao

    abstract fun sourceReviewDao():
            SourceReviewDao

    companion object {

        @Volatile
        private var instance:
                SourceSenseDatabase? = null

        private val migration1To2 = object :
            Migration(1, 2) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {
                // Add source reviews without deleting learning history
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `source_reviews` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `doi` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `authors` TEXT NOT NULL,
                        `publicationYear` INTEGER,
                        `publicationName` TEXT,
                        `publisher` TEXT,
                        `sourceType` TEXT,
                        `searchTopic` TEXT NOT NULL,
                        `relevanceAssessment` TEXT NOT NULL,
                        `publicationInformationAssessment` TEXT NOT NULL,
                        `currencyAssessment` TEXT NOT NULL,
                        `reviewDepth` TEXT NOT NULL,
                        `citationDecision` TEXT NOT NULL,
                        `verificationItems` TEXT NOT NULL,
                        `reflectionNote` TEXT NOT NULL,
                        `reviewedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    `index_source_reviews_doi`
                    ON `source_reviews` (`doi`)
                    """.trimIndent()
                )
            }
        }

        fun getInstance(
            context: Context
        ): SourceSenseDatabase {
            return instance ?: synchronized(this) {
                // Create one database instance for the application
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SourceSenseDatabase::class.java,
                    "sourcesense_database"
                )
                    .addMigrations(
                        migration1To2
                    )
                    .build()
                    .also { database ->
                        instance = database
                    }
            }
        }
    }
}
