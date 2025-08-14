package com.elts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.elts.databinding.ItemQuizResultBinding
import com.elts.models.UserQuizResult
import java.text.SimpleDateFormat
import java.util.Locale

class QuizResultAdapter(private val list: List<UserQuizResult>) :
    RecyclerView.Adapter<QuizResultAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemQuizResultBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuizResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.quizTitle.text = item.title
        holder.binding.quizDifficulty.text = "Type: ${item.difficulty.capitalize()}"
        holder.binding.quizScore.text = "Score: ${item.score}"
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        holder.binding.quizDate.text = item.takenAt?.let { sdf.format(it) } ?: "-"
    }
}