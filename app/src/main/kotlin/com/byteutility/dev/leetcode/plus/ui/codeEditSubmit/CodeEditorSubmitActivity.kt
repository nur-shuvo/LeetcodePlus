package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.ui.MainActivity
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.config.EditorLanguageHelper
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.utils.LanguageBottomSheetDialog
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.viewmodel.CodeEditorSubmitUIEvent
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.viewmodel.CodeEditorSubmitViewModel
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.viewmodel.RunCodeState
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.viewmodel.SubmissionState
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.CodeSnippet
import com.byteutility.dev.leetcode.plus.utils.getJsonExtra
import com.byteutility.dev.leetcode.plus.utils.putExtraJson
import com.byteutility.dev.leetcode.plus.utils.toTitleCase
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import io.github.rosemoe.sora.widget.CodeEditor
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CodeEditorSubmitActivity : AppCompatActivity() {

    private val titleSlug by lazy { intent.getStringExtra(EXTRA_TITLE_SLUG) }
    private val language by lazy { intent.getStringExtra(EXTRA_LANGUAGE_SLUG) }
    private val selectedLanguage by lazy { intent.getStringExtra(SELECTED_LANGUAGE) }
    private val initialCode by lazy { intent.getStringExtra(EXTRA_INITIAL_CODE) }
    private val questionId by lazy { intent.getStringExtra(EXTRA_QUESTION_ID) }
    private val testCasesExtra by lazy { intent.getStringExtra(EXTRA_TEST_CASES) }
    private var snippets: List<CodeSnippet>? = null
    private val codeEditor by lazy { findViewById<CodeEditor>(R.id.codeEditor) }

    private val accessoryBar by lazy { findViewById<HorizontalScrollView>(R.id.keyboardAccessoryBar) }
    private val submitButton by lazy { findViewById<TextView>(R.id.submit) }
    private val runButton by lazy { findViewById<Button>(R.id.btnRun) }
    private val languageButton by lazy { findViewById<TextView>(R.id.language) }
    private val resetBtn by lazy { findViewById<ImageView>(R.id.ivReset) }

    private var runProgressDialog: AlertDialog? = null

    private val viewModel: CodeEditorSubmitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_editor_submit)
        getBundle()
        initView()
        initListener()
        collectSubmissionResult()
        collectRunCodeResult()
        collectUiEvent()
    }

    private fun getBundle() {
        snippets = intent.getJsonExtra<List<CodeSnippet>>(EXTRA_ALL_LANGUAGES)
    }

    private fun initView() {
        viewModel.setCodeSnippet(
            CodeSnippet(
                lang = language!!,
                langSlug = language!!,
                code = initialCode!!
            )
        )
        configureEditorLanguage(language)
        setLanguage(selectedLanguage)
        setCode(language, initialCode)
        setupKeyboardBar()
    }

    private fun setLanguage(lan: String?) {
        languageButton.text =
            HtmlCompat.fromHtml("${lan?.toTitleCase()}", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setCode(language: String?, initialCode: String?) {
        lifecycleScope.launch {
            val savedCode = viewModel.getSavedCode(questionId!!, language!!)
            val code = savedCode ?: initialCode
            codeEditor.setText(code)
        }
    }

    private fun setupKeyboardBar() {
        val keysContainer = findViewById<LinearLayout>(R.id.keysContainer)
        val symbols = listOf("{", "}", "(", ")", "[", "]", ";", ",", ".", "<", ">", "/", "\"", ":")
        val outValue = TypedValue()
        theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        val rippleRes = outValue.resourceId
        symbols.forEach { symbol ->
            val textView = TextView(this).apply {
                text = symbol
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                gravity = Gravity.CENTER
                setPadding(40, 20, 40, 20)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                setTextColor(Color.DKGRAY)
                setBackgroundResource(rippleRes)
                isClickable = true
                isFocusable = true

                setOnClickListener {
                    codeEditor.insertText(symbol, 1)
                }
            }
            keysContainer.addView(textView)
        }

        codeEditor.setOnFocusChangeListener { _, hasFocus ->
            accessoryBar.visibility = if (hasFocus) View.VISIBLE else View.GONE
        }
    }

    private fun initListener() {
        submitButton.setOnClickListener {
            showSubmitConfirmation()
        }

        runButton.setOnClickListener {
            viewModel.runCode(
                titleSlug!!,
                language!!,
                codeEditor.text.toString(),
                questionId!!,
                testCasesExtra ?: ""
            )
        }

        resetBtn.setOnClickListener {
            viewModel.resetCode()
        }

        languageButton.setOnClickListener {
            snippets?.let { snippets ->
                LanguageBottomSheetDialog.newInstance(snippets) { selectedSnippet ->
                    configureEditorLanguage(selectedSnippet.langSlug)
                    setLanguage(selectedSnippet.lang)
                    setCode(selectedSnippet.langSlug, selectedSnippet.code)
                    viewModel.setCodeSnippet(selectedSnippet)
                }.show(supportFragmentManager, "LanguageBottomSheet")
            }
        }
    }

    private fun showSubmitConfirmation() {
        val sheet = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_submit_confirm, null)
        sheet.setContentView(view)
        view.findViewById<Button>(R.id.btnCancel).setOnClickListener { sheet.dismiss() }
        view.findViewById<Button>(R.id.btnConfirmSubmit).setOnClickListener {
            sheet.dismiss()
            viewModel.submit(titleSlug!!, language!!, codeEditor.text.toString(), questionId!!)
        }
        sheet.show()
    }

    private fun collectUiEvent() {
        lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is CodeEditorSubmitUIEvent.NavigateToLeetcodeLogin -> {
                        Builder(this@CodeEditorSubmitActivity)
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

                    is CodeEditorSubmitUIEvent.ResetCode -> {
                        codeEditor.setText(event.codeSnippet?.code)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun collectSubmissionResult() {
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

                        Builder(this@CodeEditorSubmitActivity)
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
    }

    private fun buildRunProgressDialog(): AlertDialog {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_run_progress, null)
        return Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
    }

    private fun collectRunCodeResult() {
        lifecycleScope.launch {
            viewModel.runCodeState.collect { state ->
                when (state) {
                    is RunCodeState.Running -> {
                        runButton.isEnabled = false
                        runProgressDialog = buildRunProgressDialog()
                        runProgressDialog?.show()
                    }

                    is RunCodeState.Success -> {
                        runProgressDialog?.dismiss()
                        runProgressDialog = null
                        runButton.isEnabled = true
                        RunCodeResultBottomSheet.newInstance(state.response, state.dataInput)
                            .show(supportFragmentManager, "RunCodeResult")
                    }

                    is RunCodeState.Error -> {
                        runProgressDialog?.dismiss()
                        runProgressDialog = null
                        runButton.isEnabled = true
                        Toast.makeText(
                            this@CodeEditorSubmitActivity,
                            "${state.exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is RunCodeState.Idle -> {
                        runButton.isEnabled = true
                    }
                }
            }
        }
    }

    private fun configureEditorLanguage(lan: String?) {
        lan?.let { lang ->
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
        viewModel.saveCode(
            questionId!!,
            viewModel.codeSnippet?.langSlug!!,
            codeEditor.text.toString()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        runProgressDialog?.dismiss()
        runProgressDialog = null
        codeEditor.release()
    }

    companion object {
        const val EXTRA_TITLE_SLUG = "titleSlug"
        const val EXTRA_LANGUAGE_SLUG = "languageSlug"
        const val EXTRA_INITIAL_CODE = "initialCode"
        const val EXTRA_QUESTION_ID = "questionId"
        const val EXTRA_ALL_LANGUAGES = "EXTRA_ALL_LANGUAGES"
        const val SELECTED_LANGUAGE = "SELECTED_LANGUAGE"
        const val EXTRA_TEST_CASES = "EXTRA_TEST_CASES"

        fun getIntent(
            context: Context,
            titleSlug: String,
            questionId: String,
            langSlug: String? = null,
            initialCode: String? = null,
            selectedLanguage: String?,
            allLanguages: List<CodeSnippet>,
            testCases: String? = null
        ): Intent {
            return Intent(context, CodeEditorSubmitActivity::class.java).apply {
                putExtra(EXTRA_TITLE_SLUG, titleSlug)
                putExtra(EXTRA_QUESTION_ID, questionId)
                putExtra(EXTRA_LANGUAGE_SLUG, langSlug)
                putExtra(EXTRA_INITIAL_CODE, initialCode)
                putExtra(SELECTED_LANGUAGE, selectedLanguage)
                putExtraJson(EXTRA_ALL_LANGUAGES, allLanguages)
                putExtra(EXTRA_TEST_CASES, testCases)
            }
        }
    }
}
