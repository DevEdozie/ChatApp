package com.edozie.chatapp.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.edozie.chatapp.R
import com.edozie.chatapp.util.NetworkObserver

@Composable
fun ChatListScreen(
    navController: NavController,
    networkObserver: NetworkObserver
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "No chats yet",
            style = TextStyle(
                fontSize = 25.sp
            )
        )
        FloatingActionButton(
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp
            ),
            shape = CircleShape,
            containerColor = Color(0xFF007AFF),
            contentColor = Color.White,
            onClick = {
                // Show new chat dialog
            },
            modifier = Modifier
                .align(
                    Alignment.BottomEnd
                )
                .padding(16.dp)
                .size(56.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.new_chat_ic),
                "New chat",
                modifier = Modifier.padding(12.dp)
            )
        }
    }


}
