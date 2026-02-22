package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.CodeSnippet

class LanguageAdapter(
    private val languages: List<CodeSnippet>,
    private val onItemClick: (CodeSnippet) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(languages[position], position == languages.size - 1)
    }

    override fun getItemCount(): Int = languages.size

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvLanguageName: TextView = itemView.findViewById(R.id.tvLanguageName)
        private val itemContainer: View = itemView.findViewById(R.id.itemContainer)
        private val divider: View = itemView.findViewById(R.id.divider)

        fun bind(snippet: CodeSnippet, isLastItem: Boolean) {
            tvLanguageName.text = snippet.lang
            itemContainer.setOnClickListener {
                onItemClick(snippet)
            }
            divider.visibility = if (isLastItem) View.GONE else View.VISIBLE
        }
    }
}
