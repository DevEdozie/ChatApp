package com.edozie.chatapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.edozie.chatapp.R
import com.edozie.chatapp.ui.widget.ChatThreadItem
import com.edozie.chatapp.ui.widget.NewChatDialog
import com.edozie.chatapp.util.NetworkObserver
import com.edozie.chatapp.viewmodel.ChatListViewModel

@Composable
fun ChatListScreen(
    navController: NavController,
    networkObserver: NetworkObserver,
    vm: ChatListViewModel = hiltViewModel()
) {


    val threads by vm.threads.collectAsState()
    val showDialog by vm.showDialog.collectAsState()

    val localContext = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (threads.isEmpty()) {
            Text("No chats yet", Modifier.align(Alignment.Center))
        } else {
            LazyColumn {
                items(threads) { t ->
                    ChatThreadItem(t) {
                        navController.navigate("chat/${t.id}/${t.otherUserEmail}")
                    }
                }
            }
        }
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
                vm.showNewChatDialog()
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

    if (showDialog) {
        NewChatDialog(
            onEmailSubmit = { email ->
                vm.createChatWith(
                    email,
                    onSuccess = { id -> navController.navigate("chat/$id/$email") },
                    onError = { msg ->
                        Toast.makeText(localContext, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onDismiss = { vm.dismissDialog() }
        )
    }
}



