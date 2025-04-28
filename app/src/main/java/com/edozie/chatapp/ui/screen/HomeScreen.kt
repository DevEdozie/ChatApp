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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edozie.chatapp.R
import com.edozie.chatapp.ui.widget.CustomBottomNavigationBar
import com.edozie.chatapp.util.CustomBottomNavBar
import com.edozie.chatapp.util.NetworkObserver
import com.edozie.chatapp.viewmodel.ChatViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(networkObserver: NetworkObserver) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val userCurrentRoute = currentBackStackEntry?.destination?.route

    val screenTitle = when (userCurrentRoute) {
        CustomBottomNavBar.Chats.route -> "Chats"
        CustomBottomNavBar.Profile.route -> "Profile"
        else -> ""
    }

    val shouldShowTitle = userCurrentRoute == CustomBottomNavBar.Chats.route ||
            userCurrentRoute == CustomBottomNavBar.Profile.route


//    val shouldShowAppIcon =
//        userCurrentRoute == CustomBottomNavBar.Chats.route


    val shouldShowBackArrow =
        userCurrentRoute?.startsWith("chat/") == true

    val showBottomBar = when {
        userCurrentRoute == CustomBottomNavBar.Chats.route -> true
        userCurrentRoute == CustomBottomNavBar.Profile.route -> true
        else -> false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (shouldShowBackArrow) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        if (shouldShowAppIcon) {
//                            Image(
//                                painter = painterResource(id = R.drawable.chat_app_logo_ic),
//                                contentDescription = "App Logo",
//                                modifier = Modifier.size(40.dp)
//                            )
//                            Spacer(Modifier.width(8.dp))
//                        }

                        if (shouldShowTitle) {
                            Text(
                                text = screenTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black
                            )
                        }
                    }
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
            startDestination = "splash",
//            startDestination = CustomBottomNavBar.Chats.route,
            modifier = Modifier
                .padding(paddingValues)
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("login") { LoginScreen(navController, networkObserver = networkObserver) }
            composable("signup") { SignupScreen(navController, networkObserver = networkObserver) }
            composable(
                "chat/{chatId}/{otherEmail}/{otherUid}",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                    navArgument("otherEmail") { type = NavType.StringType },
                    navArgument("otherUid") { type = NavType.StringType }),
            ) { backStack ->
                val vm: ChatViewModel = hiltViewModel()
                val cid = backStack.arguments!!.getString("chatId")!!
                val otherE = backStack.arguments!!.getString("otherEmail")!!
                val otherUid = backStack.arguments!!.getString("otherUid")!!
                LaunchedEffect(cid) {
                    vm.start(cid, otherE, otherUid)
                }
                ChatScreen(vm)
            }
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
