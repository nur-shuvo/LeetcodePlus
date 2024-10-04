package com.byteutility.dev.leetcode.plus.ui.userdetails

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.byteutility.dev.leetcode.plus.R
import androidx.compose.foundation.layout.Arrangement.SpaceBetween as SpaceBetween1


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    user: LeetCodeUser,
    submissions: List<Submission>,
) {
    Scaffold(
        topBar = {
            //TopAppBar(title = { Text(text = "LeetCode User Profile") })
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UserProfileCard(user)
            UserStatisticsCard(user)
            ProblemCategoriesSolved(
                easySolved = 7222,
                easyTotal = 7851,
                mediumSolved = 5099,
                mediumTotal = 3241,
                hardSolved = 3753,
                hardTotal = 1478
            )

            Text("Recent AC", fontSize = 16.sp)

            LatestSubmissionsList(submissions)
        }
    }
}

@Composable
fun UserProfileCard(user: LeetCodeUser) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Image(
                painter = painterResource(id = R.drawable.icon_article_background),
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column {
                Text(text = user.username)
                Text(text = "Problems Solved: ${user.totalProblemsSolved}")
                Text(text = "Ranking: ${user.globalRanking}")
            }
        }
    }
}

@Composable
fun UserStatisticsCard(user: LeetCodeUser) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Contest Rating: ${user.contestRating}")
            Text(text = "Badge: ${user.badge}")
            Text(text = "Streak: ${user.streakDays} days")
        }
    }
}

@Composable
fun ProblemCategoriesSolved(
    easySolved: Int, easyTotal: Int,
    mediumSolved: Int, mediumTotal: Int,
    hardSolved: Int, hardTotal: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = SpaceBetween1
    ) {
        // Easy Category
        ProblemCategoryBox(
            category = "Easy",
            solved = easySolved,
            total = easyTotal,
            backgroundColor = Color(0xFFE0F7FA)
        )

        // Medium Category
        ProblemCategoryBox(
            category = "Medium",
            solved = mediumSolved,
            total = mediumTotal,
            backgroundColor = Color(0xFFFFF9C4)
        )

        // Hard Category
        ProblemCategoryBox(
            category = "Hard",
            solved = hardSolved,
            total = hardTotal,
            backgroundColor = Color(0xFFFFCDD2)
        )
    }
}

@Composable
fun ProblemCategoryBox(category: String, solved: Int, total: Int, backgroundColor: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),
        modifier = Modifier
            .width(100.dp)
            .height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = category)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$solved/$total")
        }
    }
}

@Composable
fun LatestSubmissionsList(submissions: List<Submission>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Spacing between submissions
    ) {
        items(submissions) { submission ->
            SubmissionItem(submission = submission)
        }
    }
}

@Composable
fun SubmissionItem(submission: Submission) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "Title: ${submission.title}")
            Text(text = "Date: ${submission.date}")
            Text(text = "Language: ${submission.language}")
        }
    }
}

@Preview
@Composable
fun PreviewUserDetails() {
    val submissions = listOf(
        Submission(title = "Two Sum", date = "2023-10-02", language = "Java"),
        Submission(title = "Reverse Linked List", date = "2023-09-30", language = "Kotlin"),
        Submission(title = "Valid Parentheses", date = "2023-09-25", language = "Python"),
        Submission(title = "Binary Search", date = "2023-09-20", language = "C++"),
        Submission(title = "Two Sum", date = "2023-10-02", language = "Java"),
        Submission(title = "Reverse Linked List", date = "2023-09-30", language = "Kotlin"),
        Submission(title = "Valid Parentheses", date = "2023-09-25", language = "Python"),
        Submission(title = "Binary Search", date = "2023-09-20", language = "C++"),
    )
    UserProfileScreen(
        user = LeetCodeUser(
            username = "Leigh Berger",
            avatarUrl = "",
            totalProblemsSolved = 5451,
            globalRanking = 7795,
            contestRating = 2.3,
            badge = "natoque",
            streakDays = 1198
        ),
        submissions = submissions
    )
}

// Sample Data Model
data class LeetCodeUser(
    val username: String,
    val avatarUrl: String,
    val totalProblemsSolved: Int,
    val globalRanking: Int,
    val contestRating: Double,
    val badge: String,
    val streakDays: Int
)

// Sample data model
data class Submission(
    val title: String,
    val date: String,
    val language: String
)
