package com.edozie.chatapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
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
        contentAlignment = if (threads.isEmpty()) Alignment.Center else Alignment.TopStart
    ) {
        if (threads.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.connect_chats_ic),
                    contentDescription = "No chats",
                    modifier = Modifier.size(120.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "No chats yet",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Start a new conversation with your friends by tapping the button below.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
            }
        } else {
            LazyColumn {
                items(threads) { t ->
                    ChatThreadItem(t) {
                        navController.navigate("chat/${t.id}/${t.otherUserEmail}/${t.otherUserId}")
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
                    onSuccess = { chatId, otherUid ->
                        navController.navigate("chat/$chatId/$email/$otherUid")
                    },
                    onError = { msg ->
                        Toast.makeText(localContext, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onDismiss = { vm.dismissDialog() }
        )
    }
}



