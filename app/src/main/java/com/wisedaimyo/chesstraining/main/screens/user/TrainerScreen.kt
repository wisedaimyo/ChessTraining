package com.wisedaimyo.chesstraining.main.screens.user

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.data.models.ADD_TO_COURSE
import com.wisedaimyo.chesstraining.main.data.models.EDIT_TRAINER
import com.wisedaimyo.chesstraining.main.data.models.NAV_HOME
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.STUDENT_ZONE
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerCourse
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerDateTask
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.TrainerCourseViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.TrainerTasksViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun TrainerScreen(
    viewModel: TrainerCourseViewModel = hiltViewModel(),
    navController: NavController
) {
    val isAddNewStudent = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {

        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                BackButton {
                    navController.navigate(NAV_HOME)
                }

                if (viewModel.trainerCourse.value.trainerId != null) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Rate and check students",
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .padding(vertical = 1.dp)
                            .size(45.dp)
                            .clickable {
                                isAddNewStudent.value = true
                            }
                    )
                }
            }

            if (viewModel.trainerCourse.value.trainerId != null) {
                TrainerIsCreatedScreen(navController, viewModel.trainerCourseId)

                if (isAddNewStudent.value)
                    Dialog(onDismissRequest = { isAddNewStudent.value = false }) {
                        AddNewStudentComposable(viewModel = viewModel)
                    }
            } else {
                TrainerNotCreatedScreen(viewModel)
            }
        }
    }
}

@Composable
fun AddNewStudentComposable(viewModel: TrainerCourseViewModel) {
    var studentName by rememberSaveable { mutableStateOf("") }
    var content = LocalContext.current

    LaunchedEffect(key1 = viewModel.studentAddedResponse) {
        if(viewModel.studentAddedResponse == Response.Success(true)) {
            Toast.makeText(content, "Študent bol pridaný!", Toast.LENGTH_LONG).show()
            viewModel.studentAddedResponse = Response.Loading
        }
    }

    Column(
        modifier = Modifier
            .padding(30.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
    ){
        Column(
            modifier = Modifier
                .padding(16.dp, 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Pozvi študenta do kurzu",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = "Meno študenta",
                style = MaterialTheme.typography.bodyLarge
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = studentName,
                onValueChange = { studentName = it },
                placeholder = { Text(text = "n.p. James Bond") },
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    Firebase.auth.uid?.let { viewModel.addStudentToCourse(trainerId = it, studentName) }
                }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pozvi študenta")
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerIsCreatedScreen(navController: NavController, courseId: MutableState<String>) {
    val showDialog = remember { mutableStateOf(false) }
    val isSheet = remember { mutableStateOf<Boolean>(false) }

    Column(
        Modifier.verticalScroll(rememberScrollState())
    ){

        Text("Trénerské menu",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 15.dp)
            )

        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxHeight()
        ) {

            Spacer(modifier = Modifier.padding(vertical = 20.dp))

            MenuOption(title = "Zobraz kurz",
                content = "Tu si môžeš pozrieť svoj kurz.",
                image =  "🎓",
                buttonContent = "Zobraz kurz") {
                navController.navigate(STUDENT_ZONE)
            }

            Spacer(modifier = Modifier.padding(vertical = 15.dp))

            MenuOption(title = "Pridaj úlohy",
                content = "Tu môžeš pridávať úlohy do kurzu.",
                image =  "👨🏻‍🎓",
                buttonContent = "Pridaj úlohy") {
                showDialog.value = true
            }

            Spacer(modifier = Modifier.padding(vertical = 15.dp))

            MenuOption(title = "Editor",
                content = "Tu môžeš upravovať pridané úlohy.",
                image =  "📝",
                buttonContent = "Edituj úlohy") {
                navController.navigate(EDIT_TRAINER)
            }

            Spacer(modifier = Modifier.padding(vertical = 40.dp))

            if (showDialog.value) {
                DialogAddTask(showDialog = showDialog,  isSheet = isSheet, navController =navController, courseId= courseId)
            }

            if(isSheet.value) {
                showDialog.value = false
                ModalBottomSheet(
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    onDismissRequest = {
                    isSheet.value = false
                }) {
                    TimeFormTask(courseId = courseId, isSheet = isSheet)
                }
            }

        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerNotCreatedScreen(viewModel: TrainerCourseViewModel) {
    val sheetState = rememberModalBottomSheetState()
    var checkCreateCourse by rememberSaveable { mutableStateOf(false) }

    if(checkCreateCourse) {
        ModalBottomSheet(
            onDismissRequest = { checkCreateCourse = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                CreateCourseSheet(viewModel)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxHeight()
            .offset(y = (-20).dp)
    ) {
        Spacer(modifier = Modifier.padding(vertical = 5.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .background(
                    colorResource(id = R.color.primary),
                    shape = RoundedCornerShape(10.dp)
                )
                .fillMaxWidth()

        ){

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Vytvor kurz",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.padding(vertical = 5.dp))

                    Box(modifier = Modifier
                        .width(190.dp)
                    ) {
                        Text(
                            text = "Vytvor kurz a pridávaj žiakom úlohy.",
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            overflow = TextOverflow.Clip,
                            maxLines = 3,
                            color = Color.White
                        )
                    }

                }

                Text(text = "👨🏼‍🏫",
                    fontSize = MaterialTheme.typography.displayLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White)

            }

            if(true) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        checkCreateCourse = true
                    }) {
                    Text(text = "Vytvor kurz",
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun CreateCourseSheet(
    viewModel: TrainerCourseViewModel
) {
    var courseName by rememberSaveable { mutableStateOf("") }
    var descriptionCourse by rememberSaveable { mutableStateOf("") }

    Column {
        Column(
            modifier = Modifier
                .padding(16.dp, 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Vytvor kurz",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Text(
                text = "Názov kurzu",
                style = MaterialTheme.typography.bodyLarge
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = courseName,
                onValueChange = { courseName = it },
                placeholder = { Text(text = "n.p. Kurz 1.A") },
            )

            Spacer(modifier = Modifier.padding(vertical = 15.dp))


            Text(
                text = "Popis kurzu",
                style = MaterialTheme.typography.bodyLarge
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = descriptionCourse,
                onValueChange = { descriptionCourse = it },
                placeholder = { Text(text = "Kurz pre základnú školu.") },
            )

            Spacer(modifier = Modifier.padding(vertical = 15.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    viewModel.createNewCourse(
                        TrainerCourse(
                            name = courseName,
                            description = descriptionCourse,
                            trainerId = Firebase.auth.uid,
                            invited = listOf(),
                            trainerChessTask = listOf(),
                            trainerDateTask = listOf(),
                            students = listOf()
                        ))
                },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Vytvor")
                }
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFormTask(
    isSheet: MutableState<Boolean>,
    courseId: MutableState<String>,
    viewModel: TrainerTasksViewModel = hiltViewModel(),
) {
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val timePickerState = rememberTimePickerState()
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0

            val startOfDayMillis = calendar.timeInMillis
            return utcTimeMillis >= startOfDayMillis
        }
    })


    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {

            Text(text = "Nastav úlohu študentom",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Názov úlohy") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Obsah úlohy") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))


            TimePickerView(timePickerState)

            Spacer(modifier = Modifier.height(20.dp))

            DatePickerView(datePickerState)

            Spacer(modifier = Modifier.height(5.dp))


            val context = LocalContext.current

            LaunchedEffect(key1 = viewModel.createNewDateTaskResponse) {
                if(viewModel.createNewDateTaskResponse == Response.Success(true)) {
                    Toast.makeText(context, "Úloha bola pridaná", Toast.LENGTH_SHORT).show()
                    viewModel.createNewDateTaskResponse = Response.Loading
                    isSheet.value = false
                }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    val selectedTimeInMillis = datePickerState.selectedDateMillis?.plus((timePickerState.hour * 60 * 60 * 1000 + timePickerState.minute * 60 * 1000))
                    val timestamp = com.google.firebase.Timestamp(selectedTimeInMillis?.div(1000) ?: 0, 0)

                    if(name.isNotEmpty() && content.isNotEmpty()) {
                        val trainerDateTask = TrainerDateTask(
                            name = name,
                            description = content,
                            createdBy = Firebase.auth.uid,
                            done = listOf(),
                            finish = timestamp,
                        )

                        viewModel.createNewTrainerDateTask(trainerDateTask, courseId.value)
                    } else {
                        Toast.makeText(context, "Pridaj meno/obsah!", Toast.LENGTH_SHORT).show()
                    }
                }) {
                Text("Ulož")
            }


            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerView(timePickerState: TimePickerState) {
    TimeInput(
        state = timePickerState
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerView(datePickerState: DatePickerState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DatePicker(
            state = datePickerState
        )
        Spacer(
            modifier = Modifier.height(
                12.dp
            )
        )
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(Date(millis))
}


@Composable
fun DialogAddTask(
    courseId: MutableState<String>,
    showDialog : MutableState < Boolean >,
    isSheet: MutableState<Boolean>,
    navController: NavController,
) {
    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        title = {
            Text(text = "Menu pre úlohy",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        },
        text = {
            Text(text = "Pridaj úlohu do kurzu.")
        },
        buttons = {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        isSheet.value = true
                    }
                ) {
                    Text("Pridaj slovnú úlohu")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("ADD_TO_COURSE/${courseId.value}")
                    }
                ) {
                    Text("Pridaj šachovú úlohu")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showDialog.value = false }
                ) {
                    Text("Zruš okno")
                }
            }
        }
    )
}

@Composable
fun MenuOption(title: String,
               content: String,
               image: String,
               buttonContent: String,
               onClick: () -> Unit
               ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .background(
                colorResource(id = R.color.primary),
                shape = RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
           // .clickable(onClick = onClick)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {

                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.padding(vertical = 5.dp))

                Text(
                    text = content,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Text(
                text = image,
                fontSize = MaterialTheme.typography.displayLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 5.dp)
            ,
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomEnd = 8.dp, bottomStart = 8.dp),
            onClick = onClick
            ) {
            Text(
                text = buttonContent,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

    }

}


@Preview
@Composable
fun TrainerScreen_Preview() {
    TrainerScreen(navController = rememberNavController())
}