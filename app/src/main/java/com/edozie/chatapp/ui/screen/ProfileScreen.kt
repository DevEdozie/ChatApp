package com.edozie.chatapp.ui.screen

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.edozie.chatapp.util.NetworkObserver
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.edozie.chatapp.R
import com.edozie.chatapp.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    networkObserver: NetworkObserver,
    vm: AuthViewModel = hiltViewModel()
) {
    val email by vm.userEmail.collectAsState(initial = "")
//    val photoUrl by vm.userPhoto.collectAsState(initial = "")
    val photoB64 by vm.userPhoto.collectAsState(initial = "")


    // image picker state
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val pickImg = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { selectedUri = it; vm.updateProfilePicture(it) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier.size(120.dp)
            ) {
//                val painter = when {
//                    selectedUri != null -> rememberAsyncImagePainter(selectedUri)
//                    photoUrl.isNotBlank() -> rememberAsyncImagePainter(photoUrl)
//                    else -> painterResource(R.drawable.placeholder_ic)
//                }
                if (selectedUri != null) {
                    // Use the freshly picked URI
                    Image(
                        painter = rememberAsyncImagePainter(selectedUri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else if (photoB64.isNotBlank()) {
                    // Decode Base64 → Bitmap → ImageBitmap
                    val bytes = Base64.decode(photoB64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    bitmap?.asImageBitmap()?.let { img ->
                        Image(
                            bitmap = img,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                } else {
                    // Placeholder icon
                    Image(
                        painter = painterResource(R.drawable.placeholder_ic),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }

                IconButton(
                    onClick = { pickImg.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.Edit, tint = Color.Gray, contentDescription = "Change")
                }


            }

            Spacer(Modifier.height(16.dp))
            Text(email.ifBlank { "Unknown User" }, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Column {
            OutlinedButton(
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    vm.logout(); navController.navigate("login") {
                    popUpTo("profile") {
                        inclusive = true
                    }
                }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
            Spacer(Modifier.height(4.dp))
        }


    }

}