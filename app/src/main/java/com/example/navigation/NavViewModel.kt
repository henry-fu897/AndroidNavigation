package com.example.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NavViewModel : ViewModel() {

    val q1Options = listOf("Paris", "Berlin", "Madrid", "Rome")
    private val q1CorrectIndex = 0

    var q1Answer by mutableStateOf<Int?>(null)
        private set

    fun onSelect(index: Int) {
        q1Answer = index
    }
}