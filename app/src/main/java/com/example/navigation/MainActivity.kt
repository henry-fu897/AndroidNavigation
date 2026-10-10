package com.example.navigation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
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
    const val Q3 = "question3"
    const val RESULTS = "results"

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
        composable(Routes.Q1) {
            Q1Screen(
                options = viewModel.q1Options,
                selected = viewModel.q1Answer,
                onSelect = viewModel::onSelect,
                onNext = { navController.navigate(Routes.Q2) }
            )
        }
        composable(Routes.Q2) {
            Q2Screen(
                options = viewModel.q2Options,
                selected = viewModel.q2Answers,
                onToggle = viewModel::onToggle,
                onNext = {navController.navigate(Routes.Q3)}
            )
        }
        composable(Routes.Q3) {
            Q3Screen(
                text = viewModel.q3Answer,
                onTextChange = viewModel::onChange,
                onFinish = {
                    // Remove the quiz screens so Back doesn't return to them.
                    navController.navigate(Routes.RESULTS) {
                        popUpTo(Routes.Q1) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.RESULTS) {
            Results(
                viewModel = viewModel,
                onRestart = {
                    viewModel.onRestart()
                    navController.navigate(Routes.Q1) {
                        popUpTo(Routes.RESULTS) { inclusive = true }
                    }
                }
            )
        }

    }
}

@Composable
fun QuestionLayout(content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val inset = maxWidth * 0.1f // 10%
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = inset, end = inset),
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}
@Composable
fun Q1Screen(options: List<String>,
             selected: Int?,
             onSelect: (Int) -> Unit,
             onNext: () -> Unit
        ){
    QuestionLayout {
        Text(
            "Question 1",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
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
                Text(option, style = MaterialTheme.typography.bodyLarge)
            }
        }
        Button(
            onClick = onNext,
            enabled = selected != null,
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Next Question")
        }
    }
}

@Composable
fun Q2Screen(options: List<String>,
             selected: Set<Int>,
             onToggle: (Int) -> Unit,
             onNext: () -> Unit) {
    QuestionLayout {
        Text(
            "Question 2",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text("Which of these are mammals?", style = MaterialTheme.typography.headlineSmall)
        options.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = index in selected,
                        onValueChange = { onToggle(index) },
                        role = Role.Checkbox
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = index in selected, onCheckedChange = null)
                Spacer(Modifier.width(8.dp))
                Text(option, style = MaterialTheme.typography.bodyLarge)
            }
        }
        Button(
            onClick = onNext,
            enabled = selected.isNotEmpty(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Next Question")
        }
    }
}

@Composable
fun Q3Screen(text: String,
             onTextChange: (String) -> Unit,
             onFinish: () -> Unit) {
    QuestionLayout {
        Text(
            "Question 3",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text("The capital of Japan is ______.", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text("Your answer") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onFinish,
            shape = MaterialTheme.shapes.medium
            ) {
            Text("Finish")
        }
    }
}

@Composable
fun Results(viewModel: NavViewModel,
            onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Results", style = MaterialTheme.typography.headlineMedium)
        Text(
            "You scored ${viewModel.score} / 3",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))

        ResultRow(
            title = "1. Capital of France",
            correct = viewModel.correct.get(0),
            yourAnswer = viewModel.q1Options.get(viewModel.q1Answer?:0),
            correctAnswer = viewModel.q1CorrectAnswer,
        )
        ResultRow(
            title = "2. Mammals",
            correct = viewModel.correct.get(1),
            yourAnswer = viewModel.q2Options.slice(viewModel.q2Answers).joinToString(),
            correctAnswer = viewModel.q2CorrectAnswers
        )
        ResultRow(
            title = "3. Capital of Japan",
            correct = viewModel.correct.get(2),
            yourAnswer = viewModel.q3Answer,
            correctAnswer = viewModel.q3CorrectText
        )

        Spacer(Modifier.weight(1f))
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Restart")
        }
    }
}

@Composable
fun ResultRow(title: String, correct: Boolean, yourAnswer: String, correctAnswer: String) {
    val answerColor = if (correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Column {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Your answer: $yourAnswer",
            style = MaterialTheme.typography.bodyLarge,
            color = answerColor
        )
        if (!correct) {
            Text(
                text = "Correct answer: $correctAnswer",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
