package com.youyangzhao.sourcesense.navigation

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
import com.youyangzhao.sourcesense.data.repository.RoomStatisticsRepository
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
        AppDestination.topLevelDestinations
            .map { destination ->
                destination.route
            }
            .toSet()
    }

    val learningModuleRepository = remember {
        LocalLearningModuleRepository()
    }

    val academicSourceRepository = remember {
        CrossrefAcademicSourceRepository(
            apiService =
                CrossrefApiClient.service
        )
    }

    val database = remember(
        applicationContext
    ) {
        SourceSenseDatabase.getInstance(
            applicationContext
        )
    }

    val evaluationHistoryRepository =
        remember(database) {
            RoomEvaluationHistoryRepository(
                evaluationAttemptDao =
                    database.evaluationAttemptDao()
            )
        }

    val statisticsRepository =
        remember(database) {
            RoomStatisticsRepository(
                evaluationAttemptDao =
                    database.evaluationAttemptDao()
            )
        }

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
        userSettingsRepository
    ) {
        LandingViewModelFactory(
            learningModuleRepository =
                learningModuleRepository,
            userSettingsRepository =
                userSettingsRepository
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
        academicSourceRepository
    ) {
        ExploreViewModelFactory(
            academicSourceRepository =
                academicSourceRepository
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
                                        // Preserve screen state
                                        popUpTo(
                                            navController.graph
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
                AppDestination.Explore.route
            ) {
                ExploreRoute(
                    viewModel =
                        exploreViewModel,
                    onSourceSelected = { source ->
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

                        if (cleanDoi.isBlank()) {
                            Toast.makeText(
                                context,
                                "This source does not have a valid DOI.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val encodedDoi =
                                Uri.encode(
                                    cleanDoi,
                                    "/"
                                )

                            val paperPageUri =
                                Uri.parse(
                                    "https://doi.org/$encodedDoi"
                                )

                            runCatching {
                                CustomTabsIntent
                                    .Builder()
                                    .setShowTitle(true)
                                    .build()
                                    .launchUrl(
                                        context,
                                        paperPageUri
                                    )
                            }.onFailure {
                                Toast.makeText(
                                    context,
                                    "Unable to open this paper page.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                )
            }

            composable(
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
                            popUpTo(
                                navController.graph
                                    .findStartDestination()
                                    .id
                            )

                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(
                AppDestination.Statistics.route
            ) {
                StatisticsRoute(
                    viewModel =
                        statisticsViewModel
                )
            }

            composable(
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