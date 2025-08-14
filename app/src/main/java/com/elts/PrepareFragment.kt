package com.elts

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elts.models.Quiz
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PrepareFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrepareFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerQuiz: RecyclerView
    private lateinit var quizAdapter: QuizAdapter
    private val quizList = mutableListOf<Quiz>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_prepare, container, false)
        recyclerQuiz = view.findViewById(R.id.recyclerQuiz)

        recyclerQuiz.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        quizAdapter = QuizAdapter(quizList) { quiz ->
            val action = PrepareFragmentDirections
                .actionMenuPrepareToQuizQuestionFragment(quiz.id)
            findNavController().navigate(action)

        }
        recyclerQuiz.adapter = quizAdapter

        loadQuizzes()
        return view
    }


    private fun loadQuizzes() {
        val db = FirebaseFirestore.getInstance()
        db.collection("quizzes")
            .get()
            .addOnSuccessListener { snapshot ->
                quizList.clear()

                for (document in snapshot) {
                    val id = document.id
                    val title = document.getString("title") ?: "Untitled Quiz"
                    val difficulty = document.getString("difficulty") ?: "normal"

                    quizList.add(Quiz(id, title, difficulty.lowercase()))
                }

                // Sort quizzes: normal → medium → hard
                val difficultyOrder = listOf("normal", "medium", "hard")
                quizList.sortBy { difficultyOrder.indexOf(it.difficulty) }

                quizAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error loading quizzes", e)
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PrepareFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrepareFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}