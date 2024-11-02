package com.byteutility.dev.leetcode.plus.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.worker.UserDetailsSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserLoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDatastore: UserDatastore,
) : ViewModel() {

    fun saveUserName(userName: String) {
        viewModelScope.launch {
            userDatastore.saveUserBasicInfo(
                userBasicInfo = UserBasicInfo(userName = userName,
                )
            )
        }
    }
}
