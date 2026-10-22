package com.example.navigation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navigation.ui.theme.NavigationTheme
import com.example.navigation.ui.theme.semanticColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


object Routes {
    const val Q1 = "question1"
    const val Q2 = "question2"
    const val Q3 = "question3"
    const val RESULTS = "results"
    const val SETTINGS = "settings"

}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // defnd on ComponentActivity; == this.enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            var darkThemeOverride by remember { mutableStateOf<Boolean?>(null) }
            LaunchedEffect(systemDark) {
                darkThemeOverride = null
            }
            val darkTheme = darkThemeOverride ?: systemDark

            var fontScale by rememberSaveable { mutableStateOf(1f) }
            val baseDensity = LocalDensity.current

            var dynamicColor by rememberSaveable { mutableStateOf(true) }

            DisposableEffect(darkTheme) {
                this@MainActivity.enableEdgeToEdge( // use this ref of MainActivity
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkTheme }
                )
                onDispose {}
            }

            // Only the font scale changes: sp text grows, dp layout stays put.
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = baseDensity.density,
                    fontScale = baseDensity.fontScale * fontScale
                )
            ) {
                NavigationTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
                    AppNavHost(
                        darkTheme = darkTheme,
                        onDarkChange = { darkThemeOverride = it },
                        fontScale = fontScale,
                        onFontScaleChange = { fontScale = it },
                        dynamicColor = dynamicColor,
                        onDynamicColorChange = { dynamicColor = it }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
        darkTheme: Boolean,
        onDarkChange: (Boolean) -> Unit,
        fontScale: Float,
        onFontScaleChange: (Float) -> Unit,
        dynamicColor: Boolean,
        onDynamicColorChange: (Boolean) -> Unit,
        viewModel: NavViewModel = viewModel()
) {

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route

    val title = when (route) {
        Routes.Q1 -> "Question 1 of 3"
        Routes.Q2 -> "Question 2 of 3"
        Routes.Q3 -> "Question 3 of 3"
        Routes.RESULTS -> "Results"
        Routes.SETTINGS -> "Settings"
        else -> ""
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // !!
    var showRestartDialog by remember { mutableStateOf(false) }

    fun restartQuiz(popUpFrom: String) {
        viewModel.onRestart()
        navController.navigate(Routes.Q1) {
            popUpTo(popUpFrom) { inclusive = true }
        }
        scope.launch { snackbarHostState.showSnackbar("Quiz restarted") } // !!
    }

    val isQuestion = route == Routes.Q1 || route == Routes.Q2 || route == Routes.Q3
    val canGoBack = route == Routes.Q2 || route == Routes.Q3 || route == Routes.SETTINGS

    val progress = when (route) {
        Routes.Q1 -> 1f / 3
        Routes.Q2 -> 2f / 3
        Routes.Q3, Routes.RESULTS -> 1f
        else -> 0f   // route is briefly null on first composition
    }
    val animatedProgress by animateFloatAsState(progress, label = "quiz progress")

    val questionSteps = listOf(Routes.Q1, Routes.Q2, Routes.Q3)
    val currentStep = questionSteps.indexOf(route)
    var sliderValue by remember { mutableStateOf(0f) }
    LaunchedEffect(currentStep) {
        if (currentStep >= 0) sliderValue = currentStep.toFloat()
    }

    fun goToQuestion(step: Int) {
        val target = questionSteps.getOrNull(step) ?: return
        if (target == route) return
        navController.navigate(target) {
            popUpTo(Routes.Q1) { inclusive = false }   // keep question 1 at the bottom
            launchSingleTop = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceDim,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            IconButton(onClick = { showRestartDialog = true }) {
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
                if (route != Routes.SETTINGS) {
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
        },
        bottomBar = {
            // top of bottom bar for question slider
            Column {
                if (isQuestion) {
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        onValueChangeFinished = { goToQuestion(sliderValue.roundToInt()) },
                        valueRange = 0f..2f,
                        steps = 1,   // question 1, question 2, question 3
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                // Bottom navigation: one tab for the quiz (questions and results),
                // one tab for the settings, which is still empty.
                NavigationBar {
                    val onSettings = route == Routes.SETTINGS
                    NavigationBarItem(
                        selected = !onSettings,
                        onClick = {
                            // Leave the settings tab and go back to the quiz flow.
                            if (onSettings) navController.popBackStack()
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text("Quiz") }
                    )
                    NavigationBarItem(
                        selected = onSettings,
                        onClick = {
                            if (!onSettings) {
                                navController.navigate(Routes.SETTINGS) { launchSingleTop = true }
                            }
                        },
                        icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                        label = { Text("Settings") }
                    )
                }
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
                    onRestart = { restartQuiz(Routes.RESULTS) }
                )
            }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                darkTheme = darkTheme,
                onDarkChange = onDarkChange,
                fontScale = fontScale,
                onFontScaleChange = onFontScaleChange,
                dynamicColor = dynamicColor,
                onDynamicColorChange = onDynamicColorChange
            )
        }
        }
    }
    if (showRestartDialog) {
        AlertDialog(
            onDismissRequest = { showRestartDialog = false },
            title = { Text("Restart quiz?") },
            text = { Text("Your answers so far will be cleared.") },
            confirmButton = {
                TextButton(onClick = {
                    showRestartDialog = false
                    restartQuiz(route ?: Routes.Q1)
                }) { Text("Restart") }
            },
            dismissButton = {
                TextButton(onClick = { showRestartDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun SettingsScreen(
        darkTheme: Boolean,
        onDarkChange: (Boolean) -> Unit,
        fontScale: Float,
        onFontScaleChange: (Float) -> Unit,
        dynamicColor: Boolean,
        onDynamicColorChange: (Boolean) -> Unit
) {
    // The Settings tab is still mostly empty
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Dark mode",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(checked = darkTheme, onCheckedChange = onDarkChange)
        }
        Text(
            "Follows your system light/dark setting.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // Dynamic color (Material You) only exists on Android 12 and newer.
        val dynamicAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Dynamic color",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = dynamicColor && dynamicAvailable,
                onCheckedChange = onDynamicColorChange,
                enabled = dynamicAvailable
            )
        }
        Text(
            if (dynamicAvailable) {
                "Uses the colors from your wallpaper."
            } else {
                "Requires Android 12 or newer."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Setting Test Spacing",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Text size",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                "${(fontScale * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Slider(
            value = fontScale,
            onValueChange = onFontScaleChange,
            valueRange = 0.8f .. 1.4f,
            // steps = 5,   // 80%, 90%, 100%, 110%, 120%, 130%, 140%
            modifier = Modifier.fillMaxWidth(),

        )
        Text(
            "Scales all the text in the app.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

    }
}

@Composable
fun QuestionLayout(content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val inset = maxWidth * 0.1f // 10%
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = inset / 2, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = inset / 2, end = inset / 2),
                verticalArrangement = Arrangement.Center,
                content = content
            )
        }
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
        if (selected) colors.primaryContainer else colors.surfaceContainerHigh,
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
    var touched by rememberSaveable { mutableStateOf(false) }
    val isError = touched && text.isBlank()

    QuestionLayout {
        Text(
            "Question 3",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text("The capital of Japan is ______.", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = text,
            onValueChange = {
                touched = true
                onTextChange(it)
            },
            label = { Text("Your answer") },
            supportingText = {
                Text(if (isError) "Please enter an answer" else "Type a city name")
            },
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (text.isNotBlank()) onFinish() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Button(
            onClick = onFinish,
            enabled = text.isNotBlank(),
            modifier = Modifier.padding(top = 16.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Finish")
        }
    }
}

@Composable
fun Results(viewModel: NavViewModel,
            onRestart: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
}

@Composable
fun ResultRow(title: String, correct: Boolean, yourAnswer: String, correctAnswer: String) {
    val answerColor = if (correct) {
        MaterialTheme.semanticColors.correct
    } else {
        MaterialTheme.colorScheme.error
    }

    Column {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (correct) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = if (correct) "Correct" else "Incorrect",
                tint = answerColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Your answer: $yourAnswer",
                style = MaterialTheme.typography.bodyLarge,
                color = answerColor
            )
        }
        if (!correct) {
            Text(
                text = "Correct answer: $correctAnswer",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}