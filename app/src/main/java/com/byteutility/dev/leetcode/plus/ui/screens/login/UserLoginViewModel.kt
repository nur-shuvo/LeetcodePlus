package com.byteutility.dev.leetcode.plus.ui.screens.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.worker.UserDetailsSyncWorker
import com.byteutility.dev.leetcode.plus.network.RestApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserLoginViewModel @Inject constructor(
    private val userDatastore: UserDatastore,
    private val restApiService: RestApiService,
) : ViewModel() {

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val userName: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun saveUserName(userName: String, context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                // TODO: need parse message if user name does not exist
                restApiService.getUserProfile(userName)
                userDatastore.saveUserBasicInfo(
                    userBasicInfo = UserBasicInfo(userName = userName)
                )
                _loginState.value = LoginState.Success(userName)
                UserDetailsSyncWorker.enqueuePeriodicWork(context)
            } catch (_: Exception) {
                _loginState.value = LoginState.Error("Failed to retrieve user profile.")
            }
        }
    }
}
