package com.byteutility.dev.leetcode.plus.ui.screens.problem.details

import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.CodeEditorSubmitActivity
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.CodeSnippet
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.ProblemDetailsUiState
import com.byteutility.dev.leetcode.plus.ui.theme.LeetcodePlusTheme
import com.byteutility.dev.leetcode.plus.utils.ProgressDialogUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemDetailsScreen(
    titleSlug: String,
    onLeetcodeLoginVerify: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: ProblemDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${uiState.questionId}. ${uiState.title}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee()
                    )
                },

                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding() // Ensures it doesn't overlap with Android nav pill
                        .padding(16.dp)
                ) {
                    Button(
                        {
                            if (uiState.codeSnippets.isNotEmpty()) {
                                showDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.codeSnippets.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF498A5C) // Your Emerald Green
                        )
                    ) {
                        Text(
                            text = "Code and Submit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->

        if (showDialog) {
            LanguageSelectionSheet(
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

        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProgressDialogUtil.ShowGradientProgressDialog(
                    message = "Loading",
                    showDialog = true
                )
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
                onLeetcodeLoginVerify,
                onBack
            )
        }
    }
}

@Composable
fun ProblemDetailsContent(
    modifier: Modifier,
    uiState: ProblemDetailsUiState,
    onLeetcodeLoginVerify: () -> Unit,
    onBack: () -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 0.dp)
    ) {
        if (uiState.isPremiumContent) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val premiumText = buildAnnotatedString {
                    append("This problem is part of LeetCode Premium. Subscribe on ")
                    val link = LinkAnnotation.Url(
                        "https://leetcode.com/subscribe/?ref=lp_pl&source=qd",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                                fontWeight = Bold
                            )
                        )
                    )
                    pushLink(link)
                    append("LeetCode")
                    pop()
                    append(" to unlock access and start solving.")
                }

                Text(
                    text = premiumText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = {
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Text(text = "Back to All Problems")
                }
            }

        }
        ProblemHeader(uiState)
        ConnectionVerifyBanner { onLeetcodeLoginVerify() }
        ProblemDescriptionWebView(uiState, textColor)
    }
}

@Composable
private fun ColumnScope.ProblemDescriptionWebView(
    uiState: ProblemDetailsUiState,
    textColor: Color
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth(),
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = false // Keep it safe unless you need interactivity
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                    }
                    setBackgroundColor(0)
                }
            },
            update = { webView ->
                val styledHtml = getStyledHtml(uiState.content, textColor)
                webView.loadDataWithBaseURL(
                    "https://leetcode.com", // Providing a base URL helps with relative links/images
                    styledHtml,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        )
    }
}

@Composable
fun ConnectionVerifyBanner(
    onLeetcodeLoginVerify: () -> Unit
) {
    val brandGreen = Color(0xFF498A5C)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        color = brandGreen.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, brandGreen.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VpnKey, // Or Icons.Default.Link
                    contentDescription = null,
                    tint = brandGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "LeetCode Session",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(
                onClick = onLeetcodeLoginVerify,
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text(
                    text = "Verify connection",
                    style = MaterialTheme.typography.labelLarge,
                    color = brandGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ProblemHeader(uiState: ProblemDetailsUiState) {
    val brandGreen = Color(0xFF498A5C)

    // Determine semantic color based on difficulty
    val difficultyColor = when (uiState.difficulty.lowercase()) {
        "easy" -> Color(0xFF00AF9B) // LeetCode Easy Green
        "medium" -> Color(0xFFFFB800) // LeetCode Medium Orange
        "hard" -> Color(0xFFFF2D55) // LeetCode Hard Red
        else -> MaterialTheme.colorScheme.outline
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = difficultyColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, difficultyColor.copy(alpha = 0.2f))
            ) {
                Text(
                    text = uiState.difficulty,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = difficultyColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = uiState.category,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionSheet(
    codeSnippets: List<CodeSnippet>,
    onLanguageSelected: (CodeSnippet) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF498A5C).copy(alpha = 0.4f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp) // Extra padding for navigation bar
        ) {
            Text(
                text = "Select Language",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            LazyColumn {
                items(codeSnippets) { snippet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(snippet) }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = snippet.lang,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Subtle indicator for the Emerald theme
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

private fun getStyledHtml(content: String, textColor: Color): String {
    val hexColor = toHex(textColor)
    return """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                :root {
                    color-scheme: light dark;
                }
                body {
                    color: $hexColor;
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                    font-size: 15px;
                    line-height: 1.6;
                    margin: 0;
                    padding: 0px;
                    background-color: transparent;
                }
                /* Style Code Blocks (LeetCode specific) */
                pre, code {
                    background-color: rgba(0, 0, 0, 0.05);
                    border-radius: 4px;
                    font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, monospace;
                    padding: 2px 4px;
                }
                pre {
                    padding: 12px;
                    overflow-x: auto;
                    border: 1px solid rgba(0, 0, 0, 0.1);
                    margin: 16px 0;
                }
                /* Handle Images to prevent overflow */
                img {
                    max-width: 100%;
                    height: auto;
                    border-radius: 8px;
                }
                /* Style Lists */
                ul, ol {
                    padding-left: 20px;
                }
                li {
                    margin-bottom: 8px;
                }
                /* Emerald Accent for links */
                a {
                    color: #498A5C;
                    text-decoration: none;
                    font-weight: bold;
                }
                /* Style Tables (often used in LeetCode descriptions) */
                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin: 16px 0;
                }
                th, td {
                    border: 1px solid rgba(0, 0, 0, 0.1);
                    padding: 8px;
                    text-align: left;
                }
            </style>
        </head>
        <body>
            $content
        </body>
        </html>
    """.trimIndent()
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
                onLeetcodeLoginVerify = {},
                onBack = {}
            )
        }
    }
}
