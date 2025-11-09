package com.byteutility.dev.leetcode.plus.ui.screens.problem.details

import android.webkit.WebView
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.ProblemDetailsUiState
import com.byteutility.dev.leetcode.plus.ui.theme.LeetcodePlusTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemDetailsScreen(
    titleSlug: String,
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
                uiState = uiState
            )
        }
    }
}

@Composable
fun ProblemDetailsContent(
    modifier: Modifier = Modifier,
    uiState: ProblemDetailsUiState
) {
    val textColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "${uiState.questionId}. ${uiState.title}",
            style = MaterialTheme.typography.headlineMedium,
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
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "Code and Submit")
        }
    }
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
        content = "\u003Cp\u003EGiven an array of integers \u003Ccode\u003Enums\u003C/code\u003E&nbsp;and an integer \u003Ccode\u003Etarget\u003C/code\u003E, return \u003Cem\u003Eindices of the two numbers such that they add up to \u003Ccode\u003Etarget\u003C/code\u003E\u003C/em\u003E.\u003C/p\u003E\n\n\u003Cp\u003EYou may assume that each input would have \u003Cstrong\u003E\u003Cem\u003Eexactly\u003C/em\u003E one solution\u003C/strong\u003E, and you may not use the \u003Cem\u003Esame\u003C/em\u003E element twice.\u003C/p\u003E\n\n\u003Cp\u003EYou can return the answer in any order.\u003C/p\u003E\n\n\u003Cp\u003E&nbsp;\u003C/p\u003E\n\u003Cp\u003E\u003Cstrong class=\"example\"\u003EExample 1:\u003C/strong\u003E\u003C/p\u003E\n\n\u003Cpre\u003E\n\u003Cstrong\u003EInput:\u003C/strong\u003E nums = [2,7,11,15], target = 9\n\u003Cstrong\u003EOutput:\u003C/strong\u003E [0,1]\n\u003Cstrong\u003EExplanation:\u003C/strong\u003E Because nums[0] + nums[1] == 9, we return [0, 1].\n\u003C/pre\u003E\n\n\u003Cp\u003E\u003Cstrong class=\"example\"\u003EExample 2:\u003C/strong\u003E\u003C/p\u003E\n\n\u003Cpre\u003E\n\u003Cstrong\u003EInput:\u003C/strong\u003E nums = [3,2,4], target = 6\n\u003Cstrong\u003EOutput:\u003C/strong\u003E [1,2]\n\u003C/pre\u003E\n\n\u003Cp\u003E\u003Cstrong class=\"example\"\u003EExample 3:\u003C/strong\u003E\u003C/p\u003E\n\n\u003Cpre\u003E\n\u003Cstrong\u003EInput:\u003C/strong\u003E nums = [3,3], target = 6\n\u003Cstrong\u003EOutput:\u003C/strong\u003E [0,1]\n\u003C/pre\u003E\n\n\u003Cp\u003E&nbsp;\u003C/p\u003E\n\u003Cp\u003E\u003Cstrong\u003EConstraints:\u003C/strong\u003E\u003C/p\u003E\n\n\u003Cul\u003E\n\t\u003Cli\u003E\u003Ccode\u003E2 &lt;= nums.length &lt;= 10\u003Csup\u003E4\u003C/sup\u003E\u003C/code\u003E\u003C/li\u003E\n\t\u003Cli\u003E\u003Ccode\u003E-10\u003Csup\u003E9\u003C/sup\u003E &lt;= nums[i] &lt;= 10\u003Csup\u003E9\u003C/sup\u003E\u003C/code\u003E\u003C/li\u003E\n\t\u003Cli\u003E\u003Ccode\u003E-10\u003Csup\u003E9\u003C/sup\u003E &lt;= target &lt;= 10\u003Csup\u003E9\u003C/sup\u003E\u003C/code\u003E\u003C/li\u003E\n\t\u003Cli\u003E\u003Cstrong\u003EOnly one valid answer exists.\u003C/strong\u003E\u003C/li\u003E\n\u003C/ul\u003E\n\n\u003Cp\u003E&nbsp;\u003C/p\u003E\n\u003Cstrong\u003EFollow-up:&nbsp;\u003C/strong\u003ECan you come up with an algorithm that is less than \u003Ccode\u003EO(n\u003Csup\u003E2\u003C/sup\u003E)\u003C/code\u003E\u003Cfont face=\"monospace\"\u003E&nbsp;\u003C/font\u003Etime complexity?",
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
                uiState = mockUiState
            )
        }
    }
}
