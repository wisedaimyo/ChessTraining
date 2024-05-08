package com.wisedaimyo.chesstraining.API.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisedaimyo.chesstraining.API.data.NextMoveRepository
import com.wisedaimyo.chesstraining.API.data.Result
import com.wisedaimyo.chesstraining.API.data.model.NextMove
import com.wisedaimyo.chesstraining.extractEval

import com.wisedaimyo.chesstraining.extractMove
import com.wisedaimyo.chesstraining.extractMove2
import com.wisedaimyo.chesstraining.extractMovePromotion
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NextMoveViewModel(
    private val nextMoveRepository: NextMoveRepository
): ViewModel() {

    private var _nextMove = MutableStateFlow<NextMove>(NextMove("", false))
    val nextMove = _nextMove.asStateFlow()

    private var _fromTo = MutableStateFlow<Pair<String, String>>(Pair("",""))
    val fromTo = _fromTo.asStateFlow()

    private var _promotion = MutableStateFlow<Triple<String, String, String>>(Triple("","",""))
    val promotion = _promotion.asStateFlow()

    private var _evaluation = MutableStateFlow<Float>(0.0f)
    val evaluation = _evaluation.asStateFlow()

    private val _showErrorToastChannel = Channel<Boolean>()
    val showErrorToastChannel = _showErrorToastChannel.receiveAsFlow()

    init {
        getNextMove("", 0, "")
    }

    fun getEvaluation(fenNotation: String, depth: Int = 13, mode: String = "eval") {
        viewModelScope.launch {
            nextMoveRepository.getNextMove(fenNotation, depth, mode).collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _showErrorToastChannel.send(true)
                    }

                    is Result.Success -> {
                        result.data?.let {nextMove ->

                            val extract = extractEval(nextMove.data)
                            if (extract != null) {
                                _evaluation.update {
                                    extract.first
                                }
                            }else {
                                if(nextMove.data.contains("White"))
                                    _evaluation.update {
                                         10.0.toFloat()
                                    }
                                if(nextMove.data.contains("Black"))
                                    _evaluation.update {
                                        -10.0.toFloat()
                                    }

                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun getNextMove(fenNotation: String, depth: Int, mode: String = "bestmove") {
        viewModelScope.launch {
            nextMoveRepository.getNextMove(fenNotation, depth, mode).collectLatest { result ->
                when (result) {
                    is Result.Error -> {
                        _showErrorToastChannel.send(true)
                    }

                    is Result.Success -> {
                        result.data?.let {nextMove ->
                            _nextMove.update { nextMove }
                                val extract2 = extractMovePromotion(nextMove.data)
                                if(extract2 != null) {
                                    if(extract2.third.isNotEmpty())
                                     _promotion.update { Triple(extract2.first, extract2.second, extract2.third) }
                                    else
                                     _fromTo.update { Pair(extract2.first, extract2.second) }
                                }
                            }
                            }
                    else -> {}
                }
            }
        }
    }
}
