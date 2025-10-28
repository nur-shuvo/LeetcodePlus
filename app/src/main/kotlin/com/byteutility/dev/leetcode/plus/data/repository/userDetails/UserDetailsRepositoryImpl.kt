package com.byteutility.dev.leetcode.plus.data.repository.userDetails

import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.VideosByPlaylist
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.network.RestApiService
import com.byteutility.dev.leetcode.plus.network.responseVo.LeetcodeUpcomingContestsResponse
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDetailsRepositoryImpl @Inject constructor(
    private val userDatastore: UserDatastore,
    private val restApiService: RestApiService,
) : UserDetailsRepository {

    private val youtubeDataApi =
        YouTube.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), null)
            .setApplicationName("LeetCodePlusApplication")
            .build()

    override suspend fun getUserBasicInfo(): Flow<UserBasicInfo?> {
        return userDatastore.getUserBasicInfo()
    }

    override suspend fun getUserContestInfo(): Flow<UserContestInfo?> {
        return userDatastore.getUserContestInfo()
    }

    override suspend fun getUserProblemSolvedInfo(): Flow<UserProblemSolvedInfo?> {
        return userDatastore.getUserProblemSolvedInfo()
    }

    override suspend fun getUserRecentAcSubmissions(): Flow<List<UserSubmission>?> {
        return userDatastore.getUserAcSubmissions()
    }

    override suspend fun getUserLastSubmissions(): Flow<List<UserSubmission>?> {
        return userDatastore.getUserLastSubmissions()
    }

    override suspend fun getDailyProblem(): Flow<LeetCodeProblem?> {
        return userDatastore.getDailyProblem()
    }

    override suspend fun getUserRecentAcSubmissionsPaginated(
        page: Int,
        pageSize: Int
    ): Result<List<UserSubmission>> {
        delay(1000)
        val startingIndex = page * pageSize
        val submissions = userDatastore.getUserAcSubmissions().first() ?: emptyList()
        return if ((startingIndex + pageSize) <= submissions.size) {
            Result.success(
                userDatastore.getUserAcSubmissions().first()!!
                    .slice(startingIndex until startingIndex + pageSize)
            )
        } else Result.success(
            emptyList()
        )
    }

    override suspend fun getVideosByPlayList(
        nextPageToken: String?,
        playListId: String,
    ): VideosByPlaylist {
        val playListItemListResponse = youtubeDataApi.playlistItems().list(
            mutableListOf(
                YOUTUBE_PLAYLIST_PART
            )
        )
            .setPlaylistId(playListId)
            .setPageToken(nextPageToken)
            .setFields(YOUTUBE_PLAYLIST_FIELDS)
            .setMaxResults(YOUTUBE_PLAYLIST_MAX_RESULTS)
            .setKey("AIzaSyC6jMDR6Afsct6Yr7Oo_A5KrYHawUL7itc")
            .execute()

        val videoIds: MutableList<String?> = mutableListOf()

        // pull out the video id's from the playlist page
        for (item in playListItemListResponse.items) {
            videoIds.add(item.snippet.resourceId.videoId)
        }

        // get details of the videos on this playlist page
        var videoListResponse = youtubeDataApi.videos()
            .list(listOf(YOUTUBE_VIDEOS_PART))
            .setFields(YOUTUBE_VIDEOS_FIELDS)
            .setKey("AIzaSyC6jMDR6Afsct6Yr7Oo_A5KrYHawUL7itc")
            .setId(videoIds)
            .execute()

        return VideosByPlaylist(videoListResponse.items, playListItemListResponse.nextPageToken)
    }

    override suspend fun getLeetcodeUpcomingContests(): LeetcodeUpcomingContestsResponse {
        return restApiService.getLeetcodeUpcomingContests(
            "Baker_vai",
            "90dfbb9b3cd0f74ea4fb530077348d3367eccf70"
        )
    }

    override suspend fun clearAllData() {
        userDatastore.clearAllData()
    }

    companion object {
        const val YOUTUBE_PLAYLIST_MAX_RESULTS: Long = 10L
        const val YOUTUBE_PLAYLIST_PART: String = "snippet"
        const val YOUTUBE_PLAYLIST_FIELDS: String =
            "pageInfo,nextPageToken,items(id,snippet(resourceId/videoId))"
        const val YOUTUBE_VIDEOS_PART: String =
            "snippet,contentDetails,statistics"
        const val YOUTUBE_VIDEOS_FIELDS: String =
            "items(id,snippet(title,description,thumbnails/high),contentDetails/duration,statistics)"

    }
}
