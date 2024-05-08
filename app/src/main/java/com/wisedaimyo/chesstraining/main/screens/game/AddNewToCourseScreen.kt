package com.wisedaimyo.chesstraining.main.screens.game

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.board.Board
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.ui.BoardWithEnable
import com.wisedaimyo.chesstraining.extractMoveForTrainer
import com.wisedaimyo.chesstraining.getDrawableName
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.data.models.NAV_COURSE
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.TRAINER_ZONE
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerChessTask
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.TrainerCourseViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.TrainerTasksViewModel
import com.wisedaimyo.chesstraining.parseFEN
import com.wisedaimyo.chesstraining.whoPlays

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewToCourseScreen(
    viewModel: TrainerTasksViewModel = hiltViewModel(),
    courseId: String,
    navController: NavController,
) {
    val isFlipped = remember { mutableStateOf(false) }
    var isLoaded by rememberSaveable { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    var isFinished = rememberSaveable { mutableStateOf(false) }

    val fen = rememberSaveable { mutableStateOf("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1") }

    var currentValue = rememberSaveable { mutableStateOf("") }

    val isSheet = remember { mutableStateOf(false) }
    var scrollValue = rememberScrollState()

    val move = rememberSaveable { mutableIntStateOf(-1) }

    val gamePlayState = rememberSaveable { mutableStateOf( GamePlayState()) }
    val gameController = remember(gamePlayState.value) {
        GameController(
            getGamePlayState = { gamePlayState.value },
            setGamePlayState = { gamePlayState.value = it },
            preset = null
        )
    }

    val moves = rememberSaveable { mutableStateOf("") }
    val contentList = remember { mutableListOf<String>() }
    var fromToList = remember { mutableListOf<String>() }
    var fromToListSecond = remember { mutableListOf<String>() }

    LaunchedEffect(key1 = gamePlayState.value.gameState.states.size) {
        moves.value = ""
        fromToList = mutableListOf()
        fromToListSecond = mutableListOf()

        for (x in 0..<gamePlayState.value.gameState.states.size-1) {

            if(gamePlayState.value.gameState.states[x].move.toString() != "null")
                if(x%2==0) {
                    moves.value += "${(x/2)+1}. ${gamePlayState.value.gameState.states[x].move.toString()}"
                } else {
                    moves.value += gamePlayState.value.gameState.states[x].move.toString() + "  "
                }

            if(fromToList.size < x+1) {
                fromToList.add(
                    "${gamePlayState.value.gameState.states[x].move?.from.toString()}"
                    +
                    "${gamePlayState.value.gameState.states[x].move?.to.toString()}"
                    +
                    "${gamePlayState.value.gameState.states[x].move.toString()}"
                )

//                fromToListSecond.add(
//                    "${gamePlayState.value.gameState.states[x].move?.from.toString()}"
//                            +
//                    "${gamePlayState.value.gameState.states[x].move?.to}"
//                )
            } else {
                fromToList[x] = gamePlayState.value.gameState.states[x].move.toString()
            }

            if(contentList.size < x+1) {
                contentList.add("")
                if(x-1>0)
                contentList[x-1] = currentValue.value
            }
        }

        scrollValue.animateScrollTo(scrollValue.viewportSize)
        currentValue.value = ""
        move.value = gamePlayState.value.gameState.states.size-2

        println("TESTING $fromToList")

    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorResource(id = R.color.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BackButton {
                      navController.navigate(NAV_COURSE)
                }

                Icon(imageVector = Icons.Default.Add,
                    contentDescription = "Restart",
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .size(45.dp)
                        .clickable(onClick = {
                            isSheet.value = true
                        }
                        ))
            }

                Column {
                    BoardWithEnable(
                        gamePlayState = gamePlayState.value,
                        gameController = gameController,
                        isFlipped = isFlipped.value,
                        isBoardEnabled = true
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .background(colorResource(id = R.color.primary))
                            .horizontalScroll(scrollValue)
                    ) {
                        Text(text = moves.value,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(id = R.color.primary),
                            contentColor = Color.Black
                        )

                    ) {
                        Column(
                            Modifier
                                .height(150.dp)
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                                BasicTextField(
                                    value =  currentValue.value,
                                    onValueChange = { if(move.value != -1) currentValue.value = it else currentValue.value = "" },
                                    modifier = Modifier.background(Color.Transparent),
                                    decorationBox = { innerTextField ->
                                        if (currentValue.value.isEmpty()) {
                                            Text(text = if(move.intValue != -1) "Pridaj obsah k ťahu" else "Pohni s figúrkami",
                                                style = LocalTextStyle.current.copy(
                                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.Black.copy(alpha = 0.45f)
                                                ))
                                        }
                                        innerTextField()
                                    },
                                    textStyle = LocalTextStyle.current.copy(
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black
                                    )
                                )

                        }
                    }
                }

            EditGameBottomMenu(gameController, move = move, showDialog = showDialog , currentValue, contentList, isFlipped)

            if(showDialog.value) {
                EditGameMenu(showDialog, move = move,  fen= fen, gameController = gameController, fromToList = fromToList, contentList = contentList, moves = moves)
            }

        }


        if(isSheet.value) {
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                ),
                onDismissRequest = {
                    isSheet.value = false
                }) {

                ChessForm(
                    navController = navController,
                    courseId = courseId,
                    contentList = contentList,
                    fromToList = fromToList,
                    fen = fen
                )

            }
        }
    }
}


@Composable
fun EditGameBottomMenu(
    gameController: GameController,
    move: MutableIntState,
    showDialog: MutableState<Boolean>,
    currentValue: MutableState<String>,
    contentList: MutableList<String>,
    isFlipped: MutableState<Boolean>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(vertical = 15.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            modifier = Modifier
                .size(55.dp)
                .clickable(onClick = {
                    showDialog.value = true
                }
                )
        )

        Image(
            painter = if(isFlipped.value) painterResource(id = R.drawable.king_black) else painterResource(id =R.drawable.king_white),
            contentDescription = "",
            modifier = Modifier
                .size(55.dp)
                .clickable(onClick = {
                    isFlipped.value = !isFlipped.value
                }
                )
        )

        Icon(imageVector = Icons.Default.ArrowBack,
            contentDescription = "Backward",
            modifier = Modifier
                .size(55.dp)
                .clickable(onClick = {
                    if (contentList.size > 0) {
                        if (move.value > -1) {
                            contentList[move.value] = currentValue.value
                        }

                        gameController.stepBackward()

                        if (move.value >= 0)
                            move.value = move.value - 1

                        if (move.value > -1) {
                            currentValue.value = contentList[move.value]
                        } else {
                            currentValue.value = ""
                        }

                    }
                }
                )
        )

        Icon(imageVector = Icons.Default.ArrowForward,
            contentDescription = "Forward",
            modifier = Modifier
                .size(55.dp)
                .clickable(onClick = {
                    if (contentList.size - 1 > move.value) {

                        if (move.value > -1) {
                            contentList[move.value] = currentValue.value
                        }

                        gameController.stepForward()

                        if (move.value <= contentList.size - 1)
                            move.value = move.value + 1

                        if (move.value > -1) {
                            currentValue.value = contentList[move.value]
                        } else {
                            currentValue.value = ""
                        }

                    }
//                    if (moves != null) {
//                        if (move.value < moves.size - 1) {
//                            if (move.value != -1)
//                                list[move.intValue] =
//                                    Pair(list[move.intValue].first, currentValue.value)
//                            move.value += 1
//                            currentValue.value = list.get(move.intValue).second
//                            println("VALUE: ${move.value} - ")
//                        }
//                    }
                })
        )
    }
}




@Composable
fun EditGameMenu(
    showDialog: MutableState<Boolean>,
    move: MutableIntState,
    fromToList: MutableList<String>,
    contentList: MutableList<String>,
    fen: MutableState<String>,
    gameController: GameController,
    moves: MutableState<String>
) {
    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        title = {
            Text(text = "Menu",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        },
        text = {
            Text(text = "Zoznam možností.")
        },
        buttons = {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {

                Text(text = "Nahraj Fen")
                TextField(value = fen.value,
                    onValueChange = { fen.value = it })

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (fen.value.isNotEmpty()) {
                            val board = Board(
                                pieces = parseFEN(fen = fen.value)
                            )
                        gameController.reset(
                            GameSnapshotState(
                                board = board,
                                toMove = if(whoPlays(fen.value) == "White") SetPiece.WHITE else SetPiece.BLACK
                            ))

                            move.value = -1
                            fromToList.clear()
                            contentList.clear()

                        }
                    }
                ) {
                    Text("Nahraj FEN")
                }

                Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            fen.value = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
                            if (fen.value.isNotEmpty()) {
                                val board = Board(
                                    pieces = parseFEN(fen = fen.value)
                                )
                                gameController.reset(
                                    GameSnapshotState(
                                        board = board,
                                        toMove = if(whoPlays(fen.value) == "White") SetPiece.WHITE else SetPiece.BLACK
                                    ))
                            }

                            move.value = -1
                            fromToList.clear()
                            contentList.clear()

                        }
                    ) {
                        Text("Zresetuj")
                    }
                }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessForm(
    viewModel: TrainerTasksViewModel = hiltViewModel(),
    navController: NavController,
    courseId: String,
    fromToList: MutableList<String>,
    contentList: MutableList<String>,
    fen: MutableState<String>
) {
    var name by remember { mutableStateOf("") }
    var img by remember { mutableStateOf<Int>(-1) }
    var content by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isImg by remember { mutableStateOf(false) }

    var context = LocalContext.current

    val options = listOf("Otvorenia", "Stedná hra", "Koncovka", "Ostatné")
    val optionsDB = listOf("opening", "middle", "end", "other")
    var selectedIndex by remember { mutableStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp)
    ) {
        Text(text = "Pridaj do kurzu",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
            )

        
        OutlinedTextField(
            value = name,
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = { name = it },
            label = { Text("Názov") }
        )

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
            ,
            label = { Text("Obsah") }
        )

        Spacer(modifier = Modifier.height(18.dp))

        Box {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable(onClick = { expanded = true })
                    .border(BorderStroke(1.dp, Color.Black), RectangleShape)
                    .padding(5.dp)
                    .padding(horizontal = 15.dp)
            ) {
                Text(
                    options[selectedIndex]
                )
                Icon(imageVector = Icons.Default.Menu, contentDescription = "")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.Gray
                    )
            ) {
                options.forEachIndexed { index, s ->
                    DropdownMenuItem(onClick = {
                        selectedIndex = index
                        expanded = false
                    }) {
                        Text(text = s)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
           isImg = true
        }) {
            Text(text = "Vyber obrázok")
        }

        if(isImg) {
            ModalBottomSheet(onDismissRequest = { isImg = false }) {
                Column(
                    modifier = Modifier.padding(30.dp)
                ) {
                    val images = listOf(
                        R.drawable.course1,
                        R.drawable.course2,
                        R.drawable.course3,
                        R.drawable.course4,
                        R.drawable.course5,
                        R.drawable.course6,
                        R.drawable.course7,
                        R.drawable.course8,
                        R.drawable.course9
                    )

                    val rows = images.chunked(3)
                    rows.forEach { rowImages ->
                        Row() {
                            rowImages.forEach { imageResId ->
                                Image(
                                    painter = painterResource(id = imageResId),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(5.dp)
                                        .clickable(onClick = {
                                            img = imageResId
                                            isImg = false
                                        })
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))



        Button(onClick = {
            val moves = mutableListOf<String>()
            for(move in fromToList) {
                extractMoveForTrainer(move)?.let { moves.add(it) }
            }

            val trainerChessTask = TrainerChessTask(
                  name = name,
                  image = if(img!=-1) getDrawableName(context, img) else null,
                  type = optionsDB[selectedIndex],
                  createdBy = Firebase.auth.uid,
                  description = content,
                  fen = fen.value,
                  moves = moves,
                  done = listOf(),
                  describeMove = contentList.toList()
              )
            viewModel.createNewTrainerChessTask(trainerChessTask, courseId)
        }, modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)) {
            Text("Uložiť")
        }

        val context = LocalContext.current

        LaunchedEffect(key1 = viewModel.trainerDateTasksResponse) {
            if(viewModel.trainerDateTasksResponse == Response.Success(true)) {
                Toast.makeText(context, "Hra pridaná!", Toast.LENGTH_LONG).show()
                navController.navigate(TRAINER_ZONE)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}


