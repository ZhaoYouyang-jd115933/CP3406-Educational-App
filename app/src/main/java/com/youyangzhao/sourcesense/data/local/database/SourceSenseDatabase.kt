package com.youyangzhao.sourcesense.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.youyangzhao.sourcesense.data.local.dao.EvaluationAttemptDao
import com.youyangzhao.sourcesense.data.local.entity.EvaluationAnswerEntity
import com.youyangzhao.sourcesense.data.local.entity.EvaluationAttemptEntity

@Database(
    entities = [
        EvaluationAttemptEntity::class,
        EvaluationAnswerEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SourceSenseDatabase : RoomDatabase() {

    abstract fun evaluationAttemptDao(): EvaluationAttemptDao

    companion object {

        @Volatile
        private var instance: SourceSenseDatabase? = null

        fun getInstance(
            context: Context
        ): SourceSenseDatabase {
            return instance ?: synchronized(this) {
                // Create one database instance for the whole application
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SourceSenseDatabase::class.java,
                    "sourcesense_database"
                ).build().also { database ->
                    instance = database
                }
            }
        }
    }
}
