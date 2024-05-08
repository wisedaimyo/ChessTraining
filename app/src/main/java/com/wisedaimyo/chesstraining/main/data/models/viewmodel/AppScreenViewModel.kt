package com.wisedaimyo.chesstraining.main.data.models.viewmodel

import androidx.lifecycle.ViewModel
import com.wisedaimyo.chesstraining.main.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppScreenViewModel @Inject constructor(
    private val repo: AuthRepository
): ViewModel() {
    val playerName = if(repo.currentUser?.displayName.toString().isNotEmpty()) repo.currentUser?.displayName else "ANONYM"
    var playerUrl = if(repo.currentUser?.photoUrl == null) null else repo.currentUser?.photoUrl.toString()
    var elo = 1864
    val isTrainer: Boolean = true
}