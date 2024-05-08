package com.wisedaimyo.chesstraining.main.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.keyframesWithSpline
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wisedaimyo.chesstraining.main.components.AppBottomNavigation
import com.wisedaimyo.chesstraining.main.data.models.CHECK_RATING
import com.wisedaimyo.chesstraining.main.data.models.STUDENT_ZONE
import com.wisedaimyo.chesstraining.main.data.models.NAV_COURSE
import com.wisedaimyo.chesstraining.main.data.models.NAV_HOME
import com.wisedaimyo.chesstraining.main.data.models.NAV_SETTINGS
import com.wisedaimyo.chesstraining.main.data.models.NAV_TRAINING
import com.wisedaimyo.chesstraining.main.data.models.PLAYED_GAMES
import com.wisedaimyo.chesstraining.main.data.models.PLAY_PUZZLE
import com.wisedaimyo.chesstraining.main.data.models.PLAY_ROBOT
import com.wisedaimyo.chesstraining.main.data.models.*
import com.wisedaimyo.chesstraining.main.screens.game.AddNewToCourseScreen
import com.wisedaimyo.chesstraining.main.screens.game.AnalyzeCheckGameEdit
import com.wisedaimyo.chesstraining.main.screens.game.EditChessGameScreen
import com.wisedaimyo.chesstraining.main.screens.game.LearnMovePiecesScreen
import com.wisedaimyo.chesstraining.main.screens.game.PlayingPuzzleScreen
import com.wisedaimyo.chesstraining.main.screens.game.PlayingScreen
import com.wisedaimyo.chesstraining.main.screens.game.PlayingVsPlayerScreen
import com.wisedaimyo.chesstraining.main.screens.game.PlayingVsScreen
import com.wisedaimyo.chesstraining.main.screens.main.AppScreen
import com.wisedaimyo.chesstraining.main.screens.main.CourseScreen
import com.wisedaimyo.chesstraining.main.screens.main.SettingsScreen
import com.wisedaimyo.chesstraining.main.screens.main.TrainingScreen
import com.wisedaimyo.chesstraining.main.screens.user.EditTrainerModeScreen
import com.wisedaimyo.chesstraining.main.screens.user.StudentScreen
import com.wisedaimyo.chesstraining.main.screens.user.PlayedGamesScreen
import com.wisedaimyo.chesstraining.main.screens.user.RatingGamesScreen
import com.wisedaimyo.chesstraining.main.screens.user.RatingPuzzleScreen
import com.wisedaimyo.chesstraining.main.screens.user.TrainerScreen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview
@Composable
fun NavigationRouting() {
    val navController = rememberNavController()
    val showBottomBar = remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            if (showBottomBar.value) {
                AppBottomNavigation(navController = navController)
            }
        }
    ) {
        NavHost(navController = navController, startDestination = NAV_HOME) {

           // NAV_HOME
            composable(NAV_HOME) {
                AppScreen(navController = navController)
                showBottomBar.value = true
            }

            composable("$PLAY_ROBOT/{time}/{difficulty}") { backStackEntry ->
                val time = backStackEntry.arguments?.getString("time")
                val difficulty = backStackEntry.arguments?.getString("difficulty")

                if (difficulty != null) {
                    if (time != null) {
                        PlayingVsScreen(navController= navController, time =  time.toLong(), difficulty = difficulty.toInt())
                    }
                }
                showBottomBar.value = false
            }

            composable("$EDIT_GAME/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                if (id != null) {
                    EditChessGameScreen(
                        id = id,
                        navController = navController)
                    showBottomBar.value = false
                }
            }

            composable("$LEARN_GAME/{id}/{isFromTrainer}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                val isFromTrainer = backStackEntry.arguments?.getString("isFromTrainer")

                if (id != null && isFromTrainer != null) {
                    PlayingScreen(
                        id = id,
                        isFromTrainer = isFromTrainer.toBoolean(),
                        name = "",
                        navController = navController)
                    showBottomBar.value = false
                }
            }


            composable("$LEARN_GAME/{name}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name")

                if (name != null) {
                    PlayingScreen(
                        name = name,
                        navController = navController)
                    showBottomBar.value = false
                }
            }

            composable(CHECK_RATING) {
                RatingGamesScreen(navController= navController)
                showBottomBar.value = false
            }

            composable(SHOW_PUZZLE_RATING) {
                RatingPuzzleScreen(navController= navController)
                showBottomBar.value = false
            }

            composable(STUDENT_ZONE) {
                StudentScreen(navController= navController)
                showBottomBar.value = false
            }

            composable(EDIT_TRAINER) {
                EditTrainerModeScreen(navController = navController)
                showBottomBar.value = false
            }
            
            composable(TRAINER_ZONE) {
                TrainerScreen(navController = navController)
                showBottomBar.value = false
            }

            composable(PLAYED_GAMES) {
                PlayedGamesScreen(navController= navController)
                showBottomBar.value = false
            }


            composable(LEARN_MOVE_PIECES) {
                LearnMovePiecesScreen(navController)
                showBottomBar.value = false
            }

            composable("$ADD_TO_COURSE/{courseId}") { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId")

                if (courseId != null) {
                    AddNewToCourseScreen(navController= navController, courseId = courseId)
                    showBottomBar.value = false
                }
            }

            composable("$ANALYZE_GAME/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")

                if (id != null){
                    AnalyzeCheckGameEdit(navController = navController, id = id)
                    showBottomBar.value = false
                }
            }

            composable(ANALYZE_GAME) {
                AnalyzeCheckGameEdit(navController = navController)
            }

            composable("$PLAY_AGAINST_PLAYER/{gameId}") { backStackEntry ->
                val gameId = backStackEntry.arguments?.getString("gameId")

                if(gameId!= null) {
                    PlayingVsPlayerScreen(navController, gameId = gameId)
                    showBottomBar.value = false
                }
            }

            // NAV_TRAINING
            composable(NAV_TRAINING) {
                TrainingScreen(navController= navController)
                showBottomBar.value = true
            }

            composable("$PLAY_PUZZLE/{elo}") { backStackEntry ->
                val elo = backStackEntry.arguments?.getString("elo")

                if (elo != null) {
                    PlayingPuzzleScreen(navController= navController, elo = elo.toInt())
                    showBottomBar.value = false
                }

            }

            // NAV_COURSE
            composable(NAV_COURSE) {
                CourseScreen(navController= navController)
                showBottomBar.value = true
            }


            // NAV_SETTINGS
            composable(NAV_SETTINGS) {
                SettingsScreen()
                showBottomBar.value = true
            }


        }
    }
}