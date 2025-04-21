package com.edozie.chatapp.ui.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.edozie.chatapp.util.CustomBottomNavBar


@Composable
fun CustomBottomNavigationBar(
    currentDestination: String,
    onNavigate: (String) -> Unit
) {

    val bottomNavItems = listOf(
        CustomBottomNavBar.Chats,
        CustomBottomNavBar.Profile
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = Color.White,
        tonalElevation = 4.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                selected = selected,
                onClick = { onNavigate(item.route) },
                alwaysShowLabel = false,        // hide labels
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White, // active blue
                    unselectedIconColor = Color.Gray,         // inactive grey
                    indicatorColor = Color(0xFF007AFF)  // pill color
                )
            )
        }
    }


//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
//    ) {
//        BottomNavigation(
//            backgroundColor = Color.White,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            bottomNavItems.forEach { item ->
//                BottomNavigationItem(
//                    selected = currentDestination == item.route,
//                    onClick = { onNavigate(item.route) },
//                    icon = {
//                        Icon(
//                            painter = painterResource(item.icon),
//                            contentDescription = item.label,
//                            tint = Color.Gray,
//                            modifier = Modifier
//                                .size(24.dp) // Set a fixed size here
//                        )
//                    }
//                )
//            }
//        }
//    }

}
