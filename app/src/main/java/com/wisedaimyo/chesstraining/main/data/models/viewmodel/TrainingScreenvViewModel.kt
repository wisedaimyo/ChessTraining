package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.lifecycle.ViewModel
import com.wisedaimyo.chesstraining.main.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrainingScreenViewModel @Inject constructor(
    private val repo: AuthRepository
): ViewModel() {
    var playerUrl = repo.currentUser?.photoUrl.toString()
    val isTrainer: Boolean = true
}