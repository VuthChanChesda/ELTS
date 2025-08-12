package com.elts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elts.models.Quiz




class QuizAdapter(
    private val quizzes: List<Quiz>,
    private val onItemClick: (Quiz) -> Unit
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    inner class QuizViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quizTitle: TextView = view.findViewById(R.id.quizTitle)
        val quizDifficulty: TextView = view.findViewById(R.id.quizDifficulty)

        init {
            view.setOnClickListener {
                onItemClick(quizzes[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = quizzes[position]
        holder.quizTitle.text = quiz.title
        holder.quizDifficulty.text = quiz.difficulty.capitalize()
    }

    override fun getItemCount(): Int = quizzes.size
}

