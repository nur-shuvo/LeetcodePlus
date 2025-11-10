package com.byteutility.dev.leetcode.plus.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.network.RestApiService
import com.byteutility.dev.leetcode.plus.network.requestVO.ProblemSubmitRequest
import dagger.hilt.android.AndroidEntryPoint
import io.github.rosemoe.sora.widget.CodeEditor
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class CodeEditorSubmitActivity : AppCompatActivity() {

    private val titleSlug by lazy { intent.getStringExtra(EXTRA_TITLE_SLUG) }
    private val language by lazy { intent.getStringExtra(EXTRA_LANGUAGE) }
    private val initialCode by lazy { intent.getStringExtra(EXTRA_INITIAL_CODE) }
    private val questionId by lazy { intent.getStringExtra(EXTRA_QUESTION_ID) }

    private val codeEditor by lazy { findViewById<CodeEditor>(R.id.codeEditor) }
    private val submitButton by lazy { findViewById<TextView>(R.id.submit) }
    private val languageButton by lazy { findViewById<TextView>(R.id.language) }

    @Inject
    lateinit var userDatastore: UserDatastore

    @Inject
    lateinit var restApiService: RestApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_editor_submit)
        codeEditor.setText(initialCode)
        languageButton.text = "Lang: $language"

        submitButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val result = restApiService.submitLeetcodeProblem(
                        titleSlug!!,
                        userDatastore.getLeetcodeCsrfToken()!!,
                        "LEETCODE_SESSION=${userDatastore.getLeetcodeSessionToken()!!}; csrftoken=${userDatastore.getLeetcodeCsrfToken()!!}",
                        ProblemSubmitRequest(
                            language!!,
                            codeEditor.text.toString(),
                            questionId!!
                        )
                    )
                    periodicPollForSubmissionResult(result.submissionId)
                } catch (e: Exception) {
                    Log.d("SHUVO-${this::class.java.simpleName}", "onCreate: $e")
                    Toast.makeText(this@CodeEditorSubmitActivity, "$e", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun periodicPollForSubmissionResult(submissionId: Long) {
        lifecycleScope.launch {
            while (isActive) {
                delay(1.seconds)
                try {
                    val result = restApiService.getSubmissionResult(
                        submissionId,
                        userDatastore.getLeetcodeCsrfToken()!!,
                        "LEETCODE_SESSION=${userDatastore.getLeetcodeSessionToken()!!}; csrftoken=${userDatastore.getLeetcodeCsrfToken()!!}",
                    )
                    if (result.state == "SUCCESS") {
                        AlertDialog.Builder(this@CodeEditorSubmitActivity)
                            .setTitle("Submission Result")
                            .setMessage(result.statusMessage + "\n" + "compile error: ${result.compileError ?: ""}")
                            .setPositiveButton("OK") { _, _ ->
                                finish()
                            }
                            .show()
                        break
                    }
                } catch (e: Exception) {
                    Log.d(
                        "SHUVO-${this::class.java.simpleName}",
                        "periodicPollForSubmissionResult: $e"
                    )
                    Toast.makeText(this@CodeEditorSubmitActivity, "$e", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        const val EXTRA_TITLE_SLUG = "titleSlug"
        const val EXTRA_LANGUAGE = "language"
        const val EXTRA_INITIAL_CODE = "initialCode"
        const val EXTRA_QUESTION_ID = "questionId"

        fun getIntent(
            context: Context,
            titleSlug: String,
            questionId: String,
            language: String? = null,
            initialCode: String? = null
        ): Intent {
            return Intent(context, CodeEditorSubmitActivity::class.java).apply {
                putExtra(EXTRA_TITLE_SLUG, titleSlug)
                putExtra(EXTRA_QUESTION_ID, questionId)
                putExtra(EXTRA_LANGUAGE, language)
                putExtra(EXTRA_INITIAL_CODE, initialCode)
            }
        }
    }
}