package com.wisedaimyo.chesstraining.main.screens

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.wisedaimyo.chesstraining.main.data.models.*
import com.wisedaimyo.chesstraining.main.screens.register.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NavigationRegisterRouting(navController: NavHostController) {
        NavHost(
            navController = navController,
            startDestination = Screen.LoginRegisterScreen.route
        ) {
            composable(route = Screen.LoginRegisterScreen.route) {
               LoginRegisterScreen(
                   navigateToSignIn = {
                      navController.navigate(Screen.SignInScreen.route)
                   },
                   navigateToRegister = {
                       navController.navigate(Screen.RegisterChoose.route)
                   })
            }

            composable(route = Screen.RegisterChoose.route) {
                RegisterChooseScreen(
                    navigateToBack = { navController.navigate(Screen.LoginRegisterScreen.route) },
                    navController
                )
            }
            
            composable(
                route = "${Screen.RegisterImageChoose.route}/{isTrainer}",
                arguments = listOf(navArgument("isTrainer") {type = NavType.BoolType})
                ) {backStackEntry ->
                val isTrainer = backStackEntry.arguments?.getBoolean("isTrainer")
                RegisterImageChooseScreen(
                    isTrainer = isTrainer ?: false,
                    navigateToBack = { navController.navigate(Screen.RegisterChoose.route) },
                    navController
                )
            }

            composable(
                route = "finish_register/{isTrainer}/{image}",
                arguments = listOf(
                    navArgument("isTrainer") { type = NavType.BoolType },
                    navArgument("image") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val isTrainer = backStackEntry.arguments?.getBoolean("isTrainer")
                val image = backStackEntry.arguments?.getInt("image")
                RegisterFinishScreen(
                    navigateBack = { navController.navigate(Screen.LoginRegisterScreen.route) },
                    isTrainer = isTrainer ?: false,
                    image = image ?: 0
                )
            }
            
            composable(
                route = Screen.SignInScreen.route
            ) {
                LoginScreen(navigateBack = {
                    navController.navigate(Screen.LoginRegisterScreen.route)
                })
            }


            composable(
                route = "finish_register/{isTrainer}/{image}",
                arguments = listOf(
                    navArgument("isTrainer") { type = NavType.BoolType },
                    navArgument("image") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val isTrainer = backStackEntry.arguments?.getBoolean("isTrainer")
                val image = backStackEntry.arguments?.getInt("image")
                RegisterFinishScreen(
                    navigateBack = { navController.navigate(Screen.LoginRegisterScreen.route) },
                    isTrainer = isTrainer ?: false,
                    image = image ?: 0
                )
            }


        }
}