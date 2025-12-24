package com.byteutility.dev.leetcode.plus.ui.screens.settings

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.BuildConfig
import com.byteutility.dev.leetcode.plus.core.settings.config.IntervalConfigurations
import com.byteutility.dev.leetcode.plus.core.settings.model.IntervalOption
import com.byteutility.dev.leetcode.plus.ui.screens.settings.composables.DailyProblemWidgetPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userInfo by viewModel.userBasicInfo.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastSyncTime.collectAsStateWithLifecycle()
    val syncInterval by viewModel.syncInterval.collectAsStateWithLifecycle()
    val notificationInterval by viewModel.notificationInterval.collectAsStateWithLifecycle()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSyncIntervalDialog by remember { mutableStateOf(false) }
    var showNotificationIntervalDialog by remember { mutableStateOf(false) }
    var isWidgetPinned by remember { mutableStateOf(viewModel.isDailyProblemWidgetPinned()) }

    LifecycleResumeEffect(Unit) {
        isWidgetPinned = viewModel.isDailyProblemWidgetPinned()
        onPauseOrDispose {}
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showSyncIntervalDialog) {
        IntervalSelectionDialog(
            intervalOptions = IntervalConfigurations.syncIntervalOptions,
            currentInterval = syncInterval,
            onConfirm = { newInterval ->
                showSyncIntervalDialog = false
                viewModel.updateSyncInterval(newInterval)
            },
            onDismiss = { showSyncIntervalDialog = false },
            dialogTitle = "Set Sync Interval",
            dialogSubtitle = "Choose how often to sync data:"
        )
    }

    if (showNotificationIntervalDialog) {
        IntervalSelectionDialog(
            intervalOptions = IntervalConfigurations.notificationIntervalOptions,
            currentInterval = notificationInterval,
            onConfirm = { newInterval ->
                showNotificationIntervalDialog = false
                viewModel.updateNotificationInterval(newInterval)
            },
            onDismiss = { showNotificationIntervalDialog = false },
            dialogTitle = "Set Notification Interval",
            dialogSubtitle = "Choose how often to receive push notifications:"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFABDEF5).copy(alpha = 0.1f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Account Section
            SettingsSectionCard(
                title = "Account",
                icon = Icons.Default.Person
            ) {
                if (userInfo.userName.isNotEmpty()) {
                    SettingsInfoRow(
                        label = "Username",
                        value = userInfo.userName
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    SettingsInfoRow(
                        label = "Name",
                        value = userInfo.name
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    SettingsInfoRow(
                        label = "Ranking",
                        value = "#${userInfo.ranking}"
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    SettingsActionRow(
                        icon = Icons.Default.ExitToApp,
                        label = "Logout",
                        iconTint = Color.Red,
                        onClick = { showLogoutDialog = true }
                    )
                }
            }

            // Data Sync Section
            SettingsSectionCard(
                title = "Data Sync",
                icon = Icons.Default.Refresh
            ) {
                SettingsClickableRow(
                    label = "Sync Interval",
                    value = "$syncInterval minutes",
                    onClick = { showSyncIntervalDialog = true }
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsInfoRow(
                    label = "Last Synced",
                    value = lastSyncTime
                )
            }

            // notification
            SettingsSectionCard(
                title = "Notifications",
                icon = Icons.Default.NotificationImportant
            ) {
                SettingsClickableRow(
                    label = "Notifications Interval",
                    value = "$notificationInterval minutes",
                    onClick = { showNotificationIntervalDialog = true }
                )
            }

            // widget
            SettingsSectionCard(
                title = "Daily Problem Widget",
                icon = Icons.Default.Widgets
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isWidgetPinned) "Widget Pinned" else "Pin Widget",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        enabled = !isWidgetPinned,
                        checked = isWidgetPinned,
                        onCheckedChange = {
                            if (it) {
                                viewModel.requestPinWidget()
                            }
                            isWidgetPinned = it
                        }
                    )
                }

                if (!isWidgetPinned) {
                    Text(
                        text = "Add daily problem widget in your home",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    DailyProblemWidgetPlaceholder()
                }
            }

            // App Information Section
            SettingsSectionCard(
                title = "App Information",
                icon = Icons.Default.Info
            ) {
                SettingsInfoRow(
                    label = "Version",
                    value = BuildConfig.VERSION_NAME
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsInfoRow(
                    label = "Version Code",
                    value = BuildConfig.VERSION_CODE.toString()
                )
            }

            // About Section
            SettingsSectionCard(
                title = "About",
                icon = Icons.Default.Info
            ) {
                Text(
                    text = "LeetCode Plus is your companion app for tracking your LeetCode progress, managing weekly goals, and staying updated with upcoming contests.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Features:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                BulletPoint("Track your LeetCode statistics and progress")
                BulletPoint("Set and monitor weekly goals")
                BulletPoint("View daily challenges")
                BulletPoint("Browse all LeetCode problems")
                BulletPoint("Get contest reminders")
                BulletPoint("Access video solutions")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF4CAF50), Color.LightGray)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Section Header with gradient
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradientBrush)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Section Content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    label: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = iconTint
        )
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsClickableRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun IntervalSelectionDialog(
    intervalOptions: List<IntervalOption>,
    currentInterval: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
    dialogTitle: String,
    dialogSubtitle: String
) {
    var selectedInterval by remember { mutableLongStateOf(currentInterval) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = dialogTitle) },
        text = {
            Column {
                Text(
                    text = dialogSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                intervalOptions.forEach { interval ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedInterval = interval.time }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        val intervalText = buildAnnotatedString {
                            append(interval.displayLabel)
                            if (interval.isDefault) {
                                append(" (Default)")
                            }
                        }

                        Text(
                            text = intervalText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selectedInterval == interval.time) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            fontWeight = if (selectedInterval == interval.time) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                        if (selectedInterval == interval.time) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedInterval) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Confirm Logout") },
        text = { Text(text = "Are you sure you want to logout?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Logout")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
