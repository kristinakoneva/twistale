package com.kristinakoneva.twistale.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristinakoneva.twistale.domain.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val stateFlow: MutableStateFlow<AuthState> by lazy { MutableStateFlow(AuthState()) }
    val state: StateFlow<AuthState> get() = stateFlow

    private val navigationChannel = Channel<AuthEvent>(Channel.BUFFERED)
    val navigation = navigationChannel.receiveAsFlow()

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
    }

    private var isLogin: Boolean = true

    init {
        viewModelScope.launch {
            if (userRepository.getCurrentUser() != null) {
                navigationChannel.send(AuthEvent.SuccessfulAuth)
            }
        }
    }

    fun onNameInputFieldValueChanged(input: String) {
        stateFlow.update {
            it.copy(name = input, isNameValid = input.isNotBlank())
        }
    }

    fun onEmailInputFieldValueChanged(input: String) {
        stateFlow.update {
            it.copy(
                email = input,
                isEmailValid = input.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
            )
        }
    }

    fun onPasswordInputFieldValueChanged(input: String) {
        stateFlow.update {
            it.copy(
                password = input,
                isPasswordValid = input.isNotBlank() && input.length >= MIN_PASSWORD_LENGTH
            )
        }
    }

    fun onConfirmPasswordInputFieldValueChanged(input: String) {
        stateFlow.update {
            it.copy(
                confirmPassword = input,
                isConfirmPasswordValid = input.isNotBlank() && input == it.password
            )
        }
    }

    fun onPrimaryButtonClicked() {
        viewModelScope.launch {
            try {
                if (isLogin) {
                    userRepository.loginUser(state.value.email, state.value.password)
                } else {
                    userRepository.registerUser(state.value.email, state.value.password, state.value.name)
                }
                if (userRepository.getCurrentUser() != null) {
                    navigationChannel.send(AuthEvent.SuccessfulAuth)
                } else {
                    navigationChannel.send(AuthEvent.FailedAuth)
                }
            } catch (e: Exception) {
                navigationChannel.send(AuthEvent.FailedAuth)
            } finally {
                resetFields()
            }
        }
    }

    fun onSecondaryButtonClicked() {
        isLogin = !isLogin
        resetFields()
        stateFlow.update {
            it.copy(
                isLogin = isLogin
            )
        }
    }

    private fun resetFields() {
        stateFlow.update {
            it.copy(
                name = "",
                email = "",
                password = "",
                confirmPassword = "",
                isNameValid = true,
                isEmailValid = true,
                isPasswordValid = true,
                isConfirmPasswordValid = true
            )
        }
    }
}
