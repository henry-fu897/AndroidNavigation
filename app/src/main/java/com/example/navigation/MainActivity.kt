package com.example.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
                AppNavHost()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(viewModel: NavViewModel = viewModel()) {

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    val title = when (route) {
        Routes.Q1 -> "Question 1 of 3"
        Routes.Q2 -> "Question 2 of 3"
        Routes.Q3 -> "Question 3 of 3"
        Routes.RESULTS -> "Results"
        else -> ""
    }
    val isQuestion = route == Routes.Q1 || route == Routes.Q2 || route == Routes.Q3
    val canGoBack = route == Routes.Q2 || route == Routes.Q3

    val progress = when (route) {
        Routes.Q1 -> 1f / 3
        Routes.Q2 -> 2f / 3
        Routes.Q3, Routes.RESULTS -> 1f
        else -> 0f   // route is briefly null on first composition
    }
    val animatedProgress by animateFloatAsState(progress, label = "quiz progress")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (canGoBack) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        if (isQuestion) {
                            IconButton(onClick = {
                                viewModel.onRestart()
                                navController.navigate(Routes.Q1) {
                                    popUpTo(Routes.Q1) { inclusive = true }
                                }
                            }) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Restart quiz")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                LinearProgressIndicator(
                    progress = { animatedProgress }, // loading...
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    //primarytrackColor = MaterialTheme.colorScheme.error,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    strokeCap = StrokeCap.Butt, //
                    gapSize = 0.dp,
                    drawStopIndicator = {}
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = Routes.Q1
        ) {
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
            AnswerCard(
                text = option,
                selected = selected == index,
                role = Role.RadioButton,
                onClick = { onSelect(index) }
            )
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
            AnswerCard(
                text = option,
                selected = index in selected,
                role = Role.Checkbox,
                onClick = { onToggle(index) }
            )
        }
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onNext,
                enabled = selected.isNotEmpty(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Next Question")
            }
        }
    }
}

@Composable
fun AnswerCard(
    text: String,
    selected: Boolean,
    role: Role,
    onClick: () -> Unit
) {
    val shape = MaterialTheme.shapes.medium
    val colors = MaterialTheme.colorScheme

    val containerColor by animateColorAsState(
        if (selected) colors.primaryContainer else colors.surface,
        label = "container"
    )
    val contentColor by animateColorAsState(
        if (selected) colors.onPrimaryContainer else colors.onSurface,
        label = "content"
    )
    val borderColor by animateColorAsState(
        if (selected) colors.primary else colors.outline,
        label = "border"
    )

    val interaction = if (role == Role.Checkbox) {
        Modifier.toggleable(value = selected, onValueChange = { onClick() }, role = role)
    } else {
        Modifier.selectable(selected = selected, onClick = onClick, role = role)
    }

    Card(
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(shape)
            .then(interaction)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Box(Modifier.size(24.dp)) {
                if (selected) Icon(Icons.Filled.Check, contentDescription = null)
            }
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
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onFinish, shape = MaterialTheme.shapes.medium) {
                Text("Finish")
            }
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
        FilledTonalButton(
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
