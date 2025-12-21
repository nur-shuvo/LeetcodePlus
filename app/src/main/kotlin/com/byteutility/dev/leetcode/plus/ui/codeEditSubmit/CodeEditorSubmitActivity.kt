package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.ui.MainActivity
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.config.EditorLanguageHelper
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.viewmodel.CodeEditorSubmitUIEvent
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.viewmodel.CodeEditorSubmitViewModel
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.viewmodel.SubmissionState
import dagger.hilt.android.AndroidEntryPoint
import io.github.rosemoe.sora.widget.CodeEditor
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CodeEditorSubmitActivity : AppCompatActivity() {

    private val titleSlug by lazy { intent.getStringExtra(EXTRA_TITLE_SLUG) }
    private val language by lazy { intent.getStringExtra(EXTRA_LANGUAGE) }
    private val initialCode by lazy { intent.getStringExtra(EXTRA_INITIAL_CODE) }
    private val questionId by lazy { intent.getStringExtra(EXTRA_QUESTION_ID) }

    private val codeEditor by lazy { findViewById<CodeEditor>(R.id.codeEditor) }
    private val submitButton by lazy { findViewById<TextView>(R.id.submit) }
    private val languageButton by lazy { findViewById<TextView>(R.id.language) }

    private val viewModel: CodeEditorSubmitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_editor_submit)

        // Configure editor language before setting text
        configureEditorLanguage()

        lifecycleScope.launch {
            val savedCode = viewModel.getSavedCode(questionId!!, language!!)
            codeEditor.setText(savedCode ?: initialCode)
        }
        languageButton.text =
            HtmlCompat.fromHtml("<b>Lang:</b> $language", HtmlCompat.FROM_HTML_MODE_LEGACY)

        submitButton.setOnClickListener {
            viewModel.submit(
                titleSlug!!,
                language!!,
                codeEditor.text.toString(),
                questionId!!
            )
        }

        lifecycleScope.launch {
            viewModel.submissionState.collect { state ->
                when (state) {
                    is SubmissionState.Submitting -> {
                        Toast.makeText(
                            this@CodeEditorSubmitActivity,
                            "Submitting...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is SubmissionState.Success -> {
                        var message = state.response.statusMessage
                        if (state.response.compileError?.isNotEmpty() == true) {
                            message += "\nCompile error: ${state.response.compileError}"
                        }

                        AlertDialog.Builder(this@CodeEditorSubmitActivity)
                            .setTitle("Submission Result")
                            .setMessage(message)
                            .setPositiveButton("OK") { _, _ ->
                                finish()
                            }
                            .show()
                    }

                    is SubmissionState.Error -> {
                        Toast.makeText(
                            this@CodeEditorSubmitActivity,
                            "${state.exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is SubmissionState.Idle -> {
                        // Do nothing
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is CodeEditorSubmitUIEvent.NavigateToLeetcodeLogin -> {
                        AlertDialog.Builder(this@CodeEditorSubmitActivity)
                            .setTitle("Login Required")
                            .setMessage("Please login to LeetCode to submit your solution.")
                            .setPositiveButton("Login") { dialog, _ ->
                                dialog.dismiss()
                                startActivity(
                                    Intent(
                                        this@CodeEditorSubmitActivity,
                                        MainActivity::class.java
                                    ).apply {
                                        putExtra("startDestination", "leetcode_login_webview")
                                    }
                                )
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun configureEditorLanguage() {
        language?.let { lang ->
            val success = EditorLanguageHelper.configureEditor(codeEditor, lang)
            if (!success) {
                Log.i("CodeEditorSubmit", "Failed to configure editor for language: $lang")
            }
        } ?: run {
            Log.i("CodeEditorSubmit", "No language specified, using default editor configuration")
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveCode(questionId!!, language!!, codeEditor.text.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        codeEditor.release()
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