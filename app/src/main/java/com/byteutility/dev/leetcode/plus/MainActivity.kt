package com.byteutility.dev.leetcode.plus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.ui.LeetCodePlusNavGraph
import com.byteutility.dev.leetcode.plus.ui.LeetCodePlusNavigationDestinations
import com.byteutility.dev.leetcode.plus.ui.theme.LeetcodePlusTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userDatastore: UserDatastore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LeetcodePlusTheme {
                val navController = rememberNavController()

                val startDestination =
                    remember { mutableStateOf(LeetCodePlusNavigationDestinations.LOGIN_ROUTE) }

                LaunchedEffect(Unit) {
                    startDestination.value =
                        if (userDatastore.getUserBasicInfo()
                                .first()?.userName?.isNotEmpty() == true
                        ) {
                            LeetCodePlusNavigationDestinations.USER_PROFILE_ROUTE
                        } else {
                            LeetCodePlusNavigationDestinations.LOGIN_ROUTE
                        }
                }

                LeetCodePlusNavGraph(navController, startDestination.value)
            }
        }
    }
}
