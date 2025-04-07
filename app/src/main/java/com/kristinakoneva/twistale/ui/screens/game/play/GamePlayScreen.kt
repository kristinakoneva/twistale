package com.kristinakoneva.twistale.ui.screens.game.play

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kristinakoneva.twistale.domain.game.models.RoundType
import com.kristinakoneva.twistale.ui.theme.spacing_1
import com.kristinakoneva.twistale.ui.theme.spacing_2
import com.kristinakoneva.twistale.ui.theme.spacing_3
import kotlinx.serialization.Serializable

@Serializable
data object GamePlayRoute

@Composable
fun GamePlayScreen(
    onNavigateToGameRoom: () -> Unit,
    onNavigateToStory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GamePlayViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.navigation) {
        viewModel.navigation.collect { event ->
            when (event) {
                is GamePlayEvent.NavigateToGameRoom -> onNavigateToGameRoom()
                is GamePlayEvent.NavigateToStory -> onNavigateToStory()
            }
        }
    }
    viewModel.state.collectAsStateWithLifecycle().value.let { state ->
        GamePlayContent(
            onLeaveGameRoom = viewModel::onLeaveGameRoom,
            onTextInputValueChange = viewModel::onTextInputValueChange,
            onSubmit = viewModel::onSubmit,
            modifier = modifier,
            state = state,
        )
    }

}

@Composable
private fun GamePlayContent(
    state: GamePlayState,
    onLeaveGameRoom: () -> Unit,
    onTextInputValueChange: (String) -> Unit,
    onSubmit: (Bitmap?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val roundTypeTitle = when (state.roundType) {
        RoundType.WRITING -> "✍️ Writing round️"
        RoundType.DRAWING -> "🎨 Drawing round"
    }
    // Used for the drawing round
    val path = remember { Path() } // The path that will store the drawing
    val points = remember { mutableStateListOf<Offset>() } // List to store individual points
    val paths = remember { mutableStateListOf<Path>() } // Store completed paths
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) } // For storing the generated bitmap

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing_3)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(spacing_3))
                Row(horizontalArrangement = Arrangement.Center) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = onLeaveGameRoom) {
                        Text("Leave game")
                    }
                }
                Spacer(modifier = Modifier.height(spacing_3))
                Text(
                    text = roundTypeTitle,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                )
                if (state.isWaiting) {
                    WaitingState()
                } else {
                    when (state.roundType) {
                        RoundType.WRITING -> {
                            if (state.drawingHint.isEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Write an initial phrase to start a tale. 🏁 Feel free to be creative! 💡")
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            OutlinedTextField(
                                value = state.textInput,
                                textStyle = TextStyle(color = Color.Black),
                                onValueChange = onTextInputValueChange,
                                label = { Text("Phrase") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = spacing_2),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions {
                                    focusManager.clearFocus()
                                },
                            )
                            AsyncImage(
                                model = state.drawingHint,
                                contentDescription = "Drawing hint",
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        RoundType.DRAWING -> {
                            Spacer(Modifier.height(spacing_1))
                            Text("Try to draw something which would summarize the following phrase 👀:")
                            Spacer(Modifier.height(spacing_2))
                            Text(
                                text = "\"${state.textHint}\"",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.height(spacing_2))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp),
                            ) {
                                // Drawing Area
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(Color.White, shape = MaterialTheme.shapes.medium)
                                        .border(
                                            shape = MaterialTheme.shapes.medium,
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        .pointerInput(Unit) {
                                            detectDragGestures(
                                                onDragStart = { offset ->
                                                    path.moveTo(offset.x, offset.y) // Start new path
                                                    points.add(offset) // Add first point
                                                },
                                                onDrag = { change, _ ->
                                                    val position = change.position
                                                    path.lineTo(position.x, position.y) // Draw as user drags
                                                    points.add(position) // Add each point to the list as user drags
                                                },
                                                onDragEnd = {
                                                    // When drag ends, add the current path to the list of paths
                                                    paths.add(path)
                                                }
                                            )
                                        }
                                ) {
                                    // Draw all the paths already drawn
                                    paths.forEach { drawnPath ->
                                        drawPath(
                                            path = drawnPath,
                                            color = Color.Black,
                                            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                        )
                                    }

                                    // Draw the ongoing path while dragging
                                    drawPath(
                                        path = path,
                                        color = Color.Black,
                                        style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                    )

                                    // Draw the individual points as circles
                                    points.forEach { point ->
                                        drawCircle(
                                            color = Color.Black,
                                            radius = 5f,
                                            center = point
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(spacing_2))
                        }
                    }
                }
                if (!state.isWaiting) {
                    Button(
                        onClick = {
                            when (state.roundType) {
                                RoundType.WRITING -> onSubmit(null)
                                RoundType.DRAWING -> {
                                    bitmapState.value = createBitmapFromPaths(paths)
                                    onSubmit(bitmapState.value)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}

@Composable
private fun WaitingState() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "You have submitted your input for this round. ✅",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(spacing_3))
        Text(
            "Waiting for other players... ",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(spacing_2))
        AsyncImage(
            model = "https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExa3V4YW40c3ZybHc0MW10YW84dWQ1NnUxYndicTEyazlydWtkajN1YiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/um2kBnfo55iW4ZH1Fa/giphy.gif",
            contentDescription = "Waiting for other players",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
    }
}

@Composable
fun DrawingCanvas(
    onSubmit: (Bitmap?) -> Unit = {},
) {
    val path = remember { Path() } // The path that will store the drawing
    val points = remember { mutableStateListOf<Offset>() } // List to store individual points
    val paths = remember { mutableStateListOf<Path>() } // Store completed paths
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) } // For storing the generated bitmap

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
    ) {
        // Drawing Area
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            path.moveTo(offset.x, offset.y) // Start new path
                            points.add(offset) // Add first point
                        },
                        onDrag = { change, _ ->
                            val position = change.position
                            path.lineTo(position.x, position.y) // Draw as user drags
                            points.add(position) // Add each point to the list as user drags
                        },
                        onDragEnd = {
                            // When drag ends, add the current path to the list of paths
                            paths.add(path)
                        }
                    )
                }
        ) {
            // Draw all the paths already drawn
            paths.forEach { drawnPath ->
                drawPath(
                    path = drawnPath,
                    color = Color.Black,
                    style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // Draw the ongoing path while dragging
            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw the individual points as circles
            points.forEach { point ->
                drawCircle(
                    color = Color.Black,
                    radius = 5f,
                    center = point
                )
            }
        }

        // Bottom UI elements
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            // Save Drawing Button
            Button(
                onClick = {
                    // Create a bitmap from the drawing (paths)
                    bitmapState.value = createBitmapFromPaths(paths)
                    onSubmit(bitmapState.value)
                },
                modifier = Modifier.padding(spacing_2)
            ) {
                Text("Generate Preview")
            }

            // Display the Generated Bitmap Preview
            bitmapState.value?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Preview of Drawing",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray)
                        .padding(spacing_1)
                )
            }
        }
    }
}

fun createBitmapFromPaths(paths: List<Path>): Bitmap {
    val bitmapWidth = 1080
    val bitmapHeight = 1920
    val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = android.graphics.Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    // Set background color to white
    canvas.drawColor(android.graphics.Color.WHITE)

    // Draw each path on the canvas
    paths.forEach { path ->
        path.asAndroidPath().apply {
            canvas.drawPath(this, paint)
        }
    }

    return bitmap
}
