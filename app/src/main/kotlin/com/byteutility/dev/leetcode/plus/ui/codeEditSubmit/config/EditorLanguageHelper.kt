package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.config

import android.util.Log
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.widget.CodeEditor

/**
 * Helper class to configure CodeEditor with language-specific settings.
 */
@Suppress("MagicNumber", "TooGenericExceptionCaught")
object EditorLanguageHelper {

    private const val TAG = "EditorLanguageHelper"

    /**
     * Configures the CodeEditor with language-specific settings including
     * syntax highlighting, auto-indentation, and symbol pair completion.
     *
     * @param editor The CodeEditor instance to configure
     * @param languageSlug LeetCode language slug (e.g., "python3", "java")
     * @return true if configuration succeeded, false otherwise
     */
    fun configureEditor(editor: CodeEditor, languageSlug: String): Boolean {
        return try {
            // 1. Get TextMate scope name from language mapper
            val scopeName = LanguageMapper.getTextMateScopeName(languageSlug)
            Log.d(TAG, "Configuring editor for language: $languageSlug -> $scopeName")

            // 2. Set TextMate color scheme
            val colorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
            editor.colorScheme = colorScheme

            // 3. Create TextMate language with auto-completion enabled
            val language = TextMateLanguage.create(scopeName, true)

            // 4. Set the language on editor
            editor.setEditorLanguage(language)

            // 5. Enable auto-indentation
            editor.props.autoIndent = true

            // 6. Enable symbol pair auto-completion (parenthesis, brackets, quotes)
            editor.props.symbolPairAutoCompletion = true

            // 7. Configure tab width based on language
            editor.setTabWidth(getTabWidthForLanguage(languageSlug))

            // 8. Optional: Disable sticky scroll for better performance on code snippets
            editor.props.stickyScroll = false

            Log.d(TAG, "Successfully configured editor for language: $languageSlug")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to configure editor for language: $languageSlug", e)
            false
        }
    }

    /**
     * Returns the appropriate tab width for different languages.
     * Different programming languages have different conventions for indentation.
     */
    private fun getTabWidthForLanguage(languageSlug: String): Int {
        return when (languageSlug.lowercase()) {
            "python", "python3" -> 4
            "java", "kotlin", "c", "cpp", "c++", "csharp", "c#" -> 4
            "javascript", "typescript" -> 2
            "go", "golang" -> 8 // Go traditionally uses tabs
            "ruby", "php" -> 2
            "swift" -> 4
            "rust" -> 4
            else -> 4 // Default to 4 spaces
        }
    }
}
