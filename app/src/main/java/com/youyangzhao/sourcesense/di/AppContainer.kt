package com.youyangzhao.sourcesense.di

import android.content.Context
import com.youyangzhao.sourcesense.data.local.database.SourceSenseDatabase
import com.youyangzhao.sourcesense.data.local.preferences.userSettingsDataStore
import com.youyangzhao.sourcesense.data.remote.api.CrossrefApiClient
import com.youyangzhao.sourcesense.data.repository.CrossrefAcademicSourceRepository
import com.youyangzhao.sourcesense.data.repository.DataStoreUserSettingsRepository
import com.youyangzhao.sourcesense.data.repository.LocalLearningModuleRepository
import com.youyangzhao.sourcesense.data.repository.RoomEvaluationHistoryRepository
import com.youyangzhao.sourcesense.data.repository.RoomSourceReviewRepository
import com.youyangzhao.sourcesense.data.repository.RoomStatisticsRepository
import com.youyangzhao.sourcesense.domain.repository.AcademicSourceRepository
import com.youyangzhao.sourcesense.domain.repository.EvaluationHistoryRepository
import com.youyangzhao.sourcesense.domain.repository.LearningModuleRepository
import com.youyangzhao.sourcesense.domain.repository.SourceReviewRepository
import com.youyangzhao.sourcesense.domain.repository.StatisticsRepository
import com.youyangzhao.sourcesense.domain.repository.UserSettingsRepository

class AppContainer(
    context: Context
) {

    private val applicationContext =
        context.applicationContext

    private val database =
        SourceSenseDatabase.getInstance(
            context = applicationContext
        )

    val learningModuleRepository:
            LearningModuleRepository =
        LocalLearningModuleRepository()

    val academicSourceRepository:
            AcademicSourceRepository =
        CrossrefAcademicSourceRepository(
            apiService = CrossrefApiClient.service
        )

    val evaluationHistoryRepository:
            EvaluationHistoryRepository =
        RoomEvaluationHistoryRepository(
            evaluationAttemptDao =
                database.evaluationAttemptDao()
        )

    val sourceReviewRepository:
            SourceReviewRepository =
        RoomSourceReviewRepository(
            sourceReviewDao =
                database.sourceReviewDao()
        )

    val statisticsRepository:
            StatisticsRepository =
        RoomStatisticsRepository(
            evaluationAttemptDao =
                database.evaluationAttemptDao(),
            sourceReviewDao =
                database.sourceReviewDao(),
            learningModuleRepository =
                learningModuleRepository
        )

    val userSettingsRepository:
            UserSettingsRepository =
        DataStoreUserSettingsRepository(
            dataStore =
                applicationContext.userSettingsDataStore
        )
}

