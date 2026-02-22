package com.byteutility.dev.leetcode.plus.ui.networkmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.byteutility.dev.leetcode.plus.network.cache.NetworkLogCache
import com.byteutility.dev.leetcode.plus.network.model.NetworkLog
import com.byteutility.dev.leetcode.plus.ui.theme.LeetcodePlusTheme
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NetworkMonitorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LeetcodePlusTheme {
                NetworkMonitorScreen(onClose = { finish() })
            }
        }
    }
}

@Composable
private fun NetworkMonitorScreen(onClose: () -> Unit) {
    var selectedLogId by remember { mutableStateOf<String?>(null) }

    if (selectedLogId != null) {
        val log = NetworkLogCache.getById(selectedLogId!!)
        if (log != null) {
            NetworkLogDetailScreen(
                log = log,
                onBack = { selectedLogId = null },
            )
            return
        }
    }

    NetworkLogListScreen(
        onClose = onClose,
        onItemClick = { selectedLogId = it },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NetworkLogListScreen(
    onClose: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    val logs by NetworkLogCache.logs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Network Monitor (${logs.size})") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = { NetworkLogCache.clear() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear logs")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        },
    ) { paddingValues ->
        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No network requests yet",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                items(logs, key = { it.id }) { log ->
                    NetworkLogItem(log = log, onClick = { onItemClick(log.id) })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun NetworkLogItem(log: NetworkLog, onClick: () -> Unit) {
    val statusColor = when {
        log.error != null -> Color(0xFFE53935)
        log.responseCode in 200..299 -> Color(0xFF43A047)
        log.responseCode in 300..399 -> Color(0xFFFB8C00)
        log.responseCode >= 400 -> Color(0xFFE53935)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val timeFormatter = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MethodBadge(method = log.method)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = log.url.removePrefix("https://").removePrefix("http://"),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = timeFormatter.format(Date(log.timestamp)),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${log.durationMs}ms",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (log.error != null) "ERR" else log.responseCode.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor,
        )
    }
}

@Composable
private fun MethodBadge(method: String) {
    val color = when (method.uppercase()) {
        "GET" -> Color(0xFF1976D2)
        "POST" -> Color(0xFF388E3C)
        "PUT" -> Color(0xFFF57C00)
        "DELETE" -> Color(0xFFD32F2F)
        "PATCH" -> Color(0xFF7B1FA2)
        else -> Color(0xFF455A64)
    }
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color,
    ) {
        Text(
            text = method,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NetworkLogDetailScreen(log: NetworkLog, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${log.method} ${log.url.substringAfterLast("/")}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Overview
            DetailCard(title = "Overview") {
                DetailRow("URL", log.url)
                DetailRow("Method", log.method)
                DetailRow("Status", if (log.error != null) "ERROR" else log.responseCode.toString())
                DetailRow("Duration", "${log.durationMs}ms")
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                DetailRow("Time", formatter.format(Date(log.timestamp)))
                if (log.error != null) {
                    DetailRow("Error", log.error)
                }
            }

            // Request
            DetailCard(title = "Request Headers") {
                if (log.requestHeaders.isEmpty()) {
                    Text(
                        text = "(none)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    log.requestHeaders.forEach { (key, value) ->
                        DetailRow(key, value)
                    }
                }
            }

            if (log.requestBody != null) {
                DetailCard(title = "Request Body") {
                    CodeText(text = log.requestBody)
                }
            }

            // Response
            CollapsibleDetailCard(title = "Response Headers") {
                if (log.responseHeaders.isEmpty()) {
                    Text(
                        text = "(none)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    log.responseHeaders.forEach { (key, value) ->
                        DetailRow(key, value)
                    }
                }
            }

            if (log.responseBody != null) {
                DetailCard(title = "Response Body") {
                    CodeText(text = log.responseBody)
                }
            }
        }
    }
}

@Composable
private fun DetailCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun CollapsibleDetailCard(title: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}

@Composable
private fun DetailRow(key: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = key,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            fontSize = 12.sp,
        )
    }
    Spacer(modifier = Modifier.height(2.dp))
}

@Composable
private fun CodeText(text: String) {
    val formatted = remember(text) { prettyPrintJson(text) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp),
            )
            .padding(8.dp),
    ) {
        Text(
            text = formatted,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
        )
    }
}

private fun prettyPrintJson(raw: String): String {
    val trimmed = raw.trim()
    return try {
        when {
            trimmed.startsWith("{") -> JSONObject(trimmed).toString(2)
            trimmed.startsWith("[") -> JSONArray(trimmed).toString(2)
            else -> raw
        }
    } catch (_: Exception) {
        raw
    }
}
