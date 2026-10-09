package com.example.shohojbiye.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Dashboard : Screen("dashboard")
    object Calculator : Screen("calculator")
    object Registration : Screen("registration")
    object Certificate : Screen("certificate/{regId}") {
        fun createRoute(regId: String) = "certificate/$regId"
    }
}
