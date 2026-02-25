package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.network.responseVo.RunCodeCheckResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class RunCodeResultBottomSheet : BottomSheetDialogFragment() {

    private lateinit var response: RunCodeCheckResponse
    private lateinit var dataInput: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gson = Gson()
        val responseJson = requireArguments().getString(ARG_RESPONSE)!!
        val dataInputArg = requireArguments().getString(ARG_DATA_INPUT)!!
        response = gson.fromJson(responseJson, RunCodeCheckResponse::class.java)
        dataInput = dataInputArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_run_result, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView(view)
    }

    private fun bindView(view: View) {
        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
        val layoutCompileError = view.findViewById<View>(R.id.layoutCompileError)
        val tvCompileError = view.findViewById<TextView>(R.id.tvCompileError)
        val layoutRuntimeError = view.findViewById<View>(R.id.layoutRuntimeError)
        val tvRuntimeError = view.findViewById<TextView>(R.id.tvRuntimeError)
        val tvTle = view.findViewById<TextView>(R.id.tvTle)
        val layoutTestCases = view.findViewById<LinearLayout>(R.id.layoutTestCases)
        val layoutSummary = view.findViewById<View>(R.id.layoutSummary)
        val tvSummary = view.findViewById<TextView>(R.id.tvSummary)
        val tvRuntimeMemory = view.findViewById<TextView>(R.id.tvRuntimeMemory)

        // Status chip
        var statusText = response.statusMsg ?: "Unknown"
        val chipRes = when (response.correctAnswer) {
            true -> {
                statusText = "ACCEPTED"
                R.drawable.bg_status_chip_green
            }
            false -> {
                statusText = "WRONG ANSWER"
                R.drawable.bg_status_chip_red
            }
            null -> R.drawable.bg_status_chip_neutral
        }
        tvStatus.text = statusText
        tvStatus.setBackgroundResource(chipRes)

        // Compile error
        val compileError = response.fullCompileError?.takeIf { it.isNotBlank() }
            ?: response.compileError?.takeIf { it.isNotBlank() }
        if (compileError != null) {
            layoutCompileError.isVisible = true
            tvCompileError.text = compileError
        }

        // Runtime error
        val runtimeError = response.fullRuntimeError?.takeIf { it.isNotBlank() }
            ?: response.runtimeError?.takeIf { it.isNotBlank() }
        if (runtimeError != null) {
            layoutRuntimeError.isVisible = true
            tvRuntimeError.text = runtimeError
        }

        // TLE
        tvTle.isVisible = response.statusCode == 14

        // Per-test-case results
        val compareResult = response.compareResult
        if (!compareResult.isNullOrEmpty() && response.runSuccess == true) {
            val inputLines = dataInput.trim().split("\n")
            compareResult.forEachIndexed { index, resultChar ->
                val passed = resultChar == '1'
                val itemView = layoutInflater.inflate(R.layout.item_test_case_result, layoutTestCases, false)

                val tvCaseHeader = itemView.findViewById<TextView>(R.id.tvCaseHeader)
                val tvInput = itemView.findViewById<TextView>(R.id.tvInput)
                val tvOutput = itemView.findViewById<TextView>(R.id.tvOutput)
                val tvExpected = itemView.findViewById<TextView>(R.id.tvExpected)

                tvCaseHeader.text = "Case ${index + 1}"
                tvCaseHeader.setTextColor(
                    if (passed) 0xFF00AF9B.toInt() else 0xFFFF2D55.toInt()
                )

                val inputForCase = inputLines.getOrNull(index) ?: ""
                tvInput.text = "Input: $inputForCase"

                val output = response.codeAnswer?.getOrNull(index)
                tvOutput.text = "Output: ${output ?: "N/A"}"

                val expected = response.expectedCodeAnswer?.getOrNull(index)
                tvExpected.text = "Expected: ${expected ?: "N/A"}"

                layoutTestCases.addView(itemView)
            }
        }

        // Summary
        if (response.runSuccess == true) {
            layoutSummary.isVisible = true
            val correct = response.totalCorrect ?: 0
            val total = response.totalTestcases ?: 0
            tvSummary.text = "$correct / $total test cases passed"
            val runtime = response.statusRuntime
            val memory = response.statusMemory
            if (runtime != null || memory != null) {
                tvRuntimeMemory.text = buildString {
                    if (runtime != null) append("Runtime: $runtime")
                    if (runtime != null && memory != null) append("  |  ")
                    if (memory != null) append("Memory: $memory")
                }
            }
        }
    }

    companion object {
        private const val ARG_RESPONSE = "response"
        private const val ARG_DATA_INPUT = "data_input"

        fun newInstance(response: RunCodeCheckResponse, dataInput: String): RunCodeResultBottomSheet {
            return RunCodeResultBottomSheet().apply {
                arguments = bundleOf(
                    ARG_RESPONSE to Gson().toJson(response),
                    ARG_DATA_INPUT to dataInput
                )
            }
        }
    }
}
