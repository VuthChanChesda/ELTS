package com.elts.models

data class Quiz(
    val id: String = "",
    val title: String = "",
    val difficulty: String = "",
    var categoryId: String = "",
    var questions: List<Question> = emptyList()

)

data class Question(
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val answer: String = "",
    val score: Int = 0,
    val explanation: String = "",
    val order: Int = 1,
    )

