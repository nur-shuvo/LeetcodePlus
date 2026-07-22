package com.byteutility.dev.leetcode.plus.ui.screens.interview.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.byteutility.dev.leetcode.plus.ui.screens.interview.InterviewHowItWorksContent

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
    var showHowItWorks by remember { mutableStateOf(false) }

    // Already signed in with a saved profile (e.g. a returning user) - skip straight past the
    // setup form instead of making them re-save their roles every time.
    LaunchedEffect(currentUser, profile) {
        if (currentUser != null && profile?.roles?.isNotEmpty() == true) {
            onProfileSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mock Interview Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
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
                TextButton(onClick = { showHowItWorks = !showHowItWorks }) {
                    Text(if (showHowItWorks) "Hide details" else "How does this work?")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (showHowItWorks) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                AnimatedVisibility(visible = showHowItWorks) {
                    InterviewHowItWorksContent()
                }
                Button(
                    onClick = { viewModel.signIn(context) },
                    enabled = !isSigningIn,
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                ) {
                    if (isSigningIn) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Continue")
                }
            }
        }
    }
}
