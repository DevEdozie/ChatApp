package com.edozie.chatapp.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.edozie.chatapp.ui.screen.HomeScreen
import com.edozie.chatapp.ui.screen.LoginScreen
import com.edozie.chatapp.ui.screen.SignupScreen
import com.edozie.chatapp.ui.screen.SplashScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("home") { HomeScreen(navController) }
    }

}