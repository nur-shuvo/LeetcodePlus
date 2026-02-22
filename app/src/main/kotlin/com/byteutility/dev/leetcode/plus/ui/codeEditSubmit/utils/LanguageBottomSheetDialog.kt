package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.utils

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.adapter.LanguageAdapter
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.CodeSnippet
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LanguageBottomSheetDialog : BottomSheetDialogFragment() {
    private var codeSnippets: List<CodeSnippet> = emptyList()
    private var onLanguageSelected: ((CodeSnippet) -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.language_selection_bottom_sheet,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view: View) {
        val rvLanguages = view.findViewById<RecyclerView>(R.id.rvLanguages)
        rvLanguages.adapter = LanguageAdapter(codeSnippets) { snippet ->
            onLanguageSelected?.invoke(snippet)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let {
                it.setBackgroundResource(R.drawable.bg_bottom_sheet)
            }
        }

        return dialog
    }

    companion object {
        fun newInstance(
            codeSnippets: List<CodeSnippet>,
            onLanguageSelected: (CodeSnippet) -> Unit
        ): LanguageBottomSheetDialog {
            return LanguageBottomSheetDialog().apply {
                this.codeSnippets = codeSnippets
                this.onLanguageSelected = onLanguageSelected
            }
        }
    }
}
