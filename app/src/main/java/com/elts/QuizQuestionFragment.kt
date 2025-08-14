package com.elts

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.elts.databinding.FragmentQuizQuestionBinding
import com.elts.models.Question
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class QuizQuestionFragment : Fragment() {

    private var _binding: FragmentQuizQuestionBinding? = null
    private val binding get() = _binding!!

    private val args: QuizQuestionFragmentArgs by navArgs()
    private var quizId: String? = null
    private val questionList = mutableListOf<Question>()
    private var currentQuestionIndex = 0

    private var score = 0


    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizId = args.quizId
        Log.d("QuizFragment", "Received quizId = $quizId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar
        val toolbar = view.findViewById<Toolbar>(R.id.quizToolbar)
        val navIcon = ContextCompat.getDrawable(requireContext(), R.drawable.smallarrow)
        val insetTop = (18 * resources.displayMetrics.density).toInt()
        val insetDrawable = InsetDrawable(navIcon, 0, insetTop, 0, 0)
        toolbar.navigationIcon = insetDrawable
        toolbar.title = "Quiz"
        toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        // Load questions
        quizId?.let { fetchQuestions(it) }

        // SINGLE click listener (no nesting)
        binding.nextButton.setOnClickListener {
            val selectedId = binding.optionsGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(requireContext(), "Please select an answer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadio = view.findViewById<RadioButton>(selectedId)
            val selectedText = selectedRadio.text?.toString()?.trim().orEmpty()

            val question = questionList[currentQuestionIndex]
            val correctAnswer = (question.correctAnswer ?: "").trim()

            // Reset all backgrounds first
            getOptionRadios().forEach { it.setBackgroundResource(R.drawable.default_option_bg) }

            if (selectedText.equals(correctAnswer, ignoreCase = true)) {
                Log.d("QuizCheck", "✅ Correct answer selected: $selectedText")
                // ✅ Correct
                selectedRadio.setBackgroundResource(R.drawable.correct_option_bg)
                score += 1 // increment score
                // Go next after a short visual feedback
                view.postDelayed({ goToNextQuestion() }, 200)
            } else {
                Log.d("QuizCheck", "❌ Wrong answer selected: $selectedText | Correct: $correctAnswer")
                // ❌ Wrong
                selectedRadio.setBackgroundResource(R.drawable.wrong_option_bg)
                // Highlight the correct one
                getOptionRadios().firstOrNull {
                    it.text?.toString()?.trim()?.equals(correctAnswer, ignoreCase = true) == true
                }?.setBackgroundResource(R.drawable.correct_option_bg)

                showIncorrectDialog(correctAnswer, question.explanation ?: "")
            }

        }
    }

    private fun getOptionRadios() = listOf(binding.option1, binding.option2, binding.option3, binding.option4)

    private fun goToNextQuestion() {
        if (currentQuestionIndex < questionList.size - 1) {
            currentQuestionIndex++
            loadQuestion(currentQuestionIndex)
        } else {
            Toast.makeText(requireContext(), "Quiz finished!", Toast.LENGTH_SHORT).show()
            showResultDialog()        }
    }

    private fun fetchQuestions(quizId: String) {
        db.collection("quizzes")
            .document(quizId)
            .collection("questions")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    questionList.clear()
                    for (doc in documents) {
                        val question = doc.toObject(Question::class.java)
                        questionList.add(question)
                    }
                    // Optional: sort by "order" if you store it
                    // questionList.sortBy { it.order }
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

        val q = questionList[index]
        binding.questionNumberText.text = "Question ${index + 1} / ${questionList.size}"
        binding.questionText.text = q.questionText

        val options = q.options ?: emptyList()

        // Apply text / enable state / visibility
        val radios = getOptionRadios()
        radios.forEachIndexed { i, rb ->
            val text = options.getOrNull(i)
            if (text.isNullOrBlank()) {
                rb.visibility = View.GONE
            } else {
                rb.visibility = View.VISIBLE
                rb.text = text
                rb.isEnabled = true
            }
            rb.setBackgroundResource(R.drawable.default_option_bg) // reset bg
        }

        binding.optionsGroup.clearCheck()
    }

    private fun showResultDialog() {
        val totalQuestions = questionList.size
        val dialogView = layoutInflater.inflate(R.layout.dialog_quiz_result, null)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        dialogView.findViewById<android.widget.TextView>(R.id.resultText).text =
            "You scored $score out of $totalQuestions!"

        dialogView.findViewById<android.widget.Button>(R.id.finishButton).setOnClickListener {
            dialog.dismiss()
            saveScoreToUser(score)
            findNavController().popBackStack() // navigate back
        }

        dialog.show()
    }

    private fun saveScoreToUser(score: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("QuizScore", "No logged-in user found")
            return
        }

        val userUid = currentUser.uid
        val userRef = db.collection("users").document(userUid)

        // Add score entry with quizId and timestamp
        val scoreData = mapOf(
            "quizId" to quizId,
            "score" to score,
            "takenAt" to Timestamp.now()
        )

        userRef.update("quizHistory", FieldValue.arrayUnion(scoreData))
            .addOnSuccessListener { Log.d("QuizScore", "Score saved successfully for user $userUid") }
            .addOnFailureListener { e ->
                Log.e("QuizScore", "Failed to save score", e)
            }
    }


    private fun showIncorrectDialog(correctAnswer: String, explanation: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_incorrect_answer, null)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Transparent window bg so our rounded card shows, and center by default
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        dialogView.findViewById<android.widget.TextView>(R.id.correctAnswerText).text =
            "The correct answer is: $correctAnswer"

        dialogView.findViewById<android.widget.TextView>(R.id.explanationText).text =
            explanation.ifBlank { "Keep going—you’ll get the next one!" }

        dialogView.findViewById<android.widget.Button>(R.id.nextButton).setOnClickListener {
            dialog.dismiss()
            goToNextQuestion()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
