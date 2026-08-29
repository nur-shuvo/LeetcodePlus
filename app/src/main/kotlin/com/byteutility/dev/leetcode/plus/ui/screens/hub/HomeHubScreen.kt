package com.byteutility.dev.leetcode.plus.ui.screens.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.byteutility.dev.leetcode.plus.ui.theme.LeetcodePlusTheme

private val LeetCodeGradient = listOf(Color(0xFF66D98E), Color(0xFF1E8E4F))
private val MockInterviewGradient = listOf(Color(0xFF8C7CF0), Color(0xFF4B2FB3))

@Composable
fun HomeHubScreen(
    onLeetCodeClick: () -> Unit = {},
    onMockInterviewClick: () -> Unit = {}
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            HubHeader()
            Spacer(modifier = Modifier.height(36.dp))
            HubCard(
                title = "LeetCode",
                subtitle = "Daily problems, stats, and submissions",
                icon = Icons.Default.Code,
                gradientColors = LeetCodeGradient,
                onClick = onLeetCodeClick
            )
            Spacer(modifier = Modifier.height(20.dp))
            HubCard(
                title = "Mock Interview (Beta)",
                subtitle = "Get matched with a peer for a live practice round",
                icon = Icons.Default.Videocam,
                gradientColors = MockInterviewGradient,
                onClick = onMockInterviewClick
            )
        }
    }
}

@Composable
private fun HubHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AlgoCode Plus",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Choose your path",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HubCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(28.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Row(
                modifier = Modifier.align(Alignment.BottomEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeHubScreenPreview() {
    LeetcodePlusTheme {
        HomeHubScreen()
    }
}
