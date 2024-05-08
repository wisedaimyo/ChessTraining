package com.wisedaimyo.chesstraining.main.screens.game

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.wisedaimyo.chesstraining.R
import com.wisedaimyo.chesstraining.chess.model.board.Board
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.game.GameController
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.ui.BoardWithEnable
import com.wisedaimyo.chesstraining.extractMove
import com.wisedaimyo.chesstraining.extractMoveForTrainer
import com.wisedaimyo.chesstraining.getDrawableName
import com.wisedaimyo.chesstraining.getPiece
import com.wisedaimyo.chesstraining.main.components.BackButton
import com.wisedaimyo.chesstraining.main.data.models.NAV_COURSE
import com.wisedaimyo.chesstraining.main.data.models.Response
import com.wisedaimyo.chesstraining.main.data.models.TRAINER_ZONE
import com.wisedaimyo.chesstraining.main.data.models.model.ChessCourse
import com.wisedaimyo.chesstraining.main.data.models.model.TrainerChessTask
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.ChessCourseViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.EditViewModel
import com.wisedaimyo.chesstraining.main.data.models.viewmodel.TrainerTasksViewModel
import com.wisedaimyo.chesstraining.parseFEN
import com.wisedaimyo.chesstraining.whoPlays

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChessGameScreen(
    viewModelChessCourse: EditViewModel = hiltViewModel(),
    id: String = "",
    navController: NavController,
) {
    val isFlipped = remember { mutableStateOf(false) }
    val isFullLoad = remember { mutableStateOf(false) }
    var isLoaded by rememberSaveable { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    var isFinished = rememberSaveable { mutableStateOf(false) }

    val fen = rememberSaveable { mutableStateOf("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1") }

    var currentValue = rememberSaveable { mutableStateOf("") }

    val isSheet = remember { mutableStateOf(false) }
    val isRemove = remember { mutableStateOf(false) }
    var scrollValue = rememberScrollState()

    val move = rememberSaveable { mutableIntStateOf(-1) }
    val context = LocalContext.current

    val gamePlayState = rememberSaveable { mutableStateOf( GamePlayState()) }
    val gameController = remember(gamePlayState.value) {
        GameController(
            getGamePlayState = { gamePlayState.value },
            setGamePlayState = { gamePlayState.value = it },
            preset = null
        )
    }

    val moves = rememberSaveable { mutableStateOf("") }
    var contentList = remember { mutableListOf<String>() }
    var fromToList = remember { mutableListOf<String>() }
    var fromToListSecond = remember { mutableListOf<String>() }

    val chessGameTrainer = remember { mutableStateOf(TrainerChessTask()) }
    val description = rememberSaveable { mutableStateOf("") }

    if(!viewModelChessCourse.isLoaded)
        viewModelChessCourse.getTrainerChessTask(id)

    LaunchedEffect(key1 = viewModelChessCourse.chessTaskResponse) {
        viewModelChessCourse.isLoaded = true
        when (viewModelChessCourse.chessTaskResponse) {
            is Response.Success -> {
                chessGameTrainer.value = (viewModelChessCourse.chessTaskResponse as Response.Success<TrainerChessTask>).data!!

                if(chessGameTrainer.value.description != null) description.value = chessGameTrainer.value.description!!

                if (chessGameTrainer.value.fen != null) {
                    gameController.apply {
                        val board = Board(
                            pieces = parseFEN(chessGameTrainer.value.fen!!)
                        )
                        reset(
                            GameSnapshotState(
                                board = board,
                                toMove = if (whoPlays(chessGameTrainer.value.fen!!) == "White") SetPiece.WHITE else SetPiece.BLACK
                            )
                        )
                    }
                }

                if (!isLoaded) {
                    for(moveToExtract in chessGameTrainer.value.moves!!) {
                        val move = extractMove(moveToExtract)
                        val fromPosition = Position.fromString(move?.first ?: "")
                        val toPosition = Position.fromString(move?.second ?: "")
                        val promotion = move?.third

                        if (move != null) {
                            if (promotion != null) {
                                if (promotion.isNotEmpty()) {
                                    gameController.apply {
                                        gameController.applyMove(
                                            fromPosition,
                                            toPosition
                                        )
                                        gameController.apply {
                                            onPromotionPieceSelected(getPiece(promotion[0]))
                                        }
                                        gameController.applyMove(
                                            fromPosition,
                                            toPosition
                                        )
                                    }
                                } else {
                                    if (!move.first.equals("")) {
                                        gameController.applyMove(
                                            fromPosition,
                                            toPosition
                                        )
                                    }
                                }
                            }
                        }
                    }

                    for(ss in chessGameTrainer.value.describeMove!!)
                      contentList.add(ss)

                    isLoaded = true
                }
            }
            is Response.Loading -> {}
            else -> {println("TEST LOADED ERROR")}
        }
    }

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
                    gamePlayState.value.gameState.states[x].move?.from.toString()
                            +
                            gamePlayState.value.gameState.states[x].move?.to.toString()
                            +
                            gamePlayState.value.gameState.states[x].move.toString()
                )
            } else {
                fromToList[x] = gamePlayState.value.gameState.states[x].move.toString()
            }

            println("CONTENT LIST $contentList")
                if (contentList.size < x + 1) {
                        contentList.add("")
                    if (x - 1 > 0)
                        contentList[x - 1] = currentValue.value
                }

        }

        scrollValue.animateScrollTo(scrollValue.viewportSize)
        currentValue.value = ""
        move.value = gamePlayState.value.gameState.states.size-2

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

                Row(modifier = Modifier.padding(top = 5.dp)) {


                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .size(40.dp)
                            .clickable(onClick = {
                                isRemove.value = true
                            }
                            ))

                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))

                    Icon(imageVector = Icons.Default.Edit,
                        contentDescription = "ADD",
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .size(40.dp)
                            .clickable(onClick = {
                                isSheet.value = true
                            }
                            ))

                }
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

        LaunchedEffect(key1 = viewModelChessCourse.isRemovedResponse) {
            when(viewModelChessCourse.isRemovedResponse) {
                is Response.Success -> {
                    Toast.makeText(context, "Úloha bola zmazaná!", Toast.LENGTH_LONG).show()
                    navController.navigate(TRAINER_ZONE)
                }
                else -> { }
            }
        }

        if(isRemove.value) {
            AlertDialog(
                onDismissRequest = { isRemove.value = false },
                title = {
                    Text(text = "Naozaj chceš zmazať?",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                },
                buttons = { 
                    Column(
                        modifier = Modifier.padding(all = 8.dp)
                    ) {

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModelChessCourse.removeChessTask(id)
                            }) {
                            Text(text = "Áno")
                        }
                        
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { isRemove.value = false }) {
                            Text(text = "Nie")
                        }

                    }
                })
        }

        if(isSheet.value) {
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                ),
                onDismissRequest = {
                    isSheet.value = false
                }) {


                ChessFormUpdate(
                    navController = navController,
                    courseId = id,
                    contentList = contentList,
                    fromToList = fromToList,
                    fen = fen
                )

            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessFormUpdate(
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
        Text(text = "Uprav v kurze",
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
                type = optionsDB[selectedIndex],
                image = if(img!=-1) getDrawableName(context, img) else null,
                createdBy = com.google.firebase.ktx.Firebase.auth.uid,
                description = content,
                fen = fen.value,
                moves = moves,
                done = listOf(),
                describeMove = contentList.toList()
            )


            viewModel.updateTrainerChessTask(trainerChessTask, courseId)

        }, modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)) {
            Text("Uložiť")
        }

        val context = LocalContext.current

        LaunchedEffect(key1 = viewModel.isUpdated) {
            if(viewModel.isUpdated == Response.Success(true)) {
                Toast.makeText(context, "Hra zmenená!", Toast.LENGTH_LONG).show()
                navController.navigate(TRAINER_ZONE)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
    }
}