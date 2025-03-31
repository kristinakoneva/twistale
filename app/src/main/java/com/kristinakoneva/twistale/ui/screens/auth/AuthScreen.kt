package com.kristinakoneva.twistale.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kristinakoneva.twistale.ui.theme.spacing_1
import com.kristinakoneva.twistale.ui.theme.spacing_2
import com.kristinakoneva.twistale.ui.theme.spacing_3
import kotlinx.serialization.Serializable

@Serializable
data object AuthRoute

@Composable
fun AuthScreen(
    navigateToGameRoom: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(viewModel.navigation) {
        viewModel.navigation.collect { event ->
            when (event) {
                is AuthEvent.SuccessfulAuth -> navigateToGameRoom()
                is AuthEvent.FailedAuth -> {
                    focusManager.clearFocus()
                    Toast.makeText(context, "Failed to authenticate", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    viewModel.state.collectAsStateWithLifecycle().value.let { state ->
        AuthScreenContent(
            name = state.name,
            email = state.email,
            password = state.password,
            confirmPassword = state.confirmPassword,
            onNameInputFieldValueChange = viewModel::onNameInputFieldValueChanged,
            onEmailInputFieldValueChange = viewModel::onEmailInputFieldValueChanged,
            onPasswordInputFieldValueChange = viewModel::onPasswordInputFieldValueChanged,
            onConfirmPasswordInputFieldValueChange = viewModel::onConfirmPasswordInputFieldValueChanged,
            isNameValid = state.isNameValid,
            isEmailValid = state.isEmailValid,
            isPasswordValid = state.isPasswordValid,
            isConfirmPasswordValid = state.isConfirmPasswordValid,
            isLogin = state.isLogin,
            onPrimaryButtonClicked = viewModel::onPrimaryButtonClicked,
            onSecondaryButtonClicked = viewModel::onSecondaryButtonClicked,
            modifier = modifier,
        )
    }
}

@Composable
fun AuthScreenContent(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    onNameInputFieldValueChange: (String) -> Unit,
    onEmailInputFieldValueChange: (String) -> Unit,
    onPasswordInputFieldValueChange: (String) -> Unit,
    onConfirmPasswordInputFieldValueChange: (String) -> Unit,
    isNameValid: Boolean,
    isEmailValid: Boolean,
    isPasswordValid: Boolean,
    isConfirmPasswordValid: Boolean,
    isLogin: Boolean,
    onPrimaryButtonClicked: () -> Unit,
    onSecondaryButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val onNameValueChange = remember(name, onNameInputFieldValueChange) { { name: String -> onNameInputFieldValueChange(name) } }
    val onEmailValueChange = remember(email, onNameInputFieldValueChange) { { email: String -> onEmailInputFieldValueChange(email) } }
    val onPasswordValueChange =
        remember(password, onNameInputFieldValueChange) { { password: String -> onPasswordInputFieldValueChange(password) } }
    val onConfirmPasswordValueChange = remember(
        confirmPassword,
        onNameInputFieldValueChange
    ) { { confirmPassword: String -> onConfirmPasswordInputFieldValueChange(confirmPassword) } }

    val focusManager = LocalFocusManager.current

    val titleText = if (isLogin) "Login" else "Register"
    val buttonText = if (isLogin) "Login" else "Register"
    val descriptionText = if (isLogin) "Don't have an account yet?" else "Already have an account?"
    val secondaryButtonText = if (isLogin) "Register" else "Login"
    val isPrimaryButtonEnabled =
        if (isLogin) isEmailValid && isPasswordValid && email.isNotBlank() && password.isNotBlank()
        else isNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid &&
            name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing_3)
            .imePadding(),
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.systemBarsPadding())
        Text(
            text = titleText,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing_3),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        if (!isLogin) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameValueChange,
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing_2),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                isError = !isNameValid,
            )
            if (!isNameValid) {
                Text("Name cannot be empty!", color = MaterialTheme.colorScheme.error)
            }
        }

        OutlinedTextField(
            value = email,
            onValueChange = { onEmailValueChange(it) },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing_1),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            isError = !isEmailValid,
        )
        if (!isEmailValid) {
            Text("Invalid email address!", color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = password,
            onValueChange = { onPasswordValueChange(it) },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing_1),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
            isError = !isPasswordValid,
        )
        if (!isPasswordValid) {
            Text("Invalid password!", color = MaterialTheme.colorScheme.error)
        }

        if (!isLogin) {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { onConfirmPasswordValueChange(it) },
                label = { Text("Confirm Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing_1),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions {
                    focusManager.clearFocus()
                },
                isError = !isConfirmPasswordValid,
            )
            if (!isConfirmPasswordValid) {
                Text("Passwords do not match!", color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(spacing_2))

        Button(
            onClick = { onPrimaryButtonClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing_1),
            enabled = isPrimaryButtonEnabled,
            contentPadding = PaddingValues(spacing_2),
        ) {
            Text(buttonText.uppercase())
        }

        Text(
            text = descriptionText,
            modifier = Modifier
                .padding(top = spacing_3)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        OutlinedButton(
            onClick = {
                focusManager.clearFocus()
                onSecondaryButtonClicked()
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = spacing_1),
            contentPadding = PaddingValues(spacing_2),
        ) {
            Text(secondaryButtonText.uppercase())
        }
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    AuthScreenContent(
        name = "Kristina",
        email = "test@test.com",
        password = "password",
        confirmPassword = "password",
        onNameInputFieldValueChange = {},
        onEmailInputFieldValueChange = {},
        onPasswordInputFieldValueChange = {},
        onConfirmPasswordInputFieldValueChange = {},
        isNameValid = true,
        isEmailValid = true,
        isPasswordValid = true,
        isConfirmPasswordValid = true,
        isLogin = false,
        onPrimaryButtonClicked = {},
        onSecondaryButtonClicked = {}
    )
}

@Preview
@Composable
fun LoginScreenPreview() {
    AuthScreenContent(
        name = "",
        email = "test@test.com",
        password = "password",
        confirmPassword = "",
        onNameInputFieldValueChange = {},
        onEmailInputFieldValueChange = {},
        onPasswordInputFieldValueChange = {},
        onConfirmPasswordInputFieldValueChange = {},
        isNameValid = true,
        isEmailValid = true,
        isPasswordValid = true,
        isConfirmPasswordValid = true,
        isLogin = true,
        onPrimaryButtonClicked = {},
        onSecondaryButtonClicked = {},
    )
}
