package com.wisedaimyo.chesstraining

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.pieces.Bishop
import com.wisedaimyo.chesstraining.chess.model.pieces.King
import com.wisedaimyo.chesstraining.chess.model.pieces.Knight
import com.wisedaimyo.chesstraining.chess.model.pieces.Pawn
import com.wisedaimyo.chesstraining.chess.model.pieces.Piece
import com.wisedaimyo.chesstraining.chess.model.pieces.Queen
import com.wisedaimyo.chesstraining.chess.model.pieces.Rook
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece.BLACK
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece.WHITE
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun getImageResId(context: Context, imageName: String?): Int {
    return context.resources.getIdentifier(imageName, "drawable", context.packageName)
}

fun getDrawableName(context: Context, resId: Int): String? {
    return context.resources.getResourceEntryName(resId)
}


fun calculateEloRating(rating1: Double, rating2: Double, score1: Double): Double {
    val k = 32
    val expectedScore1 = 1 / (1 + Math.pow(10.0, (rating2 - rating1) / 400))
    return rating1 + k * (score1 - expectedScore1)
}

fun getPiecePromotion(input: String, isWhite: Boolean): String {
    if(isWhite) {
        if (input.contains("Queen")) {
            return "Q"
        }
        if (input.contains("Rook")) {
            return "R"
        }
        if (input.contains("Knight")) {
            return "N"
        }
        if (input.contains("Bishop")) {
            return "B"
        }
    } else {
        if (input.contains("Queen")) {
            return "q"
        }
        if (input.contains("Rook")) {
            return "r"
        }
        if (input.contains("Knight")) {
            return "n"
        }
        if (input.contains("Bishop")) {
            return "b"
        }
    }

    return ""
}

fun extractMove(input: String): Triple<String, String, String>? {
    val regex = Regex("(\\w{2})(\\w{2})(\\w+)?")
    val matchResult = regex.find(input)

    return matchResult?.let {
        val (from, to, promotion) = it.destructured
        Triple(from, to, promotion)
    }
}

fun extractMoveForTrainer(input: String): String? {
    val regex = Regex("(\\w{2})(\\w{2})*=?(\\w)?")
    val matchResult = regex.find(input)

    return matchResult?.let {
        val (from, to, promotion) = it.destructured
        "${from}${to}${promotion}"
    }
}

fun extractMovePromotion(input: String): Triple<String, String, String>? {
    val regex = Regex("bestmove (\\w{2})(\\w{2})(\\w+)?(?: ponder \\w{2}\\w{2})?")
    val matchResult = regex.find(input)

    return matchResult?.let {
        val (from, to, promotion) = it.destructured
        Triple(from, to, promotion ?: "")
    }
}

fun getPiece(pieceLetter: Char): Piece {
    var setPiece: SetPiece?
    if (pieceLetter.isUpperCase()) {
        setPiece = WHITE
    } else {
        setPiece = BLACK
    }

    if(pieceLetter.toUpperCase() == 'Q') return Queen(setPiece)
    if(pieceLetter.toUpperCase() == 'N') return Knight(setPiece)
    if(pieceLetter.toUpperCase() == 'B') return Bishop(setPiece)
    if(pieceLetter.toUpperCase() == 'R') return Rook(setPiece)

    return Queen(setPiece)
}


fun calculateRemainingTime(timestamp: Timestamp, hoursToAdd: Int): String {
    val instantTimestamp = Instant.ofEpochMilli(timestamp.seconds * 1000 + timestamp.nanoseconds / 1_000_000)
    val durationToAdd = java.time.Duration.ofHours(hoursToAdd.toLong())
    val newTimestamp = instantTimestamp.plus(durationToAdd)
    val currentTime = Instant.now()
    val remainingDuration = java.time.Duration.between(currentTime, newTimestamp)
    val days = remainingDuration.toDays()
    val hours = remainingDuration.toHours() % 24
    val minutes = remainingDuration.toMinutes() % 60
    val formattedTime = buildString {
        if (days > 0) append("$days ${if (days > 1) "dní" else "deň"} ")
        if (hours > 0) append("$hours ${if (hours > 1) "hodín" else "hodina"} ")
        if (minutes > 0) append("$minutes ${if (minutes > 1) "minút" else "minuta"}")
    }
    return formattedTime
}

fun calculateRemainingDuration(timestamp: Timestamp, hoursToAdd: Int): Long {
    val instantTimestamp = Instant.ofEpochMilli(timestamp.seconds * 1000 + timestamp.nanoseconds / 1_000_000)
    val durationToAdd = java.time.Duration.ofHours(hoursToAdd.toLong())
    val newTimestamp = instantTimestamp.plus(durationToAdd)
    val currentTime = Instant.now()
    return java.time.Duration.between(currentTime, newTimestamp).toMillis()
}


fun extractMove2(input: String): Pair<String, String>? {
    val regex = Regex("(\\w{2})(\\w{2})")
    val matchResult = regex.find(input)

    return matchResult?.let {
        val (from, to) = it.destructured
        Pair(from, to)
    }
}

fun extractEval(input: String): Pair<Float, String>? {
    val regex = """(-?\d+\.\d+)\s\((\w+)""".toRegex()
    val matchResult = regex.find(input)

    return matchResult?.let {
        val (number, string) = it.destructured
        val evaluation = number.toFloat()
        Pair(evaluation, string)
    }
}



fun stringToList(input: String): List<String> {
    return input.split(" ").filter { it.isNotEmpty() }
}

fun formatTimestampIntoReadableFormat(timestamp: Long): String {
    val timestampMillis = timestamp * 1000L
    val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), ZoneId.systemDefault())
    val now = LocalDateTime.now()
    val yesterday = now.minusDays(1)
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val relativeTime = when {
        localDateTime.toLocalDate() == now.toLocalDate() -> "Dnes"
        localDateTime.toLocalDate() == yesterday.toLocalDate() -> "Včera"
        else -> localDateTime.format(DateTimeFormatter.ofPattern("MMM d"))
    }

    val formattedTime = localDateTime.format(formatter)
    return "$relativeTime o $formattedTime"
}

fun generateFEN(input: List<String>, whiteMoves: Boolean): String {
    val board = Array(8) { Array(8) { ' ' } }
    for (item in input) {
        val col = item[0].toLowerCase() - 'a'
        val row = 7 - (item[1] - '1')
        val piece = if (item.length == 3) item[2] else if (item[0].isUpperCase()) 'P' else 'p'
        board[row][col] = piece
    }

    var fen = ""
    for (row in board) {
        var empty = 0
        for (piece in row) {
            if (piece == ' ') {
                empty++
            } else {
                if (empty > 0) {
                    fen += empty.toString()
                    empty = 0
                }
                fen += piece
            }
        }
        if (empty > 0) {
            fen += empty.toString()
        }
        fen += '/'
    }
    fen = fen.removeSuffix("/")

    fen += if(whiteMoves)
        " w - - 0 1"
    else
        " b - - 0 1"

    return fen
}

fun generateFEN(input: List<String>, whiteMoves: Boolean, castling: String, enpassant: String = "-"): String {
    val board = Array(8) { Array(8) { ' ' } }
    for (item in input) {
        val col = item[0].toLowerCase() - 'a'
        val row = 7 - (item[1] - '1')
        val piece = if (item.length == 3) item[2] else if (item[0].isUpperCase()) 'P' else 'p'
        board[row][col] = piece
    }

    var fen = ""
    for (row in board) {
        var empty = 0
        for (piece in row) {
            if (piece == ' ') {
                empty++
            } else {
                if (empty > 0) {
                    fen += empty.toString()
                    empty = 0
                }
                fen += piece
            }
        }
        if (empty > 0) {
            fen += empty.toString()
        }
        fen += '/'
    }
    fen = fen.removeSuffix("/")

    fen += if(whiteMoves)
        " w $castling $enpassant 0 1"
    else
        " b $castling $enpassant 0 1"

    return fen
}

fun canCastle(listOfMoves: List<Pair<String, String>>): String {
    var lastPartOfFen = "KQkq"

    if (listOfMoves.any { it.first == "a1" }) lastPartOfFen = lastPartOfFen.filterNot { it == 'Q'}
    if( listOfMoves.any { it.first == "h1" }) lastPartOfFen = lastPartOfFen.filterNot { it == 'K'}

    if (listOfMoves.any { it.first == "a8" }) lastPartOfFen = lastPartOfFen.filterNot { it == 'Q'}
    if (listOfMoves.any { it.first == "h8" }) lastPartOfFen = lastPartOfFen.filterNot { it == 'K'}

    if (listOfMoves.any { it.first == "e1" }) lastPartOfFen = lastPartOfFen.filterNot { it == 'K'}
    if (listOfMoves.any { it.first == "e1" }) lastPartOfFen = lastPartOfFen.filterNot { it == 'Q'}
    if(listOfMoves.any { it.first == "e8" }) lastPartOfFen = lastPartOfFen.filterNot { it == 'k' }
    if(listOfMoves.any { it.first == "e8" }) lastPartOfFen = lastPartOfFen.filterNot { it == 'q' }

    return lastPartOfFen
}

fun whoPlays(fen: String): String {
    val parts = fen.split(" ")
    return if (parts[1] == "w") "White" else "Black"
}


fun parseFEN(fen: String): Map<Position, Piece> {
    val parts = fen.split(" ")
    val board = parts[0].split("/")
    val result = mutableMapOf<Position, Piece>()

    for (i in board.indices) {
        var col = 0
        for (char in board[i]) {
            if (char.isDigit()) {
                col += char.toString().toInt()
            } else {
                val row = 7 - i
                val piece = char
                val position = Position.fromString("${(col + 'a'.toInt()).toChar()}${row + 1}")
                result[position] = when (piece) {
                    'k' -> King(BLACK)
                    'K' -> King(WHITE)
                    'q' -> Queen(BLACK)
                    'Q' -> Queen(WHITE)
                    'r' -> Rook(BLACK)
                    'R' -> Rook(WHITE)
                    'p' -> Pawn(BLACK)
                    'P' -> Pawn(WHITE)
                    'b' -> Bishop(BLACK)
                    'B' -> Bishop(WHITE)
                    'n' -> Knight(BLACK)
                    'N' -> Knight(WHITE)
                    else -> throw IllegalArgumentException("Unknown piece: $piece")
                }
                col++
            }
        }
    }
    return result
}

fun parseFENList(fen: String): List<String> {
    val parts = fen.split(" ")
    val board = parts[0].split("/")
    val isWhite = parts[1] == "w"
    val result = mutableListOf<String>()

    for (i in board.indices) {
        var col = 0
        for (char in board[i]) {
            if (char.isDigit()) {
                col += char.toString().toInt()
            } else {
                val row = 7 - i
                val piece = if (char.isUpperCase() == isWhite) char else char.toLowerCase()
                result.add("${(col + 'a'.toInt()).toChar()}${row + 1}$piece")
                col++
            }
        }
    }

    return result
}

class Utils {
    companion object {
        fun print(e: Exception) = Log.e(ContentValues.TAG, e.stackTraceToString())

        fun showMessage(
            context: Context,
            message: String?,
        ) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}