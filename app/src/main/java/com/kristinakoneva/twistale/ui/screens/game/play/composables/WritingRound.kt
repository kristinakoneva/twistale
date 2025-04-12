package com.kristinakoneva.twistale.ui.screens.game.play.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import coil3.compose.AsyncImage
import com.kristinakoneva.twistale.ui.screens.game.play.GamePlayState
import com.kristinakoneva.twistale.ui.theme.spacing_1
import com.kristinakoneva.twistale.ui.theme.spacing_2

@Composable
fun WritingRound(
    state: GamePlayState,
    onTextInputValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val focusManager = LocalFocusManager.current
        if (state.drawingHint.isEmpty()) {
            Spacer(modifier = Modifier.height(spacing_1))
            Text("Write an initial phrase to start a tale. 🏁 Feel free to be creative! 💡")
            Spacer(modifier = Modifier.height(spacing_1))
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
        Spacer(modifier = Modifier.height(spacing_2))
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Submit")
        }
    }
}
