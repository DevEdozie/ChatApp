package com.edozie.chatapp.ui.screen

import android.content.Context
import android.window.SplashScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.edozie.chatapp.R
import com.edozie.chatapp.util.CustomBottomNavBar
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    // Get context for accessing SharedPreferences
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(2000L) // Wait for 3 seconds

        try {
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val isLoggedIn = prefs.getBoolean("is_logged_in", false)


            if (isLoggedIn) {
                navController.navigate(CustomBottomNavBar.Chats.route) {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        } catch (e: Exception) {
            // Log the error to help diagnose the crash
            e.printStackTrace()
        }
    }


    // UI: Display the app icon centered on the screen
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.chat_app_logo_ic),
            contentDescription = "App Logo",
            modifier = Modifier.size(128.dp),
            colorFilter = ColorFilter.tint(Color(0xFF007AFF))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    val navController = rememberNavController()
    SplashScreen(navController = navController)
}