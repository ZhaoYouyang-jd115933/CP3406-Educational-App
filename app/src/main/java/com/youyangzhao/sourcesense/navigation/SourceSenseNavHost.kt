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
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.youyangzhao.sourcesense.ui.evaluation.EvaluationScreen
import com.youyangzhao.sourcesense.ui.landing.LandingScreen
import com.youyangzhao.sourcesense.ui.settings.SettingsScreen
import com.youyangzhao.sourcesense.ui.statistics.StatisticsScreen

@Composable
fun SourceSenseNavHost(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                AppDestination.topLevelDestinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                // Keep one copy of each main screen
                                popUpTo(navController.graph.findStartDestination().id) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Landing.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestination.Landing.route) {
                LandingScreen(
                    onStartEvaluation = {
                        navController.navigate(AppDestination.Evaluation.route)
                    }
                )
            }

            composable(AppDestination.Evaluation.route) {
                EvaluationScreen()
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

