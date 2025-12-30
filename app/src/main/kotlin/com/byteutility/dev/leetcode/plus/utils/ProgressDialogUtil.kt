package com.byteutility.dev.leetcode.plus.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * A beautiful, modern progress dialog utility for Jetpack Compose.
 * Features a gradient circular progress indicator with smooth animations.
 */
object ProgressDialogUtil {

    /**
     * Displays an attractive circular progress dialog
     *
     * @param message The message to display below the progress indicator
     * @param showDialog Whether to show the dialog
     * @param onDismiss Callback when dialog is dismissed (optional)
     */
    @Composable
    fun ShowProgressDialog(
        message: String = "Please wait...",
        showDialog: Boolean = true,
        onDismiss: (() -> Unit)? = null
    ) {
        if (showDialog) {
            Dialog(
                onDismissRequest = { onDismiss?.invoke() },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Surface(
                    modifier = Modifier
                        .size(200.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Animated circular progress indicator with gradient effect
                        AnimatedGradientProgressIndicator()

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    /**
     * An animated gradient circular progress indicator with smooth rotation
     */
    @Composable
    private fun AnimatedGradientProgressIndicator() {
        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .rotate(rotation),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp),
                strokeWidth = 6.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }

    /**
     * Alternative variant with custom gradient colors
     *
     * @param message The message to display below the progress indicator
     * @param showDialog Whether to show the dialog
     * @param primaryColor Primary color for the progress indicator
     * @param secondaryColor Secondary color for gradient effect (optional)
     * @param onDismiss Callback when dialog is dismissed (optional)
     */
    @Composable
    fun ShowGradientProgressDialog(
        message: String = "Loading...",
        showDialog: Boolean = true,
        primaryColor: Color = MaterialTheme.colorScheme.primary,
        secondaryColor: Color? = null,
        onDismiss: (() -> Unit)? = null
    ) {
        if (showDialog) {
            Dialog(
                onDismissRequest = { onDismiss?.invoke() },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
//                val gradientColors = if (secondaryColor != null) {
//                    listOf(primaryColor, secondaryColor)
//                } else {
//                    listOf(
//                        primaryColor,
//                        primaryColor.copy(alpha = 0.7f)
//                    )
//                }

                Surface(
                    modifier = Modifier.size(220.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 12.dp,
                    shadowElevation = 12.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val infiniteTransition = rememberInfiniteTransition(label = "rotation")
                            val rotation by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(
                                        durationMillis = 1200,
                                        easing = LinearEasing
                                    ),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "rotation"
                            )

                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .rotate(rotation)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(90.dp),
                                    strokeWidth = 7.dp,
                                    color = primaryColor,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    strokeCap = StrokeCap.Round
                                )
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}