package com.byteutility.dev.leetcode.plus.ui.userdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.data.repository.UserDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject



data class UserDetailsUiState(
    val userBasicInfo: UserBasicInfo = UserBasicInfo(),
    val userContestInfo: UserContestInfo = UserContestInfo(),
    val userProblemSolvedInfo: UserProblemSolvedInfo = UserProblemSolvedInfo(),
    val userSubmissions: List<UserSubmission> = emptyList()
)

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val userDetailsRepository: UserDetailsRepository,
    private val userDatastore: UserDatastore,
) : ViewModel() {

    private val userBasicInfo =
        MutableStateFlow<UserBasicInfo>(
            UserBasicInfo(
                name = "Bert Boyer",
                userName = "Clara Trevino",
                avatar = "leo",
                ranking = 5469,
                country = "Belarus"
            )
        )

    private val userContestInfo =
        MutableStateFlow<UserContestInfo>(
            value = UserContestInfo(
                rating = 6.7,
                globalRanking = 5624,
                attend = 4251
            )
        )

    private val userProblemSolvedInfo =
        MutableStateFlow<UserProblemSolvedInfo>(
            value = UserProblemSolvedInfo(
                easy = 3667,
                medium = 3987,
                hard = 8330
            )
        )

    private val userSubmissions =
        MutableStateFlow<List<UserSubmission>>(value = listOf())


    val uiState: StateFlow<UserDetailsUiState> =
        combine(
            userBasicInfo,
            userContestInfo,
            userProblemSolvedInfo,
            userSubmissions
        ) { userBasicInfo, userContestInfo, userProblemSolvedInfo, userSubmissions ->
            UserDetailsUiState(
                userBasicInfo = userBasicInfo,
                userContestInfo = userContestInfo,
                userProblemSolvedInfo = userProblemSolvedInfo,
                userSubmissions = userSubmissions
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserDetailsUiState()
        )

    init {

        viewModelScope.launch {
            userDetailsRepository
                .getUserBasicInfo()
                .collect {
                }
        }

        viewModelScope.launch {
            userDetailsRepository
                .getUserContestInfo()
                .collect {
                }
        }

        viewModelScope.launch {
            userDetailsRepository
                .getUserProblemSolvedInfo()
                .collect {
                }
        }

        viewModelScope.launch {
            userDetailsRepository
                .getUserRecentSubmissions()
                .collect {
                }
        }
    }
}