import android.content.Context
import com.wisedaimyo.chesstraining.R
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

data class ChessPuzzle(
    val PuzzleId: String = "",
    val FEN: String= "",
    val Moves: String= "",
    val Rating: String= "",
    val RatingDeviation: String= "",
    val Popularity: String= "",
    val NbPlays: String= "",
    val Themes: String= "",
    val GameUrl: String= "",
    val OpeningTags: String = "",
)

object ChessPuzzleCsvParser {
    fun parsePuzzlesFromCsv(context: Context, minRating: Int, maxRating: Int): ChessPuzzle? {
        val inputStream = context.resources.openRawResource(R.raw.puzzles)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var selectedPuzzle: ChessPuzzle? = null
        var count = 0

        reader.useLines { lines ->
            lines.drop(1).forEach { line ->
                val fields = line.split(",")
                val rating = fields[3].toInt()
                if (rating in minRating..maxRating) {
                    val puzzle = ChessPuzzle(
                        PuzzleId = fields[0],
                        FEN = fields[1],
                        Moves = fields[2],
                        Rating = rating.toString(),
                        RatingDeviation = fields[4],
                        Popularity = fields[5],
                        NbPlays = fields[6],
                        Themes = fields[7],
                        GameUrl = fields[8],
                        OpeningTags = fields[9]
                    )
                    count++
                    if (Random.nextInt(count) == 0) {
                        selectedPuzzle = puzzle
                    }
                }
            }
        }

        return selectedPuzzle
    }
}
