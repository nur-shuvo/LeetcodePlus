package com.byteutility.dev.leetcode.plus.ui.screens.login.model


/**
 * Created by Shuvo on 11/06/2025.
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val userName: String) : LoginState()
    data class Error(val message: String) : LoginState()
}