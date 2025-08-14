package com.elts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.elts.databinding.FragmentResultsBinding
import com.elts.models.UserQuizResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val quizResults = mutableListOf<UserQuizResult>()
    private lateinit var adapter: QuizResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ✅ Use ViewBinding inflate
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (currentUser != null) {
            loadUsername()
        } else {
            binding.usernameText.text = "No user logged in"
        }
        // RecyclerView setup
        adapter = QuizResultAdapter(quizResults)
        binding.resultsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.resultsRecycler.adapter = adapter

        fetchUserQuizHistory()
    }

    private fun loadUsername() {
        val userRef = db.collection("users").document(currentUser!!.uid)
        userRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val username = doc.getString("username") ?: currentUser.email ?: "User"
                binding.usernameText.text = username
            } else {
                binding.usernameText.text = currentUser.email ?: "User"
            }
        }.addOnFailureListener {
            binding.usernameText.text = currentUser.email ?: "User"
        }
    }


    private fun fetchUserQuizHistory() {
        val userRef = db.collection("users").document(currentUser!!.uid)
        userRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val history = doc.get("quizHistory") as? List<Map<String, Any>> ?: emptyList()
                for (entry in history) {
                    val quizId = entry["quizId"] as? String ?: continue
                    val score = (entry["score"] as? Number)?.toInt() ?: 0
                    val takenAt = entry["takenAt"] as? com.google.firebase.Timestamp

                    // Fetch quiz info for title and difficulty
                    db.collection("quizzes").document(quizId).get()
                        .addOnSuccessListener { quizDoc ->
                            if (quizDoc.exists()) {
                                val title = quizDoc.getString("title") ?: "Unknown Quiz"
                                val difficulty = quizDoc.getString("difficulty") ?: "Normal"

                                val result = UserQuizResult(title, difficulty, score, takenAt?.toDate())
                                quizResults.add(result)
                                adapter.notifyDataSetChanged()
                            }
                        }.addOnFailureListener {
                            Log.e("ResultFragment", "Failed to get quiz info for $quizId", it)
                        }
                }
            }
        }.addOnFailureListener {
            Log.e("ResultFragment", "Failed to get user quiz history", it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
