package com.edozie.chatapp.ui.screen

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
import com.edozie.chatapp.utils.AuthState
import com.edozie.chatapp.viewmodel.AuthViewModel


@Composable
fun SignupScreen(navController: NavController, vm: AuthViewModel = hiltViewModel()) {

    val state by vm.state.collectAsState()
    val email by vm.email.collectAsState()
    val password by vm.password.collectAsState()
    val confirmPassword by vm.confirm_password.collectAsState()

    // Local UI state for validation error
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
            "Sign up",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text("Create an account to get started with us.")
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
            onValueChange = { vm.onEmailChange(it) },
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
                // clear error when user retypes
                if (passwordError != null) passwordError = null
            },
            placeholder = "Create your password",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(4.dp))
        // Confirm field
        CustomTextField(
            value = confirmPassword,
            onValueChange = {
                vm.onConfirmPasswordChange(it)
                // clear error when user retypes
                if (passwordError != null) passwordError = null
            },
            placeholder = "Confirm password",
            isPassword = true
        )
        // Show mismatch error
        passwordError?.let { errorMsg ->
            Spacer(Modifier.height(4.dp))
            Text(errorMsg, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
        // Show Firebase errors or navigate on success
        when (state) {
            is AuthState.Error -> Text((state as AuthState.Error).message, color = Color.Red)
            is AuthState.Authenticated -> LaunchedEffect(Unit) {
                navController.navigate("login") { popUpTo("signup") { inclusive = true } }
            }

            else -> {}
        }
        // BUTTON SECTION

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // "Create Account" button
            Button(
                onClick = {
                    // Validate passwords match before calling ViewModel
                    if (password != confirmPassword) {
                        passwordError = "Passwords do not match"
                    } else {
                        passwordError = null
                        vm.signUp(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state is AuthState.Loading) CircularProgressIndicator(Modifier.size(24.dp))
                else Text(text = "Create Account")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // We can build an annotated string to style "Login" differently
                val annotatedText = buildAnnotatedString {
                    append("Already Have An Account? ")
                    withStyle(style = SpanStyle(color = Color(0xFFFFA500))) {
                        append("Login")
                    }
                }
                Text(
                    text = annotatedText,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        // Navigate to signup screen
                        navController.navigate("login") {
                            popUpTo("signup") { inclusive = true }
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
fun SignupScreenPreview() {
    val navController = rememberNavController()
    SignupScreen(navController = navController)
}



