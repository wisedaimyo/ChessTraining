package com.wisedaimyo.chesstraining.main.screens.user

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.getImageResId
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.components.Custom_Card
import com.wisedaimyo.chesstraining.main.components.ShimmeringTaskForStudent
import com.wisedaimyo.chesstraining.main.components.TaskForStudent
import com.wisedaimyo.chesstraining.main.data.models.EDIT_GAME
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.TRAINER_ZONE
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.EditorViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.StudentScreenViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.TrainerTasksViewModel
import com.wisedaimyo.chesstraining.main.screens.main.ShimmeringBoxes

@Composable
fun EditTrainerModeScreen(
    viewModel: StudentScreenViewModel = hiltViewModel(),
    navController: NavController
){
    LaunchedEffect(key1 = viewModel.currentUser) {
        if(viewModel.currentUser.isTrainer == true) {
            viewModel.getCourseDataForTrainer()
        } else if(viewModel.currentUser.isTrainer == false) {
            viewModel.getCourseDataForStudent()
        }
    }

    LaunchedEffect(key1 = viewModel.chessTasks) {
        if(viewModel.chessTasks.isNotEmpty())
            for (task in viewModel.chessTasks) {
                if(task.second.type == "opening")
                    viewModel.openings = viewModel.openings + task
                if(task.second.type == "middle")
                    viewModel.middle = viewModel.middle + task
                if(task.second.type == "end")
                    viewModel.end = viewModel.end + task
                if(task.second.type == "other")
                    viewModel.other = viewModel.other + task
            }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                BackButton {
                    navController.navigate(TRAINER_ZONE)
                }

                Text(
                    text = "EDITOR",
                    modifier = Modifier.padding(horizontal = 15.dp),
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.padding(vertical = 15.dp))

            if (viewModel.isLoaded) {
                EditScreen(navController = navController, viewModel)
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavController,
    viewModel: StudentScreenViewModel
) {
    var checkTasks by remember { mutableStateOf(false) }
    var isSheet = remember { mutableStateOf(false) }
    var checkSettings by remember { mutableStateOf(false) }

    Column {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {

            if(isSheet.value) {
                ModalBottomSheet(onDismissRequest = { isSheet.value = false }) {
                    Column {
                        RemoveTasks(isSheet = isSheet)
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = {
                        isSheet.value = true
                    }) {
                    Text(text = "Zmaž časové úlohy.")
                }
            }
            
            Spacer(modifier = Modifier.padding(vertical = 15.dp))

            Column {
                TopMenuSectionTrainer("Otvorenia", viewModel.openings.size)

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    if (viewModel.openings.isNotEmpty()) {
                        for (task in viewModel.openings) {
                            task.second.name?.let {
                                Firebase.auth.uid?.let { it1 ->
                                    task.second.done?.contains(
                                        it1
                                    ) ?: false
                                }?.let { it2 ->
                                    Custom_Card(
                                        image = getImageResId(context = LocalContext.current, task.second.image ?: "ic_launcher_foreground" ),
                                        isFinihed = it2,
                                        name = it
                                    ) {
                                        navController.navigate("$EDIT_GAME/${task.first}")
                                    }
                                }
                            }
                        }
                    } else {
                        ShimmeringBoxes()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 13.dp))

            Column {
                TopMenuSectionTrainer("Stredná hra", viewModel.middle.size)

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {

                    if (viewModel.middle.isNotEmpty()) {
                        for (task in viewModel.middle) {
                            task.second.name?.let {
                                Firebase.auth.uid?.let { it1 ->
                                    task.second.done?.contains(
                                        it1
                                    ) ?: false
                                }?.let { it2 ->
                                    Custom_Card(
                                        image = getImageResId(context = LocalContext.current, task.second.image ?: "ic_launcher_foreground" ),
                                        isFinihed = it2,
                                        name = it
                                    ) {
                                        navController.navigate("$EDIT_GAME/${task.first}")
                                    }
                                }
                            }
                        }
                    } else {
                        ShimmeringBoxes()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 13.dp))

            Column {
                TopMenuSectionTrainer("Koncová hra", viewModel.end.size)

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    if (viewModel.end.isNotEmpty()) {
                        for (task in viewModel.end) {
                            task.second.name?.let {
                                Firebase.auth.uid?.let { it1 ->
                                    task.second.done?.contains(
                                        it1
                                    ) ?: false
                                }?.let { it2 ->
                                    Custom_Card(
                                        image = getImageResId(context = LocalContext.current, task.second.image ?: "ic_launcher_foreground" ),
                                        isFinihed = it2,
                                        name = it
                                    ) {
                                        navController.navigate("$EDIT_GAME/${task.first}")
                                    }
                                }
                            }
                        }
                    } else {
                        ShimmeringBoxes()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(vertical = 13.dp))

            Column {
                TopMenuSectionTrainer("Ostatné", viewModel.other.size)

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    if (viewModel.other.isNotEmpty()) {
                        for (task in viewModel.other) {
                            task.second.name?.let {
                                Firebase.auth.uid?.let { it1 ->
                                    task.second.done?.contains(
                                        it1
                                    ) ?: false
                                }?.let { it2 ->
                                    Custom_Card(
                                        image = getImageResId(context = LocalContext.current, task.second.image ?: "ic_launcher_foreground" ),
                                        isFinihed = it2,
                                        name = it
                                    ) {
                                        navController.navigate("$EDIT_GAME/${task.first}")
                                    }
                                }
                            }
                        }
                    } else {
                        ShimmeringBoxes()
                    }
                }
            }

            Spacer(
                modifier = Modifier.padding(
                    vertical = 30.dp
                )
            )

        }
    }
}


@Composable
fun RemoveTasks(viewModel: StudentScreenViewModel = hiltViewModel(), isSheet: MutableState<Boolean>) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Klikni na zmazanie",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
        )

        LaunchedEffect(key1 = viewModel.removeTrainerDateTaskWithIdResponse) {
            when(viewModel.removeTrainerDateTaskWithIdResponse) {
                is Response.Success -> { isSheet.value = false }
                is Response.Failure -> { Toast.makeText(context, "Nastala chyba", Toast.LENGTH_LONG).show() }
                else -> { }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        if (viewModel.dateTasks.isNotEmpty()) {
            for(task in viewModel.dateTasks)
                Column(
                    modifier =
                        Modifier.clickable {
                            viewModel.removeDateTask(task.first)
                        }
                ) {
                task.second.finish?.let {
                    TaskForStudent(
                        title = task.second.name ?: "",
                        content = task.second.description ?: "",
                        time = it.toDate()
                    )
                }
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))

                }
        } else {
            for (x in 0..<3) {
                ShimmeringTaskForStudent()
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
            }
        }

    }
}