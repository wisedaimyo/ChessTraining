package com.wisedaimyo.chesstraining.main.data.models

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)