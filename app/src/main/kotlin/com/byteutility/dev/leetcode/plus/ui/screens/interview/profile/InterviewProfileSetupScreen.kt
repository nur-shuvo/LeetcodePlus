package com.byteutility.dev.leetcode.plus.ui.screens.interview.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewProfileSetupScreen(
    onBack: () -> Unit = {},
    onProfileSaved: () -> Unit = {},
    viewModel: InterviewProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val isSigningIn by viewModel.isSigningIn.collectAsStateWithLifecycle()

    var selectedRoles by remember(profile) { mutableStateOf(profile?.roles?.toSet() ?: emptySet()) }
    var leetcodeHandle by remember(profile) { mutableStateOf(profile?.leetcodeHandle ?: "") }

    // Already signed in with a saved profile (e.g. a returning user) - skip straight past the
    // setup form instead of making them re-save their roles every time.
    LaunchedEffect(currentUser, profile) {
        if (currentUser != null && profile?.roles?.isNotEmpty() == true) {
            onProfileSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mock Interview Profile") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentUser == null) {
                Text(
                    text = "Mock interviews match you with another LeetCodePlus user in " +
                        "real time for a live practice session.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Google sign-in is required so we know who you are when pairing " +
                        "you with a peer - this is separate from your LeetCode account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { viewModel.signIn(context) },
                    enabled = !isSigningIn,
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                ) {
                    if (isSigningIn) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                    }
                    Text("Sign in with Google")
                }
            } else {
                Text(
                    text = "Which roles do you want to practice?",
                    style = MaterialTheme.typography.titleMedium
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(InterviewRole.entries) { role ->
                        FilterChip(
                            selected = selectedRoles.contains(role),
                            onClick = {
                                selectedRoles = if (selectedRoles.contains(role)) {
                                    selectedRoles - role
                                } else {
                                    selectedRoles + role
                                }
                            },
                            label = { Text(role.displayName) }
                        )
                    }
                }

                OutlinedTextField(
                    value = leetcodeHandle,
                    onValueChange = { leetcodeHandle = it },
                    label = { Text("LeetCode handle (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        viewModel.saveRoles(selectedRoles.toList(), leetcodeHandle)
                        onProfileSaved()
                    },
                    enabled = selectedRoles.isNotEmpty()
                ) {
                    Text("Save profile")
                }
            }
        }
    }
}
