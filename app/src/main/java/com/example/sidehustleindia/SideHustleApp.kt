package com.example.sidehustleindia

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sidehustleindia.ui.MainScreen
import com.example.sidehustleindia.ui.dashboard.HustleDetailScreen
import com.example.sidehustleindia.ui.auth.LoginScreen
import com.example.sidehustleindia.ui.onboarding.CityInputScreen
import com.example.sidehustleindia.ui.onboarding.SkillsInputScreen
import com.example.sidehustleindia.ui.onboarding.TimeGoalsScreen
import com.example.sidehustleindia.ui.onboarding.WelcomeScreen
import com.example.sidehustleindia.viewmodel.ProfileViewModel
import com.example.sidehustleindia.ui.subscription.SubscriptionScreen

@Composable
fun SideHustleApp() {
    val navController = rememberNavController()
    val viewModel: ProfileViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("main") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("welcome") {
            WelcomeScreen(onStartClick = { navController.navigate("skills") })
        }
        composable("skills") {
            SkillsInputScreen(onNext = { tech, soft, specific ->
                viewModel.updateTechnicalSkills(tech)
                viewModel.updateSoftSkills(soft)
                viewModel.updateSpecificSkills(specific)
                navController.navigate("city")
            })
        }
        composable("city") {
            CityInputScreen(onNext = { city ->
                viewModel.updateCity(city)
                navController.navigate("time_goals")
            })
        }
        composable("time_goals") {
            TimeGoalsScreen(onFinish = { time, goal ->
                viewModel.updateTime(time)
                viewModel.updateGoals(goal)
                navController.navigate("main")
            })
        }
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onHustleClick = { hustleId ->
                    navController.navigate("hustle_detail/$hustleId")
                }
            )
        }
        
        composable("hustle_detail/{hustleId}") { backStackEntry ->
            val hustleId = backStackEntry.arguments?.getString("hustleId") ?: return@composable
            HustleDetailScreen(
                hustleId = hustleId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable("subscription") {
            SubscriptionScreen(
                currentlyPro = viewModel.isPro.collectAsState().value,
                onUpgradeClick = {
                    viewModel.toggleProStatus()
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
