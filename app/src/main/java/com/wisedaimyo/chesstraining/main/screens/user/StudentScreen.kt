package com.wisedaimyo.chesstraining.main.screens.user

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.getImageResId
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.components.Custom_Card
import com.wisedaimyo.chesstraining.main.components.ShimmeringTaskForStudent
import com.wisedaimyo.chesstraining.main.components.TaskForStudent
import com.wisedaimyo.chesstraining.main.data.models.LEARN_GAME
import com.wisedaimyo.chesstraining.main.data.models.NAV_HOME
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.StudentScreenViewModel
import com.wisedaimyo.chesstraining.main.screens.main.ShimmeringBoxes
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date

@SuppressLint("SuspiciousIndentation")
@Composable
fun StudentScreen(
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
            BackButton {
                navController.navigate(NAV_HOME)
            }

            if (viewModel.isLoaded) {
                StudentIsInCourseScreen(navController= navController,viewModel)
            } else {
                Firebase.auth.uid?.let { viewModel.getInvitationsForStudent(it) }
                StudentIsNotInCourseScreen(viewModel ,viewModel.invitations.isNotEmpty())
            }
        }
    }
}

@Composable
fun StudentTasks(viewModel: StudentScreenViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
       modifier = Modifier
           .fillMaxWidth()
           .fillMaxHeight()
           .verticalScroll(rememberScrollState())
    ) {
        Text("Tvoje zadania",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            )

        LaunchedEffect(key1 = viewModel.addStudentToTheTaskWithIdResponse) {
            when(viewModel.addStudentToTheTaskWithIdResponse) {
                is Response.Success -> {
                    viewModel.getCourseDataForStudent()
                    viewModel.addStudentToTheTaskWithIdResponse = Response.Loading
                }
                else -> { }
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 10.dp))


            if (viewModel.dateTasks.isNotEmpty()) {
                for(task in viewModel.dateTasks) {
                    if(LocalDateTime.now().isBefore(task.second.finish?.toDate()?.toInstant()?.atZone(
                            ZoneId.systemDefault())?.toLocalDateTime()))
                    Column(
                        Modifier.clickable {
                            Firebase.auth.uid?.let { viewModel.addStudentToTheDateTask(it, task.first) }
                        }
                    ) {
                        task.second.finish?.let {
                            TaskForStudent(
                                title = task.second.name ?: "",
                                content = task.second.description ?: "",
                                time = it.toDate(),
                                isFinished = task.second.done?.contains(Firebase.auth.uid) == true
                            )
                        }
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

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentIsInCourseScreen(
    navController: NavController,
    viewModel: StudentScreenViewModel
) {
    val sheetState = rememberModalBottomSheetState()
    val sheetState2 = rememberModalBottomSheetState()
    var checkTasks by rememberSaveable { mutableStateOf(false) }
    var checkSettings by rememberSaveable { mutableStateOf(false) }
    var hasTasks by rememberSaveable { mutableStateOf(false) }

    Column {
        if(checkTasks) {
            ModalBottomSheet(
                onDismissRequest = { checkTasks = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                  StudentTasks(viewModel)
                }
            }
        }

        LaunchedEffect(key1 = viewModel.signOutResponse) {
            when(viewModel.signOutResponse) {
                is Response.Success -> { navController.navigate(NAV_HOME) }
                else -> { }
            }
        }

        if(checkSettings) {
            ModalBottomSheet(
                onDismissRequest = { checkSettings = false },
                sheetState = sheetState2
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Button(onClick = {
                            if(viewModel.currentUser.isTrainer == false)
                              Firebase.auth.uid?.let { viewModel.signOutStudent(it) }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Nastavenia",
                                modifier = Modifier
                                    .size(30.dp)
                            )
                            Text(text = "Odísť zo skupiny")
                        }

                    }
                }
            }
        }


        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ){

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
                    Column{
                        if (viewModel.dateTasks.isNotEmpty()) {
                         Row(
                             horizontalArrangement = Arrangement.SpaceBetween,
                             modifier = Modifier.fillMaxWidth()
                         ) {
                             Column {
                                 Text(
                                     text = "Úlohy od trénera",
                                     fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                     fontWeight = FontWeight.ExtraBold,
                                     color = Color.White
                                 )

                                 Spacer(modifier = Modifier.padding(vertical = 5.dp))

                                 Text(
                                     text = "Boli ti pridelené úlohy.",
                                     fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                     fontWeight = FontWeight.ExtraBold,
                                     color = Color.White
                                 )
                             }

                             Text(text = "🧗",
                                 fontSize = MaterialTheme.typography.displayLarge.fontSize,
                                 fontWeight = FontWeight.ExtraBold,
                                 color = Color.White)

                         }

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = 5.dp),
                                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomEnd = 8.dp, bottomStart = 8.dp),
                                onClick = {
                                    checkTasks = true
                                }) {
                                Text(text = "Pozri úlohy",
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        } else {
                            Text(
                                text = "Úlohy od trénera",
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.padding(vertical = 5.dp))

                            Text(
                                text = "Zatial nemáš žiadne úlohy.",
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Text(text = "🧗",
                        fontSize = MaterialTheme.typography.displayLarge.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White)

                }

            }

            Spacer(modifier = Modifier.padding(vertical = 15.dp))

            Column{
                TopMenuSectionTrainer("Otvorenia", viewModel.openings.size)

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    if(viewModel.openings.isNotEmpty()) {
                        for (task in viewModel.openings) {
                                task.second.name?.let {
                                    Firebase.auth.uid?.let { it1 ->
                                        task.second.done?.contains(
                                            it1
                                        ) ?: false
                                    }?.let { it2 ->
                                        Custom_Card(
                                            image = getImageResId(context = LocalContext.current, task.second.image ?: "ic_launcher_foreground"),
                                            isFinihed = it2,
                                            name = it
                                        ) {
                                            navController.navigate("$LEARN_GAME/${task.first}/true")
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

                    if(viewModel.middle.isNotEmpty()) {
                        for (task in viewModel.middle) {
                            task.second.name?.let {
                                Firebase.auth.uid?.let { it1 ->
                                    task.second.done?.contains(
                                        it1
                                    ) ?: false
                                }?.let { it2 ->
                                    Custom_Card(
                                        image = getImageResId(context = LocalContext.current, task.second.image ?: "ic_launcher_foreground"),
                                        isFinihed = it2,
                                        name = it
                                    ) {
                                        navController.navigate("$LEARN_GAME/${task.first}/true")
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
                    if(viewModel.end.isNotEmpty()) {
                        for (task in viewModel.end) {
                            task.second.name?.let {
                                Firebase.auth.uid?.let { it1 ->
                                    task.second.done?.contains(
                                        it1
                                    ) ?: false
                                }?.let { it2 ->
                                    Custom_Card(
                                        image = getImageResId(context = LocalContext.current, task.second.image ?: "ic_launcher_foreground"),
                                        isFinihed = it2,
                                        name = it
                                    ) {
                                        navController.navigate("$LEARN_GAME/${task.first}/true")
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
                    if(viewModel.other.isNotEmpty()) {
                        for (task in viewModel.other) {
                            task.second.name?.let {
                                Firebase.auth.uid?.let { it1 ->
                                    task.second.done?.contains(
                                        it1
                                    ) ?: false
                                }?.let { it2 ->
                                    Custom_Card(
                                        image = getImageResId(context = LocalContext.current, task.second.image ?: "ic_launcher_foreground"),
                                        isFinihed = it2,
                                        name = it
                                    ) {
                                        navController.navigate("$LEARN_GAME/${task.first}/true")
                                    }
                                }
                            }
                        }
                    } else {
                        ShimmeringBoxes()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(
                vertical = 30.dp
            ))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {
                        checkSettings = true
                    })
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Nastavenia",
                    modifier = Modifier
                        .size(30.dp)
                )
                Text(text = "Nastavenia")
            }

            Spacer(modifier = Modifier.padding(
                vertical = 15.dp
            ))

        }

    }
}

@Composable
fun TopMenuSectionTrainer(name: String, howMany: Int){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            name,
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 13.dp)
        )

        Text(text = "$howMany",
            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 13.dp)

        )

    }
}

@Composable
fun StudentIsNotInCourseScreen(viewModel: StudentScreenViewModel, isInvitation: Boolean) {
    Column {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Text(text = "Momentálne nie si v žiadnom kurze.",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

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
                    Column{
                        if (!isInvitation) {
                            Text(
                                text = "Pozvánky od trénera",
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Momentálne nemáš žiadne pozvánky.",
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Pozvánky od trénera",
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Bola ti poslaná pozvánka.",
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Text(text = "👨🏼‍🏫",
                        fontSize = MaterialTheme.typography.displayLarge.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,)

                }

                val showInvitationsDialog = remember { mutableStateOf(false) }

                if(isInvitation) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showInvitationsDialog.value = true
                        }) {
                        Text(text = "Pozri pozvánky")
                    }
                }

                if (showInvitationsDialog.value) {
                   for( inv in viewModel.invitations )
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .background(Color.White)
                            .padding(30.dp)
                    ) {
                        Text(
                            text = "Pozvánky",
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                        )

                        Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .background(Color.LightGray)
                                ,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                inv.second.name?.let {
                                    Text(text = it,
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                        fontWeight = FontWeight.ExtraBold,
                                    )
                                }

                                Button(onClick = {
                                    Firebase.auth.uid?.let {
                                        viewModel.acceptInvitation(inv.first,
                                            it
                                        )
                                    }
                                }) {
                                    Text(text = "Prijať",
                                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                        fontWeight = FontWeight.ExtraBold,)
                                }
                            }

                    }

                }


            }
        }
    }
}






