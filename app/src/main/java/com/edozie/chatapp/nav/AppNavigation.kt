package com.edozie.chatapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.edozie.chatapp.ui.screen.HomeScreen
import com.edozie.chatapp.ui.screen.LoginScreen
import com.edozie.chatapp.ui.screen.SignupScreen
import com.edozie.chatapp.ui.screen.SplashScreen
import com.edozie.chatapp.util.NetworkObserver


@Composable
fun AppNavigation(
    networkObserver: NetworkObserver
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController, networkObserver = networkObserver) }
        composable("signup") { SignupScreen(navController, networkObserver = networkObserver) }
        composable("home") { HomeScreen(networkObserver) }
    }

}