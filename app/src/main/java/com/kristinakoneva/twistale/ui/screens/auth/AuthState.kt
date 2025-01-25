package com.kristinakoneva.twistale.ui.screens.auth

data class AuthState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isNameValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isConfirmPasswordValid: Boolean = true,
    val isLogin: Boolean = true
)
