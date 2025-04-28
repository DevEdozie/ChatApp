package com.edozie.chatapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.edozie.chatapp.ui.widget.CustomTextField
import com.edozie.chatapp.util.AuthState
import com.edozie.chatapp.util.CustomBottomNavBar
import com.edozie.chatapp.util.NetworkObserver
import com.edozie.chatapp.viewmodel.AuthViewModel


@Composable
fun LoginScreen(
    navController: NavController,
    vm: AuthViewModel = hiltViewModel(),
    networkObserver: NetworkObserver
) {

    val state by vm.state.collectAsState()
    val email by vm.email.collectAsState()
    val password by vm.password.collectAsState()
    val context = LocalContext.current

    // Local UI state for validation errors
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "Login",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text("Welcome back, please enter your details")
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "Email",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        // Email field
        CustomTextField(
            value = email,
            onValueChange = {
                vm.onEmailChange(it)
                emailError = null
            },
            placeholder = "Enter your email"
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Password",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        // Password field
        CustomTextField(
            value = password,
            onValueChange = {
                vm.onPasswordChange(it)
                passwordError = null
            },
            placeholder = "Enter your password",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(20.dp))

        when (state) {
            is AuthState.Error -> Text((state as AuthState.Error).message, color = Color.Red)
            is AuthState.Authenticated -> LaunchedEffect(Unit) {
                navController.navigate(CustomBottomNavBar.Chats.route) {
                    popUpTo("login") {
                        inclusive = true
                    }
                }
            }

            else -> {}
        }
        // BUTTON SECTION

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // "Login" button
            Button(
                onClick = {
                    // Empty‑field check (email)
                    if (email.isBlank()) {
                        emailError = "Email field can not be empty"
                        return@Button
                    }
                    // Empty‑field check (password)
                    if (password.isBlank()) {
                        passwordError = "Password field can not be empty"
                        return@Button
                    }
                    // Connectivity check
                    if (!networkObserver.isOnline()) {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    vm.login(email.trim(), password.trim())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = state != AuthState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state is AuthState.Loading) CircularProgressIndicator(Modifier.size(24.dp))
                else Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // We can build an annotated string to style "Login" differently
                val annotatedText = buildAnnotatedString {
                    append("Don't Have An Account? ")
                    withStyle(style = SpanStyle(color = Color(0xFFFFA500))) {
                        append("Sign Up")
                    }
                }
                Text(
                    text = annotatedText,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        // Navigate to signup screen
                        navController.navigate("signup") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    // Create a NetworkObserver from the preview's Context
    val context = LocalContext.current.applicationContext
    val networkObserver = NetworkObserver(context)

    LoginScreen(
        navController = navController,
        networkObserver = networkObserver
    )
}




