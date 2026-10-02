package com.example.navigation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigation.ui.theme.NavigationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    AppNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavHost(modifier: Modifier, viewModel: NavViewModel = viewModel()) {

    val navController = rememberNavController()

    NavHost(modifier = modifier, navController = navController, startDestination = "screen1") {
        composable("screen1") {Screen1(navController, viewModel.text, viewModel::onTextChange)}
        composable("screen2") {Screen2(navController, viewModel.text)}
    }
}

@Composable
fun Screen1(navController: NavController,
            text: String,
            onTextChange: (String) -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            TextField(
                value = text,
                onValueChange = { onTextChange(it) },
                label = { Text("Enter some text")},
                modifier = Modifier.fillMaxWidth()
            )
            Text("Screen 1")
            Button(onClick = { navController.navigate("screen_2") }) {
                Text("Forward")
            }
        }
    }
}

@Composable
fun Screen2(navController: NavController, text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text(text)
            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
        }
    }
}
