package com.byteutility.dev.leetcode.plus.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.R

@Composable
fun UserLoginScreen(
    viewModel: UserLoginViewModel = hiltViewModel(),
    onProceedClick: () -> Unit = {},
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (loginState) {
        is UserLoginViewModel.LoginState.Idle -> {
            LeetCodeUsernameScreen { username ->
                viewModel.saveUserName(username, context)
            }
        }

        is UserLoginViewModel.LoginState.Success -> {
            LaunchedEffect(Unit) {
                onProceedClick()
            }
        }

        is UserLoginViewModel.LoginState.Error -> {
            LaunchedEffect(Unit) {
                Toast.makeText(
                    context,
                    (loginState as UserLoginViewModel.LoginState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            LeetCodeUsernameScreen { username ->
                viewModel.saveUserName(username, context)
            }
        }

        else -> {}
    }
}


@Composable
fun LeetCodeUsernameScreen(
    onProceedClick: (userName: String) -> Unit,
) {
    var username by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.leetcode_plus_logo),
                contentDescription = "App Logo",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                label = { Text(text = "Enter your LeetCode username") },
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    if (username.isNotEmpty()) {
                        onProceedClick(username)
                    }
                },
                shape = RoundedCornerShape(32.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 6.dp)
            ) {
                Text(text = "Proceed")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLeetCodeLoginScreen() {
    LeetCodeUsernameScreen {}
}