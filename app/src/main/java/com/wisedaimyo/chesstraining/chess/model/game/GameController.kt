package com.wisedaimyo.chesstraining.chess.model.game

import com.wisedaimyo.chesstraining.chess.model.board.Position
import com.wisedaimyo.chesstraining.chess.model.board.Square
import com.wisedaimyo.chesstraining.chess.model.dataviz.DatasetVisualisation
import com.wisedaimyo.chesstraining.chess.model.game.preset.Preset
import com.wisedaimyo.chesstraining.chess.model.move.BoardMove
import com.wisedaimyo.chesstraining.chess.model.move.targetPositions
import com.wisedaimyo.chesstraining.chess.model.pieces.Piece
import com.wisedaimyo.chesstraining.chess.model.pieces.Queen
import com.wisedaimyo.chesstraining.chess.model.pieces.SetPiece
import com.wisedaimyo.chesstraining.chess.model.state.GameMetaInfo
import com.wisedaimyo.chesstraining.chess.model.state.GamePlayState
import com.wisedaimyo.chesstraining.chess.model.state.GameProgress
import com.wisedaimyo.chesstraining.chess.model.state.GameSnapshotState
import com.wisedaimyo.chesstraining.chess.model.state.Promotion
import com.wisedaimyo.chesstraining.chess.model.state.PromotionState
import java.lang.IllegalStateException


class GameController(
    val getGamePlayState: () -> GamePlayState,
    private val setGamePlayState: ((GamePlayState) -> Unit)? = null,
    preset: Preset? = null
) {
    init {
        preset?.let { applyPreset(it) }
    }

    private val gamePlayState: GamePlayState
        get() = getGamePlayState()

    val gameSnapshotState: GameSnapshotState
        get() = gamePlayState.gameState.currentSnapshotState

    val toMove: SetPiece
        get() = gameSnapshotState.toMove

    fun reset(
        gameSnapshotState: GameSnapshotState = GameSnapshotState(),
        gameMetaInfo: GameMetaInfo = GameMetaInfo.createWithDefaults()
    ) {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Reducer.Action.ResetTo(gameSnapshotState, gameMetaInfo))
        )
    }

    fun applyPreset(preset: Preset) {
        reset()
        preset.apply(this)
    }

    fun square(position: Position): Square =
        gameSnapshotState.board[position]

    private fun Position.hasOwnPiece() =
        square(this).hasPiece(gameSnapshotState.toMove)

    fun onClick(position: Position) {
        if (gameSnapshotState.gameProgress != GameProgress.IN_PROGRESS) return
        if (position.hasOwnPiece()) {
            toggleSelectPosition(position)
        } else if (canMoveTo(position)) {
            val selectedPosition = gamePlayState.uiState.selectedPosition
            requireNotNull(selectedPosition)
            applyMove(selectedPosition, position)
        }
    }

    private fun toggleSelectPosition(position: Position) {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Reducer.Action.ToggleSelectPosition(position))
        )
    }

    private fun canMoveTo(position: Position) =
        position in gamePlayState.uiState.possibleMoves().targetPositions()

    fun applyMove(from: Position, to: Position) {
        val boardMove = findBoardMove(from, to) ?: return
        applyMove(boardMove)
    }

    private fun applyMove(boardMove: BoardMove) {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Reducer.Action.ApplyMove(boardMove))
        )
    }

     fun findBoardMove(from: Position, to: Position): BoardMove? {
        val legalMoves = gameSnapshotState
            .legalMovesFrom(from)
            .filter { it.to == to }

        return when {
            legalMoves.isEmpty() -> {
                throw IllegalArgumentException("No legal moves exist between $from -> $to")
            }
            legalMoves.size == 1 -> {
                legalMoves.first()
            }
            legalMoves.all { it.consequence is Promotion } -> {
                handlePromotion(to, legalMoves)
            }
            else -> {
                throw IllegalStateException("Legal moves: $legalMoves")
            }
        }
    }

     private fun handlePromotion(at: Position, legalMoves: List<BoardMove>): BoardMove? {
        var promotionState = gamePlayState.promotionState
        if (setGamePlayState == null && promotionState == PromotionState.None) {
            promotionState = PromotionState.ContinueWith(Queen(gameSnapshotState.toMove))
        }

        when (val promotion = promotionState) {
            is PromotionState.None -> {
                setGamePlayState?.invoke(
                    Reducer(gamePlayState, Reducer.Action.RequestPromotion(at))
                )
            }
            is PromotionState.Await -> {
                throw IllegalStateException()
            }
            is PromotionState.ContinueWith -> {
                return legalMoves.find { move ->
                    (move.consequence as Promotion).let {
                        it.piece::class == promotion.piece::class
                    }
                }
            }
        }

        return null
    }

    fun onPromotionPieceSelected(piece: Piece) {
        val state = gamePlayState.promotionState
        if (state !is PromotionState.Await) error("Not in expected state: $state")
        val position = state.position
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Reducer.Action.PromoteTo(piece))
        )
        onClick(position)
    }

    fun setVisualisation(visualisation: DatasetVisualisation) {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Reducer.Action.SetVisualisation(visualisation))
        )
    }

    fun stepForward() {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Reducer.Action.StepForward)
        )
    }

    fun stepBackward() {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Reducer.Action.StepBackward)
        )
    }

    fun goToMove(index: Int) {
        setGamePlayState?.invoke(
            Reducer(gamePlayState, Reducer.Action.GoToMove(index))
        )
    }
}

