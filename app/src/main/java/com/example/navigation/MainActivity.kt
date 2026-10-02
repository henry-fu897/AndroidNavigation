package com.example.navigation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigation.ui.theme.NavigationTheme


object Routes {
    const val Q1 = "question1"
    const val Q2 = "question2"
}
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

    NavHost(modifier = modifier, navController = navController, startDestination = Routes.Q1) {
        composable(Routes.Q1) {Q1Screen(options = viewModel.q1Options,
            selected = viewModel.q1Answer,
            onSelect = viewModel::onSelect,
            onNext = { navController.navigate(Routes.Q2) })}
        composable(Routes.Q2) {Q2Screen(viewModel.q1Options, viewModel.q1Answer, {navController.popBackStack()})}
    }
}

@Composable
fun Q1Screen(options: List<String>,
             selected: Int?,
             onSelect: (Int) -> Unit,
             onNext: () -> Unit
        ){
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text("Question 1", style = MaterialTheme.typography.labelLarge)
            Text("What is the capital of France?", style = MaterialTheme.typography.headlineSmall)
            options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selected == index,
                            onClick = { onSelect(index) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selected == index, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    Text(option)
                }
            }
            Button(onClick = onNext,
                    enabled = selected != null) {
                Text("Next Question")
            }
        }
    }
}

@Composable
fun Q2Screen(options: List<String>,
             selected: Int?,
             onNext: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            Text(selected?.let(options::getOrNull) ?: "")
            Button(onClick = onNext) {
                Text("Back")
            }
        }
    }
}
