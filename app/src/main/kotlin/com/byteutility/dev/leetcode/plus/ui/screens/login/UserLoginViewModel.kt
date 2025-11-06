package com.byteutility.dev.leetcode.plus.ui.screens.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.worker.UserDetailsSyncWorker
import com.byteutility.dev.leetcode.plus.network.RestApiService
import com.byteutility.dev.leetcode.plus.ui.screens.login.model.LoginState
import com.byteutility.dev.leetcode.plus.utils.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserLoginViewModel @Inject constructor(
    private val userDatastore: UserDatastore,
    private val restApiService: RestApiService,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    fun saveUserName(userName: String, context: Context) {
        if (userName.isEmpty()) {
            Toast.makeText(context, "Username cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            if (networkMonitor.isOnline.firstOrNull() != true) {
                Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                return@launch
            }
            _loginState.value = LoginState.Loading
            try {
                // TODO: need parse message if user name does not exist
                restApiService.getUserProfile(userName)
                userDatastore.saveUserBasicInfo(
                    userBasicInfo = UserBasicInfo(userName = userName)
                )
                _loginState.value = LoginState.Success(userName)
                UserDetailsSyncWorker.enqueuePeriodicWork(context)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Failed to retrieve user profile.")
                Toast.makeText(
                    context,
                    "Error: ${e.message}, try again after 15 mints",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
