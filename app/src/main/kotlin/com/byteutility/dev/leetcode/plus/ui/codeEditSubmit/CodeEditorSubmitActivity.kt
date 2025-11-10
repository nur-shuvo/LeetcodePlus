package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.byteutility.dev.leetcode.plus.R
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

    private var progressDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_editor_submit)
        codeEditor.setText(initialCode)
        languageButton.text = "Lang: $language"

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
                        progressDialog = AlertDialog.Builder(this@CodeEditorSubmitActivity)
                            .setMessage("Submitting...")
                            .setCancelable(false)
                            .show()
                    }

                    is SubmissionState.Success -> {
                        progressDialog?.dismiss()
                        var message = state.response.statusMessage
                        if (state.response.compileError != null) {
                            message += "\nCompile error: ${state.response.compileError}"
                        }
                        AlertDialog.Builder(this@CodeEditorSubmitActivity)
                            .setTitle("Submission Result")
                            .setMessage(message)
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }

                    is SubmissionState.Error -> {
                        progressDialog?.dismiss()
                        Toast.makeText(
                            this@CodeEditorSubmitActivity,
                            "${state.exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is SubmissionState.Idle -> {
                        progressDialog?.dismiss()
                    }
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