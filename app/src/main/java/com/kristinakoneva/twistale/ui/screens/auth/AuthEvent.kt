package com.kristinakoneva.twistale.ui.screens.auth

sealed interface AuthEvent {
    data object SuccessfulAuth : AuthEvent

    data object FailedAuth : AuthEvent
}
