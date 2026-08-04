package com.youyangzhao.sourcesense.navigation

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
import com.youyangzhao.sourcesense.data.repository.LocalEvidenceRepository
import com.youyangzhao.sourcesense.data.repository.RoomEvaluationHistoryRepository
import com.youyangzhao.sourcesense.ui.evaluation.EvaluationRoute
import com.youyangzhao.sourcesense.ui.evaluation.EvaluationViewModel
import com.youyangzhao.sourcesense.ui.evaluation.EvaluationViewModelFactory
import com.youyangzhao.sourcesense.ui.landing.LandingScreen
import com.youyangzhao.sourcesense.ui.result.ResultRoute
import com.youyangzhao.sourcesense.ui.settings.SettingsScreen
import com.youyangzhao.sourcesense.ui.statistics.StatisticsScreen

@Composable
fun SourceSenseNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val context = LocalContext.current.applicationContext

    val topLevelRoutes = remember {
        AppDestination.topLevelDestinations
            .map { destination ->
                destination.route
            }
            .toSet()
    }

    val evidenceRepository = remember {
        LocalEvidenceRepository()
    }

    val database = remember(context) {
        SourceSenseDatabase.getInstance(context)
    }

    val evaluationHistoryRepository = remember(database) {
        RoomEvaluationHistoryRepository(
            evaluationAttemptDao = database.evaluationAttemptDao()
        )
    }

    val evaluationFactory = remember(
        evidenceRepository,
        evaluationHistoryRepository
    ) {
        EvaluationViewModelFactory(
            evidenceRepository = evidenceRepository,
            evaluationHistoryRepository =
                evaluationHistoryRepository
        )
    }

    val evaluationViewModel: EvaluationViewModel = viewModel(
        factory = evaluationFactory
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute in topLevelRoutes) {
                NavigationBar {
                    AppDestination.topLevelDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    // Preserve state across main navigation screens
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
                                    imageVector = destination.icon,
                                    contentDescription = destination.label
                                )
                            },
                            label = {
                                Text(text = destination.label)
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Landing.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestination.Landing.route) {
                LandingScreen(
                    onStartEvaluation = {
                        evaluationViewModel.restartEvaluation()

                        navController.navigate(
                            AppDestination.Evaluation.route
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(AppDestination.Evaluation.route) {
                EvaluationRoute(
                    viewModel = evaluationViewModel,
                    onEvaluationComplete = {
                        navController.navigate(
                            AppDestination.Result.route
                        ) {
                            // Remove the completed activity from the back stack
                            popUpTo(AppDestination.Evaluation.route) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(AppDestination.Result.route) {
                ResultRoute(
                    viewModel = evaluationViewModel,
                    onTryAgain = {
                        evaluationViewModel.restartEvaluation()

                        navController.navigate(
                            AppDestination.Evaluation.route
                        ) {
                            popUpTo(AppDestination.Result.route) {
                                inclusive = true
                            }

                            launchSingleTop = true
                        }
                    },
                    onBackHome = {
                        evaluationViewModel.restartEvaluation()

                        navController.navigate(
                            AppDestination.Landing.route
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

            composable(AppDestination.Statistics.route) {
                StatisticsScreen()
            }

            composable(AppDestination.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

