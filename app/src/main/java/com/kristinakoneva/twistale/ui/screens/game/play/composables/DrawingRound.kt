package com.kristinakoneva.twistale.ui.screens.game.play.composables

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.kristinakoneva.twistale.ui.screens.game.play.GamePlayState
import com.kristinakoneva.twistale.ui.theme.spacing_1
import com.kristinakoneva.twistale.ui.theme.spacing_2

@Composable
fun DrawingRound(
    state: GamePlayState,
    onSubmit: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
) {
    val path = remember { Path() } // The path that will store the drawing
    val points = remember { mutableStateListOf<Offset>() } // List to store individual points
    val paths = remember { mutableStateListOf<Path>() } // Store completed paths
    val canvasSize = remember { mutableStateOf(IntSize.Zero) }
    Column(modifier = modifier) {
        Spacer(Modifier.height(spacing_1))
        Text("Try to draw something which would summarize the following phrase 👀:")
        Spacer(Modifier.height(spacing_2))
        Text(
            text = "\"${state.textHint}\"",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(spacing_2))
        Column(modifier = Modifier.weight(1f).fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
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
                        .onSizeChanged {
                            canvasSize.value = it
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
        }
        Spacer(modifier = Modifier.height(spacing_2))
        Button(
            onClick = {
                onSubmit(
                    createBitmapFromPaths(
                        paths = paths,
                        width = canvasSize.value.width,
                        height = canvasSize.value.height,
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Submit")
        }
        Spacer(modifier = Modifier.height(spacing_2))
    }
}

fun createBitmapFromPaths(paths: List<Path>, width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
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
