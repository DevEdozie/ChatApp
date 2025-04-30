package com.edozie.chatapp.ui.screen

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.edozie.chatapp.util.NetworkObserver
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.edozie.chatapp.R
import com.edozie.chatapp.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ProfileScreen(
    navController: NavController,
    networkObserver: NetworkObserver,
    vm: AuthViewModel = hiltViewModel()
) {
    val email by vm.userEmail.collectAsState(initial = "")
//    val photoUrl by vm.userPhoto.collectAsState(initial = "")
    val photoB64 by vm.userPhoto.collectAsState(initial = "")

    // Alert Dialog toggle
    var showLogoutDialog by remember { mutableStateOf(false) }


    // image picker state
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    val pickImg = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { selectedUri = it; vm.updateProfilePicture(it) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .imePadding()
        ,
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
            Text(
                email.ifBlank { "Unknown User" },
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                letterSpacing = 0.15.sp,
                lineHeight = 28.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp),
                textAlign = TextAlign.Left
            )
        }

        Column {
            OutlinedButton(
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    showLogoutDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Logout",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(4.dp))
                    Image(
                        painter = painterResource(R.drawable.logout_ic),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.Red),
                        modifier = Modifier.size(24.dp)
                    )
                }

            }
            Spacer(Modifier.height(4.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(8.dp),
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    "Confirm logout",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                )
            },
            text = {
                Text(
                    "Are you sure you want to log out?",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = {
                        showLogoutDialog = false
                        vm.logout()
                        navController.navigate("login") {
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        "Logout",
                        color = MaterialTheme.colorScheme.onError
                    )
                }

            },
            dismissButton = {

                OutlinedButton(
                    shape = RoundedCornerShape(8.dp),
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }

            }
        )
    }

}