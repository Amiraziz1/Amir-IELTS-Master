package com.example.ieltsmaster.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ieltsmaster.ui.screens.home.HomeScreen
import com.example.ieltsmaster.ui.screens.ielts.IeltsScreen
import com.example.ieltsmaster.ui.screens.ielts.MockTestScreen
import com.example.ieltsmaster.ui.screens.learn.LearnScreen
import com.example.ieltsmaster.ui.screens.learn.LessonDetailScreen
import com.example.ieltsmaster.ui.screens.onboarding.OnboardingScreen
import com.example.ieltsmaster.ui.screens.practice.AiConversationScreen
import com.example.ieltsmaster.ui.screens.practice.PracticeScreen
import com.example.ieltsmaster.ui.screens.practice.SpeakingCoachScreen
import com.example.ieltsmaster.ui.screens.profile.ProfileScreen
import com.example.ieltsmaster.ui.screens.progress.ProgressScreen
import com.example.ieltsmaster.ui.viewmodel.IeltsMasterViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Learn : Screen("learn", "Learn", Icons.Default.School)
    object Practice : Screen("practice?tab={tab}", "Practice", Icons.Default.FitnessCenter)
    object Ielts : Screen("ielts", "IELTS", Icons.Default.Assignment)
    object Progress : Screen("progress", "Progress", Icons.Default.BarChart)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    // Sub-screens
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Default.Star)
    object LessonDetail : Screen("lesson/{lessonId}", "Lesson Detail", Icons.Default.PlayArrow)
    object MockTest : Screen("mock_test/{testId}", "Mock Test", Icons.Default.Quiz)
}

@Composable
fun AppNavigation(viewModel: IeltsMasterViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Learn,
        Screen.Practice,
        Screen.Ielts,
        Screen.Progress,
        Screen.Profile
    )

    val showBottomBar = currentRoute in listOf(
        "home",
        "learn",
        "practice?tab={tab}",
        "ielts",
        "progress",
        "profile"
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val isSelected = when (screen) {
                            Screen.Practice -> currentRoute?.startsWith("practice") == true
                            else -> currentRoute == screen.route
                        }
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            onClick = {
                                val target = if (screen == Screen.Practice) "practice?tab=VOCABULARY" else screen.route
                                navController.navigate(target) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier.testTag("nav_item_${screen.title.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                // If user is not onboarded, redirect to onboarding
                if (userProfile != null && !userProfile!!.isOnboarded) {
                    LaunchedEffect(Unit) {
                        navController.navigate("onboarding") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }

                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToLearn = {
                        navController.navigate("learn")
                    },
                    onNavigateToPractice = { skill ->
                        navController.navigate("practice?tab=$skill")
                    },
                    onNavigateToIelts = {
                        navController.navigate("ielts")
                    },
                    onOpenLesson = { lessonId ->
                        navController.navigate("lesson/$lessonId")
                    },
                    onNavigateToConversation = {
                        navController.navigate("conversation")
                    },
                    onNavigateToSpeakingCoach = {
                        navController.navigate("speaking_coach")
                    }
                )
            }

            composable("onboarding") {
                OnboardingScreen(
                    viewModel = viewModel,
                    onComplete = {
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            composable("learn") {
                LearnScreen(
                    viewModel = viewModel,
                    onOpenLesson = { lessonId ->
                        navController.navigate("lesson/$lessonId")
                    }
                )
            }

            composable(
                route = "practice?tab={tab}",
                arguments = listOf(navArgument("tab") {
                    type = NavType.StringType
                    defaultValue = "VOCABULARY"
                })
            ) { backStackEntry ->
                val tab = backStackEntry.arguments?.getString("tab") ?: "VOCABULARY"
                PracticeScreen(viewModel = viewModel, initialTab = tab)
            }

            composable("ielts") {
                IeltsScreen(
                    viewModel = viewModel,
                    onStartMockTest = { testId ->
                        navController.navigate("mock_test/$testId")
                    }
                )
            }

            composable("progress") {
                ProgressScreen(viewModel = viewModel)
            }

            composable("profile") {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToOnboarding = {
                        navController.navigate("onboarding")
                    }
                )
            }

            composable(
                route = "lesson/{lessonId}",
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
                LessonDetailScreen(
                    lessonId = lessonId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "mock_test/{testId}",
                arguments = listOf(navArgument("testId") { type = NavType.StringType })
            ) { backStackEntry ->
                val testId = backStackEntry.arguments?.getString("testId") ?: ""
                MockTestScreen(
                    testId = testId,
                    viewModel = viewModel,
                    onFinish = { navController.popBackStack() }
                )
            }

            composable("conversation") {
                AiConversationScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("speaking_coach") {
                SpeakingCoachScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
