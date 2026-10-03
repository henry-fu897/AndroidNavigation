package com.example.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NavViewModel : ViewModel() {

    val q1Options = listOf("Paris", "Berlin", "Madrid", "Rome")
    private val q1CorrectIndex = 0
    val q1CorrectAnswer: String
        get() = q1Options.get(q1CorrectIndex)

    val q2Options = listOf("Dolphin", "Shark", "Bat", "Penguin")
    private val q2CorrectIndices = setOf(0, 2)
    val q2CorrectAnswers: String
        get() = q2Options.slice(q2CorrectIndices).joinToString()

    val q3CorrectText = "Tokyo"


    var q1Answer by mutableStateOf<Int?>(null)
        private set

    var q2Answers by mutableStateOf<Set<Int>>(emptySet())
        private set

    var q3Answer by mutableStateOf("")
        private set


    val correct : List<Boolean>
        get() = listOf(q1Answer == q1CorrectIndex, q2Answers == q2CorrectIndices, q3Answer.trim().equals(q3CorrectText, ignoreCase = true))
    val score : Int
        get() = correct.count{it}


    fun onSelect(index: Int) {
        q1Answer = index
    }

    fun onToggle(index: Int) {
        q2Answers = if (index in q2Answers) q2Answers - index else q2Answers + index
    }

    fun onChange(text: String) {
        q3Answer = text
    }

    fun onRestart() {
        q1Answer = null
        q2Answers = emptySet()
        q3Answer = ""
    }
}