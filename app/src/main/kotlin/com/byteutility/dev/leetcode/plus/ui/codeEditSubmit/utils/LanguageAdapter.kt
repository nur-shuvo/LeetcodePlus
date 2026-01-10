package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.CodeSnippet

class LanguageAdapter(private val dataSet: List<CodeSnippet>,private val listener: OnClickListener) :
    RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val data = dataSet[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }


    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvLan = itemView.findViewById<TextView>(R.id.tvLan)

        fun bind(value: CodeSnippet) {
            tvLan.text = value.lang

            tvLan.setOnClickListener {
                listener.onClick(value)
            }
        }
    }

    interface OnClickListener {
        fun onClick(value: CodeSnippet)
    }

}