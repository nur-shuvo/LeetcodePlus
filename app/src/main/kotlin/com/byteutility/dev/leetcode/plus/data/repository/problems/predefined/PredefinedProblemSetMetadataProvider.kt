package com.byteutility.dev.leetcode.plus.data.repository.problems.predefined

import android.content.res.AssetManager
import android.util.Log
import com.byteutility.dev.leetcode.plus.domain.model.SetMetadata
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import javax.inject.Inject

class PredefinedProblemSetMetadataProvider @Inject constructor(
    private val assetManager: AssetManager,
    private val gson: Gson
) {

    fun getAvailableStaticSets(): List<SetMetadata> {
        return try {
            assetManager.open("predefined_problem_set_manifest.json").bufferedReader()
                .use { reader ->
                    val type = object : TypeToken<List<SetMetadata>>() {}.type
                    gson.fromJson(reader, type)
                }
        } catch (e: IOException) {
            Log.e(this.javaClass.name, "getAvailableStaticSets: exception ${e.message}")
            emptyList()
        } finally {
            emptyList<SetMetadata>()
        }
    }
}
