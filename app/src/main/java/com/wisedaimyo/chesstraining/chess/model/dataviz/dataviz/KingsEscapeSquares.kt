package com.wisedaimyo.chesstraining.chess.model.dataviz.dataviz

import androidx.compose.ui.graphics.Color
import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.dataviz.Datapoint
import com.wisedaimyo.chesstraining.chess.model.dataviz.DatasetVisualisation
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import com.wisedaimyo.chesstraining.chess.model.pieces.King
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.model.state.Move
import com.wisedaimyo.chesstraining.chess.model.state.MoveIntention

abstract class KingsEscapeSquares(
    private val set: SetPiece,
    private val colorScale: Pair<Color, Color>
) : DatasetVisualisation {

    override val minValue: Int = 0

    override val maxValue: Int = 2

    private val CACHE_KEY = this::class.java

    private data class EscapeSquares(
        val degree1: List<Position>,
        val degree2: List<Position>,
    )

    private data class EscapeSquare(
        val isDegree1: Boolean,
        val isDegree2: Boolean,
    ) {
        val isAnyEscape: Boolean =
            isDegree1 || isDegree2
    }

    override fun dataPointAt(
        position: Position,
        state: GameSnapshotState,
        cache: MutableMap<Any, Any>,
    ): Datapoint? {

        if (cache[CACHE_KEY] == null) {
            val kingSquare = state.board.find<King>(set).firstOrNull() ?: return null
            val degree1 = state.legalMovesFrom(kingSquare.position).map { it.to }
            val degree2 = degree1
                .flatMap { immediate ->
                    state.derivePseudoGameState(
                        boardMove = BoardMove(
                            move = Move(
                                piece = kingSquare.piece!!,
                                intent = MoveIntention(from = kingSquare.position, immediate)
                            )
                        )
                    ).legalMovesFrom(immediate).map { it.to }
            }

            cache[CACHE_KEY] = EscapeSquares(
                degree1 = degree1,
                degree2 = degree2,
            )
        }

        val escapeSquares = cache[CACHE_KEY] as EscapeSquares

        return valueAt(position, escapeSquares).let { escapeSquare ->
            Datapoint(
                value = when {
                    escapeSquare.isDegree1 -> 2
                    escapeSquare.isDegree2 -> 1
                    else -> 0
                },
                label = null,
                colorScale = when {
                    escapeSquare.isAnyEscape -> colorScale
                    else -> Color.Unspecified to Color.Unspecified
                },
            )
        }
    }

    private fun valueAt(position: Position, escapeSquares: EscapeSquares): EscapeSquare =
        EscapeSquare(
            isDegree1 = escapeSquares.degree1.contains(position),
            isDegree2 = escapeSquares.degree2.contains(position),
        )
}
