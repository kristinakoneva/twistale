package com.kristinakoneva.twistale.ui.screens.game.play

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kristinakoneva.twistale.domain.game.models.RoundType
import com.kristinakoneva.twistale.ui.theme.spacing_2
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
    val title = when (state.roundType) {
        RoundType.WRITING -> "Writing round"
        RoundType.DRAWING -> "Drawing round"
    }
    Scaffold(modifier = modifier) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Button(onClick = onLeaveGameRoom) {
                Text("Leave game")
            }
            Text(text = title)
            if (state.isWaiting) {
                Text("Waiting for other players")
            } else {
                when (state.roundType) {
                    RoundType.WRITING -> {
                        AsyncImage(
                            model = state.drawingHint,
                            contentDescription = "Drawing hint",
                        )
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

                    }

                    RoundType.DRAWING -> {
                        Text(
                            text = state.textHint,
                        )
                        DrawingCanvas(
                            onSubmit = onSubmit,
                        )
                    }
                }
                Button(onClick = {
                    onSubmit(null)
                }) {
                    Text("Submit")
                }
            }
        }
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Drawing Area
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            path.moveTo(offset.x, offset.y) // Start new path
                            //points.clear() // Clear points when new drag starts
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
                            // path.reset() // Reset the current path for next drawing
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
                modifier = Modifier.padding(16.dp)
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
                        .padding(8.dp)
                )
            }
        }
    }
}

fun createBitmapFromPaths(paths: List<Path>): Bitmap {
    val bitmapWidth = 1080 // Adjust as needed
    val bitmapHeight = 1920 // Adjust as needed
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
