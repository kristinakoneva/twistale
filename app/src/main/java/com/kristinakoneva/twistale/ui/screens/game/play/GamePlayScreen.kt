package com.kristinakoneva.twistale.ui.screens.game.play

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kristinakoneva.twistale.domain.game.models.RoundType
import com.kristinakoneva.twistale.ui.theme.spacing_2
import kotlinx.serialization.Serializable

@Serializable
data object GamePlayRoute

@Composable
fun GamePlayScreen(
    onNavigateToGameRoom: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GamePlayViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel.navigation) {
        viewModel.navigation.collect { event ->
            when (event) {
                is GamePlayEvent.NavigateToGameRoom -> onNavigateToGameRoom()
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
    onSubmit: () -> Unit,
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

                    }
                }
                Button(onClick = onSubmit) {
                    Text("Submit")
                }
            }
        }
    }
}
