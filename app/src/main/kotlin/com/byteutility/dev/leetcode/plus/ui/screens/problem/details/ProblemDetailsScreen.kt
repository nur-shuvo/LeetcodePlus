package com.byteutility.dev.leetcode.plus.ui.screens.problem.details

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.CodeEditorSubmitActivity
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.CodeSnippet
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.ProblemDetailsUiState
import com.byteutility.dev.leetcode.plus.ui.theme.LeetcodePlusTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemDetailsScreen(
    titleSlug: String,
    onLeetcodeLoginVerify: () -> Unit = {}
) {
    val viewModel: ProblemDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getQuestionDetails(titleSlug)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Problem Detail", fontSize = 20.sp, fontWeight = Bold) })
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Error: ${uiState.error}")
            }
        } else {
            ProblemDetailsContent(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                titleSlug = titleSlug,
                onLeetcodeLoginVerify
            )
        }
    }
}

@Composable
fun ProblemDetailsContent(
    modifier: Modifier = Modifier,
    uiState: ProblemDetailsUiState,
    titleSlug: String,
    onLeetcodeLoginVerify: () -> Unit
) {
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        LanguageSelectionDialog(
            codeSnippets = uiState.codeSnippets,
            onLanguageSelected = { snippet ->
                showDialog = false
                context.startActivity(
                    CodeEditorSubmitActivity.getIntent(
                        context,
                        titleSlug,
                        uiState.questionId,
                        snippet.langSlug,
                        snippet.code
                    )
                )
            },
            onDismiss = { showDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "${uiState.questionId}. ${uiState.title}",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 28.sp
            ),
            fontWeight = Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = uiState.difficulty,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = uiState.category, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = {
                    onLeetcodeLoginVerify.invoke()
                }
            ) {
                Text(text = "Verify connection", fontSize = 16.sp)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth(),
                factory = { context ->
                    WebView(context).apply {
                        setBackgroundColor(0) // transparent background
                    }
                },
                update = { webView ->
                    val htmlContent = """
                        <html>
                        <head>
                            <style>
                                body {
                                    color: ${toHex(textColor)};
                                    background-color: transparent;
                                }
                                a {
                                    color: #0000FF;
                                }
                            </style>
                        </head>
                        <body>
                            ${uiState.content}
                        </body>
                        </html>
                    """.trimIndent()
                    webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                }
            )
        }

        Button(
            onClick = {
                if (uiState.codeSnippets.isNotEmpty()) {
                    showDialog = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = uiState.codeSnippets.isNotEmpty()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(text = "Code and Submit")
            }
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    codeSnippets: List<CodeSnippet>,
    onLanguageSelected: (CodeSnippet) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Language") },
        text = {
            Column {
                codeSnippets.forEach { snippet ->
                    Text(
                        text = snippet.lang,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(snippet) }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun toHex(color: Color): String {
    val red = (color.red * 255).toInt()
    val green = (color.green * 255).toInt()
    val blue = (color.blue * 255).toInt()
    return String.format("#%02x%02x%02x", red, green, blue)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ProblemDetailsScreenPreview() {
    val mockUiState = ProblemDetailsUiState(
        questionId = "1",
        title = "Two Sum",
        difficulty = "Easy",
        category = "Array",
        content = "...",
        isLoading = false,
        error = null
    )
    LeetcodePlusTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Problem Detail") })
            }
        ) { paddingValues ->
            ProblemDetailsContent(
                modifier = Modifier.padding(paddingValues),
                uiState = mockUiState,
                titleSlug = "two-sum"
            ) {}
        }
    }
}