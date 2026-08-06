package com.youyangzhao.sourcesense.navigation

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.youyangzhao.sourcesense.data.local.database.SourceSenseDatabase
import com.youyangzhao.sourcesense.data.local.preferences.userSettingsDataStore
import com.youyangzhao.sourcesense.data.remote.api.CrossrefApiClient
import com.youyangzhao.sourcesense.data.repository.CrossrefAcademicSourceRepository
import com.youyangzhao.sourcesense.data.repository.DataStoreUserSettingsRepository
import com.youyangzhao.sourcesense.data.repository.LocalLearningModuleRepository
import com.youyangzhao.sourcesense.data.repository.RoomEvaluationHistoryRepository
import com.youyangzhao.sourcesense.data.repository.RoomSourceReviewRepository
import com.youyangzhao.sourcesense.data.repository.RoomStatisticsRepository
import com.youyangzhao.sourcesense.domain.model.AcademicSource
import com.youyangzhao.sourcesense.ui.evaluation.EvaluationRoute
import com.youyangzhao.sourcesense.ui.evaluation.EvaluationViewModel
import com.youyangzhao.sourcesense.ui.evaluation.EvaluationViewModelFactory
import com.youyangzhao.sourcesense.ui.explore.ExploreRoute
import com.youyangzhao.sourcesense.ui.explore.ExploreViewModel
import com.youyangzhao.sourcesense.ui.explore.ExploreViewModelFactory
import com.youyangzhao.sourcesense.ui.landing.LandingRoute
import com.youyangzhao.sourcesense.ui.landing.LandingViewModel
import com.youyangzhao.sourcesense.ui.landing.LandingViewModelFactory
import com.youyangzhao.sourcesense.ui.result.ResultRoute
import com.youyangzhao.sourcesense.ui.review.RealSourceReviewRoute
import com.youyangzhao.sourcesense.ui.review.RealSourceReviewViewModel
import com.youyangzhao.sourcesense.ui.review.RealSourceReviewViewModelFactory
import com.youyangzhao.sourcesense.ui.settings.SettingsRoute
import com.youyangzhao.sourcesense.ui.settings.SettingsViewModel
import com.youyangzhao.sourcesense.ui.settings.SettingsViewModelFactory
import com.youyangzhao.sourcesense.ui.statistics.StatisticsRoute
import com.youyangzhao.sourcesense.ui.statistics.StatisticsViewModel
import com.youyangzhao.sourcesense.ui.statistics.StatisticsViewModelFactory

@Composable
fun SourceSenseNavHost(
    modifier: Modifier = Modifier,
    reduceAnimations: Boolean = false,
    soundFeedbackEnabled: Boolean = true
) {
    val navController =
        rememberNavController()

    val backStackEntry by
    navController
        .currentBackStackEntryAsState()

    val currentRoute =
        backStackEntry
            ?.destination
            ?.route

    val context =
        LocalContext.current

    val applicationContext =
        context.applicationContext

    val topLevelRoutes = remember {
        AppDestination
            .topLevelDestinations
            .map { destination ->
                destination.route
            }
            .toSet()
    }

    // Create the local learning content repository
    val learningModuleRepository = remember {
        LocalLearningModuleRepository()
    }

    // Create the repository used for real literature searches
    val academicSourceRepository = remember {
        CrossrefAcademicSourceRepository(
            apiService =
                CrossrefApiClient.service
        )
    }

    // Create one Room database instance for all local records
    val database = remember(
        applicationContext
    ) {
        SourceSenseDatabase.getInstance(
            context = applicationContext
        )
    }

    // Store and observe learning-module attempts
    val evaluationHistoryRepository =
        remember(database) {
            RoomEvaluationHistoryRepository(
                evaluationAttemptDao =
                    database.evaluationAttemptDao()
            )
        }

    // Store and observe structured real-source reviews
    val sourceReviewRepository =
        remember(database) {
            RoomSourceReviewRepository(
                sourceReviewDao =
                    database.sourceReviewDao()
            )
        }

    // Combine module progress, evaluation history and source reviews
    val statisticsRepository =
        remember(
            database,
            learningModuleRepository
        ) {
            RoomStatisticsRepository(
                evaluationAttemptDao =
                    database.evaluationAttemptDao(),
                sourceReviewDao =
                    database.sourceReviewDao(),
                learningModuleRepository =
                    learningModuleRepository
            )
        }

    // Store user preferences with DataStore
    val userSettingsRepository =
        remember(applicationContext) {
            DataStoreUserSettingsRepository(
                dataStore =
                    applicationContext
                        .userSettingsDataStore
            )
        }

    val landingFactory = remember(
        learningModuleRepository,
        userSettingsRepository,
        evaluationHistoryRepository
    ) {
        LandingViewModelFactory(
            learningModuleRepository =
                learningModuleRepository,
            userSettingsRepository =
                userSettingsRepository,
            evaluationHistoryRepository =
                evaluationHistoryRepository
        )
    }

    val evaluationFactory = remember(
        learningModuleRepository,
        evaluationHistoryRepository
    ) {
        EvaluationViewModelFactory(
            learningModuleRepository =
                learningModuleRepository,
            evaluationHistoryRepository =
                evaluationHistoryRepository
        )
    }

    val exploreFactory = remember(
        academicSourceRepository,
        sourceReviewRepository
    ) {
        ExploreViewModelFactory(
            academicSourceRepository =
                academicSourceRepository,
            sourceReviewRepository =
                sourceReviewRepository
        )
    }

    val sourceReviewFactory = remember(
        sourceReviewRepository
    ) {
        RealSourceReviewViewModelFactory(
            sourceReviewRepository =
                sourceReviewRepository
        )
    }

    val statisticsFactory = remember(
        statisticsRepository
    ) {
        StatisticsViewModelFactory(
            statisticsRepository =
                statisticsRepository
        )
    }

    val settingsFactory = remember(
        userSettingsRepository
    ) {
        SettingsViewModelFactory(
            userSettingsRepository =
                userSettingsRepository
        )
    }

    val landingViewModel:
            LandingViewModel =
        viewModel(
            factory = landingFactory
        )

    val evaluationViewModel:
            EvaluationViewModel =
        viewModel(
            factory = evaluationFactory
        )

    val exploreViewModel:
            ExploreViewModel =
        viewModel(
            factory = exploreFactory
        )

    val sourceReviewViewModel:
            RealSourceReviewViewModel =
        viewModel(
            factory = sourceReviewFactory
        )

    val statisticsViewModel:
            StatisticsViewModel =
        viewModel(
            factory = statisticsFactory
        )

    val settingsViewModel:
            SettingsViewModel =
        viewModel(
            factory = settingsFactory
        )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute in topLevelRoutes) {
                NavigationBar {
                    AppDestination
                        .topLevelDestinations
                        .forEach { destination ->
                            NavigationBarItem(
                                selected =
                                    currentRoute ==
                                            destination.route,
                                onClick = {
                                    navController.navigate(
                                        destination.route
                                    ) {
                                        // Preserve each top-level screen state
                                        popUpTo(
                                            navController
                                                .graph
                                                .findStartDestination()
                                                .id
                                        ) {
                                            saveState = true
                                        }

                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector =
                                            destination.icon,
                                        contentDescription =
                                            destination.label
                                    )
                                },
                                label = {
                                    Text(
                                        text =
                                            destination.label
                                    )
                                }
                            )
                        }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination =
                AppDestination.Landing.route,
            modifier = Modifier.padding(
                innerPadding
            ),
            enterTransition = {
                if (reduceAnimations) {
                    EnterTransition.None
                } else {
                    fadeIn()
                }
            },
            exitTransition = {
                if (reduceAnimations) {
                    ExitTransition.None
                } else {
                    fadeOut()
                }
            },
            popEnterTransition = {
                if (reduceAnimations) {
                    EnterTransition.None
                } else {
                    fadeIn()
                }
            },
            popExitTransition = {
                if (reduceAnimations) {
                    ExitTransition.None
                } else {
                    fadeOut()
                }
            }
        ) {
            composable(
                route =
                    AppDestination.Landing.route
            ) {
                LandingRoute(
                    viewModel =
                        landingViewModel,
                    onStartModule = { moduleId ->
                        evaluationViewModel
                            .startModule(
                                moduleId = moduleId
                            )

                        navController.navigate(
                            AppDestination
                                .Evaluation
                                .route
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route =
                    AppDestination.Explore.route
            ) {
                ExploreRoute(
                    viewModel =
                        exploreViewModel,
                    onOpenSource = { source ->
                        openSourcePage(
                            context = context,
                            source = source
                        )
                    },
                    onEvaluateSource = {
                            source,
                            searchTopic ->

                        // Pass the selected real source into the review flow
                        sourceReviewViewModel
                            .startReview(
                                source = source,
                                searchTopic =
                                    searchTopic
                            )

                        navController.navigate(
                            AppDestination
                                .SourceReview
                                .route
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route =
                    AppDestination.SourceReview.route
            ) {
                RealSourceReviewRoute(
                    viewModel =
                        sourceReviewViewModel,
                    onBackToExplore = {
                        navController.popBackStack()
                    },
                    onOpenPaperPage = { source ->
                        openSourcePage(
                            context = context,
                            source = source
                        )
                    }
                )
            }

            composable(
                route =
                    AppDestination.Evaluation.route
            ) {
                EvaluationRoute(
                    viewModel =
                        evaluationViewModel,
                    reduceAnimations =
                        reduceAnimations,
                    soundFeedbackEnabled =
                        soundFeedbackEnabled,
                    onEvaluationComplete = {
                        navController.navigate(
                            AppDestination
                                .Result
                                .route
                        ) {
                            // Remove the completed evaluation screen
                            popUpTo(
                                AppDestination
                                    .Evaluation
                                    .route
                            ) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route =
                    AppDestination.Result.route
            ) {
                ResultRoute(
                    viewModel =
                        evaluationViewModel,
                    onTryAgain = {
                        evaluationViewModel
                            .restartEvaluation()

                        navController.navigate(
                            AppDestination
                                .Evaluation
                                .route
                        ) {
                            // Replace the result with a new attempt
                            popUpTo(
                                AppDestination
                                    .Result
                                    .route
                            ) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    },
                    onBackHome = {
                        navController.navigate(
                            AppDestination
                                .Landing
                                .route
                        ) {
                            // Return to the main destination
                            popUpTo(
                                navController
                                    .graph
                                    .findStartDestination()
                                    .id
                            )

                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route =
                    AppDestination.Statistics.route
            ) {
                StatisticsRoute(
                    viewModel =
                        statisticsViewModel,
                    onPracticeModule = { moduleId ->
                        // Start the recommended module from Statistics
                        evaluationViewModel.startModule(
                            moduleId = moduleId
                        )

                        navController.navigate(
                            AppDestination
                                .Evaluation
                                .route
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                route =
                    AppDestination.Settings.route
            ) {
                SettingsRoute(
                    viewModel =
                        settingsViewModel
                )
            }
        }
    }
}

private fun openSourcePage(
    context: Context,
    source: AcademicSource
) {
    val cleanDoi = source.doi
        .trim()
        .removePrefix(
            "https://doi.org/"
        )
        .removePrefix(
            "http://doi.org/"
        )
        .removePrefix("doi:")
        .trim()

    // Open the DOI landing page instead of a machine-readable file
    val doiPageUri =
        if (cleanDoi.isNotBlank()) {
            val encodedDoi = Uri.encode(
                cleanDoi,
                "/"
            )

            Uri.parse(
                "https://doi.org/$encodedDoi"
            )
        } else {
            null
        }

    // Use the publisher page only when no DOI is available
    val publisherPageUri = source.url
        ?.takeIf { url ->
            url.isSafeWebUrl()
        }
        ?.let(Uri::parse)

    val destinationUri =
        doiPageUri ?: publisherPageUri

    if (destinationUri == null) {
        Toast.makeText(
            context,
            "This source does not have a valid paper page.",
            Toast.LENGTH_LONG
        ).show()

        return
    }

    runCatching {
        CustomTabsIntent
            .Builder()
            .setShowTitle(true)
            .build()
            .launchUrl(
                context,
                destinationUri
            )
    }.onFailure {
        Toast.makeText(
            context,
            "Unable to open this paper page.",
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun String.isSafeWebUrl(): Boolean {
    return startsWith(
        prefix = "https://",
        ignoreCase = true
    ) || startsWith(
        prefix = "http://",
        ignoreCase = true
    )
}