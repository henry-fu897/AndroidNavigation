package com.example.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NavViewModel : ViewModel() {

    var text by mutableStateOf("")
        private set

    fun onTextChange (newText: String) {
        text = newText
    }
}