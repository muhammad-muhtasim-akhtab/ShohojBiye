package com.example.shohojbiye

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.shohojbiye.ui.Screen
import com.example.shohojbiye.ui.navigation.ShohojBiyeNavGraph
import com.example.shohojbiye.ui.theme.ShohojBiyeTheme
import com.example.shohojbiye.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val startDestination = if (sessionManager.getAuthToken() != null) {
            Screen.Dashboard.route
        } else {
            Screen.Login.route
        }

        setContent {
            ShohojBiyeTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShohojBiyeNavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        sessionManager = sessionManager,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
