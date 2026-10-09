package com.example.shohojbiye.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shohojbiye.ui.Screen
import com.example.shohojbiye.ui.auth.LoginScreen
import com.example.shohojbiye.ui.auth.SignupScreen
import com.example.shohojbiye.ui.calculator.CalculatorScreen
import com.example.shohojbiye.ui.dashboard.DashboardScreen
import com.example.shohojbiye.ui.registration.CertificateScreen
import com.example.shohojbiye.ui.registration.RegistrationFormScreen
import com.example.shohojbiye.util.SessionManager

@Composable
fun ShohojBiyeNavGraph(
    navController: NavHostController,
    startDestination: String,
    sessionManager: SessionManager,
    modifier: Modifier = Modifier,
) {
    var userRole by remember { 
        mutableStateOf(sessionManager.getUserRole() ?: "GENERAL_USER") 
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    userRole = role
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignup = { navController.navigate(Screen.Signup.route) }
            )
        }
        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                role = userRole,
                onNavigateToCalculator = { navController.navigate(Screen.Calculator.route) },
                onNavigateToRegistration = { navController.navigate(Screen.Registration.route) },
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Calculator.route) {
            CalculatorScreen()
        }
        composable(Screen.Registration.route) {
            RegistrationFormScreen(
                onRegistrationSuccess = { regId ->
                    navController.navigate(Screen.Certificate.createRoute(regId)) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Certificate.route) { backStackEntry ->
            val regId = backStackEntry.arguments?.getString("regId") ?: ""
            CertificateScreen(
                regId = regId,
                onBackToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
