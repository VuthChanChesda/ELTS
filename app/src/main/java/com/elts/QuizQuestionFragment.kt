package com.elts

import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.elts.databinding.FragmentQuizQuestionBinding
import com.elts.models.Question
import com.google.firebase.firestore.FirebaseFirestore

class QuizQuestionFragment : Fragment() {

    private var _binding: FragmentQuizQuestionBinding? = null
    private val binding get() = _binding!!

    private val args: QuizQuestionFragmentArgs by navArgs()
    private var quizId: String? = null
    private val questionList = mutableListOf<Question>()
    private var currentQuestionIndex = 0

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizId = args.quizId
        Log.d("QuizFragment", "Received quizId = $quizId")    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val toolbar = view.findViewById<Toolbar>(R.id.quizToolbar)

        val navIcon = ContextCompat.getDrawable(requireContext(), R.drawable.smallarrow)
        val insetTop = (18 * resources.displayMetrics.density).toInt() // 8dp down
        val insetDrawable = InsetDrawable(navIcon, 0, insetTop, 0, 0)

        toolbar.navigationIcon = insetDrawable

        toolbar.title = "Quiz"
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        quizId?.let { fetchQuestions(it) }

        binding.nextButton.setOnClickListener {
            if (currentQuestionIndex < questionList.size - 1) {
                currentQuestionIndex++
                loadQuestion(currentQuestionIndex)
            } else {
                Toast.makeText(requireContext(), "Quiz finished!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchQuestions(quizId: String) {
        db.collection("quizzes")
            .document(quizId)
            .collection("questions") // subcollection
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    questionList.clear()
                    for (doc in documents) {
                        val question = doc.toObject(Question::class.java)
                        Log.d("QuizFragment", "Loaded question: ${question.questionText}")

                        questionList.add(question)
                    }
                    loadQuestion(0)
                } else {
                    Toast.makeText(requireContext(), "No questions found!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("QuizFragment", "Failed to fetch questions", e)
                Toast.makeText(requireContext(), "Failed to fetch questions", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadQuestion(index: Int) {
        if (questionList.isEmpty()) return

        val question = questionList[index]
        binding.questionNumberText.text = "Question ${index + 1} / ${questionList.size}"
        binding.questionText.text = question.questionText

        val optionButtons = listOf(
            binding.option1Button,
            binding.option2Button,
            binding.option3Button,
            binding.option4Button
        )

        for (i in optionButtons.indices) {
            optionButtons[i].text = question.options.getOrNull(i) ?: ""
            optionButtons[i].isEnabled = question.options.getOrNull(i) != null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_QUIZ_ID = "quiz_id"

        fun newInstance(quizId: String) = QuizQuestionFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_QUIZ_ID, quizId)
            }
        }
    }
}
