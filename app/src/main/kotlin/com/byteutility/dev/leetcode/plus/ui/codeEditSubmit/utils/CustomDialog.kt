package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.adapter.LanguageAdapter
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.CodeSnippet

class CustomDialog(val context: Context) {

    private val dialog: Dialog = Dialog(context)

    fun showLanSelectionDialog(
        dataSet: List<CodeSnippet>,
        action:DialogAction<CodeSnippet>
    ){
        val view = LayoutInflater.from(context).inflate(R.layout.lan_selection_dialog,null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.show()

        val rvLan = view.findViewById<RecyclerView>(R.id.rvLan)
        val btnDismiss = view.findViewById<Button>(R.id.btnDismiss)

        rvLan.apply {
            setHasFixedSize(true)
            adapter = LanguageAdapter(
                dataSet = dataSet,
                listener = object : LanguageAdapter.OnClickListener {
                    override fun onClick(value: CodeSnippet) {
                        action.positive(value)
                        dialog.dismiss()
                    }
                },
            )
        }

        btnDismiss.setOnClickListener {
            dialog.dismiss()
        }
    }

    interface DialogAction<T>{
        fun positive(value:T)
    }
}