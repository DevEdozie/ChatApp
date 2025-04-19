package com.edozie.chatapp.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.edozie.chatapp.util.NetworkObserver
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(navController: NavController, networkObserver: NetworkObserver) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(
            "No profile yet",
            style = TextStyle(
                fontSize = 25.sp
            )
        )
    }
}