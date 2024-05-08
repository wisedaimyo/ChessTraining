package com.wisedaimyo.chesstraining.main.screens

import com.wisedaimyo.chesstraining.main.data.models.*

sealed class Screen(val route: String) {
    object LoginRegisterScreen: Screen(LOGIN_REGISTER)
    object SignInScreen: Screen(LOGIN)
    object RegisterChoose: Screen(REGISTER_CHOOSE)
    object RegisterImageChoose: Screen(REGISTER_IMAGE_CHOOSE)
    object FinishScreen: Screen(FINISH_REGISTER)
}