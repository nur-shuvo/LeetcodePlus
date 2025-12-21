package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.config

/**
 * Maps LeetCode language slugs to TextMate scope names for syntax highlighting.
 */
object LanguageMapper {
    private val languageMap = mapOf(
        // Python
        "python" to "source.python",
        "python3" to "source.python",

        // Java
        "java" to "source.java",

        // JavaScript/TypeScript
        "javascript" to "source.js",
        "typescript" to "source.ts",

        // C/C++
        "c" to "source.c",
        "cpp" to "source.cpp",
        "c++" to "source.cpp",

        // Kotlin
        "kotlin" to "source.kotlin",

        // C#
        "csharp" to "source.cs",
        "c#" to "source.cs",

        // Go
        "go" to "source.go",
        "golang" to "source.go",

        // Rust
        "rust" to "source.rust",

        // Swift
        "swift" to "source.swift",

        // Ruby
        "ruby" to "source.ruby",

        // PHP
        "php" to "source.php",

        // SQL
        "sql" to "source.sql",
        "mysql" to "source.sql",
        "mssql" to "source.sql",
        "oraclesql" to "source.sql",
        "postgresql" to "source.sql",

        // Shell
        "bash" to "source.shell",
        "shell" to "source.shell",

        // Other
        "scala" to "source.scala",
        "dart" to "source.dart",
        "elixir" to "source.elixir",
        "erlang" to "source.erlang",
        "racket" to "source.racket"
    )

    /**
     * Returns the TextMate scope name for the given LeetCode language slug.
     * @param leetcodeSlug The LeetCode language slug (e.g., "python3", "java")
     * @return The TextMate scope name (e.g., "source.python", "source.java")
     */
    fun getTextMateScopeName(leetcodeSlug: String): String {
        return languageMap[leetcodeSlug.lowercase()] ?: "source.java" // Default to Java
    }

    /**
     * Checks if the given language slug is supported.
     * @param leetcodeSlug The LeetCode language slug
     * @return true if supported, false otherwise
     */
    fun isLanguageSupported(leetcodeSlug: String): Boolean {
        return languageMap.containsKey(leetcodeSlug.lowercase())
    }
}
