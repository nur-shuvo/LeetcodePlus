package com.byteutility.dev.leetcode.plus.ui.login

import androidx.lifecycle.ViewModel
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class UserLoginViewModel @Inject constructor(
    private val userDatastore: UserDatastore,
) : ViewModel() {

    suspend fun saveUserName(userName: String) {
        userDatastore.saveUserBasicInfo(
            userBasicInfo = UserBasicInfo(
                userName = userName,
            )
        )
    }
}
