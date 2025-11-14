package com.byteutility.dev.leetcode.plus.ui.screens.leetcode_login

import androidx.lifecycle.ViewModel
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import dagger.hilt.android.lifecycle.HiltViewModel

/**
 * Created by Shuvo on 11/09/2025.
 */
@HiltViewModel
class LeetcodeLoginViewModel @javax.inject.Inject constructor(
    private val userDatastore: UserDatastore
) : ViewModel() {
    suspend fun saveCookies(csrf: String, session: String) {
        userDatastore.saveLeetcodeCsrfToken(csrf)
        userDatastore.saveLeetcodeSessionToken(session)
    }
}