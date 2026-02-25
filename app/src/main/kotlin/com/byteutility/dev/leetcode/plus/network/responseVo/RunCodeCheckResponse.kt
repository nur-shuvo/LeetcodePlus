package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class RunCodeCheckResponse(
    @SerializedName("state") val state: String,
    @SerializedName("status_code") val statusCode: Int? = null,
    @SerializedName("run_success") val runSuccess: Boolean? = null,
    @SerializedName("status_runtime") val statusRuntime: String? = null,
    @SerializedName("status_memory") val statusMemory: String? = null,
    @SerializedName("code_answer") val codeAnswer: List<String>? = null,
    @SerializedName("expected_code_answer") val expectedCodeAnswer: List<String>? = null,
    @SerializedName("std_output_list") val stdOutputList: List<String>? = null,
    @SerializedName("compare_result") val compareResult: String? = null,
    @SerializedName("total_correct") val totalCorrect: Int? = null,
    @SerializedName("total_testcases") val totalTestcases: Int? = null,
    @SerializedName("status_msg") val statusMsg: String? = null,
    @SerializedName("compile_error") val compileError: String? = null,
    @SerializedName("full_compile_error") val fullCompileError: String? = null,
    @SerializedName("runtime_error") val runtimeError: String? = null,
    @SerializedName("full_runtime_error") val fullRuntimeError: String? = null,
    @SerializedName("correct_answer") val correctAnswer: Boolean? = null,
    @SerializedName("pretty_lang") val prettyLang: String? = null,
    @SerializedName("submission_id") val submissionId: String? = null,
)
