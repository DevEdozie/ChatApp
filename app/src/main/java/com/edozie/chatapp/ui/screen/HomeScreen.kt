package com.edozie.chatapp.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.edozie.chatapp.R
import com.edozie.chatapp.ui.widget.CustomBottomNavigationBar
import com.edozie.chatapp.util.CustomBottomNavBar
import com.edozie.chatapp.util.NetworkObserver


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(networkObserver: NetworkObserver) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val userCurrentRoute = currentBackStackEntry?.destination?.route

    val showBottomBar = when {
        userCurrentRoute == CustomBottomNavBar.Chats.route -> true
        userCurrentRoute == CustomBottomNavBar.Profile.route -> true
        else -> false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.chat_app_logo_ic),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(40.dp)
                    )
                }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                CustomBottomNavigationBar(
                    currentDestination = userCurrentRoute ?: "splash",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Avoid multiple copies of the same destination
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },


        ) { paddingValues ->

        NavHost(
            navController = navController,
//            startDestination = "splash",
            startDestination = CustomBottomNavBar.Chats.route,
            modifier = Modifier
                .padding(paddingValues)
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("login") { LoginScreen(navController, networkObserver = networkObserver) }
            composable("signup") { SignupScreen(navController, networkObserver = networkObserver) }
            composable("chat") { ChatScreen() }
            composable(CustomBottomNavBar.Chats.route) {
                ChatListScreen(
                    navController,
                    networkObserver
                )
            }
            composable(CustomBottomNavBar.Profile.route) {
                ProfileScreen(
                    navController,
                    networkObserver
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

}