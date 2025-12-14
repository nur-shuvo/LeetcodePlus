package com.byteutility.dev.leetcode.plus.ui.screens.login

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.ui.screens.login.model.LoginState
import com.byteutility.dev.leetcode.plus.utils.ProgressDialogUtil

@Composable
fun UserLoginScreen(
    viewModel: UserLoginViewModel = hiltViewModel(),
    onProceedClick: () -> Unit = {},
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (loginState) {
        is LoginState.Idle -> {
            LeetCodeUsernameScreen { username ->
                viewModel.saveUserName(username, context)
            }
        }

        is LoginState.Loading -> {
            LeetCodeUsernameScreen { username ->
                viewModel.saveUserName(username, context)
            }
            
            ProgressDialogUtil.ShowGradientProgressDialog(
                message = "Fetching user profile...",
                showDialog = true
            )
        }

        is LoginState.Success -> {
            LaunchedEffect(Unit) {
                onProceedClick()
            }
        }

        is LoginState.Error -> {
            LaunchedEffect(Unit) {
                Toast.makeText(
                    context,
                    (loginState as LoginState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            LeetCodeUsernameScreen { username ->
                viewModel.saveUserName(username, context)
            }
        }
    }
}


@Composable
fun LeetCodeUsernameScreen(
    onProceedClick: (userName: String) -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var showHelp by remember { mutableStateOf(false) }

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
                painter = painterResource(id = R.drawable.app_icon_playstore),
                contentDescription = "App Logo",
            )

            OutlinedTextField(
                label = { Text("Enter your LeetCode username") },
                value = username,
                onValueChange = { username = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            TextButton(onClick = { showHelp = !showHelp }) {
                Text(
                    text = if (showHelp) "Hide help" else "How to find your username?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            AnimatedVisibility(visible = showHelp) {
                Image(
                    painter = painterResource(id = R.drawable.leetcode_username_guide),
                    contentDescription = "LeetCode Username Guide",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                )
            }

            Button(
                onClick = {
                    if (username.isNotEmpty()) onProceedClick(username)
                },
                shape = RoundedCornerShape(32.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 6.dp)
            ) {
                Text("Proceed")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLeetCodeLoginScreen() {
    LeetCodeUsernameScreen {}
}