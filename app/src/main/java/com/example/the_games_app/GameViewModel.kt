package com.example.the_games_app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class GameState {
    PLAYING, FINISHED
}

class GameViewModel : ViewModel() {
    var gameState by mutableStateOf(GameState.PLAYING)
        private set

    var score by mutableIntStateOf(0)
        private set

    var strikes by mutableIntStateOf(0)
        private set

    var timeRemaining by mutableIntStateOf(20)
        private set

    var activeCircleIndex by mutableIntStateOf(-1)
        private set

    var isStrikeLocked by mutableStateOf(false)
        private set

    var lastTapTime by mutableLongStateOf(0L)
        private set

    private var gameJob: Job? = null
    private var timerJob: Job? = null
    private var circleSpawnJob: Job? = null

    init {
        startGame()
    }

    private fun startGame() {
        score = 0
        strikes = 0
        timeRemaining = 20
        activeCircleIndex = -1
        isStrikeLocked = false
        gameState = GameState.PLAYING

        startTimer()
        startCircleSpawning()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (timeRemaining > 0) {
                delay(1000)
                timeRemaining--
            }
            endGame()
        }
    }

    private fun startCircleSpawning() {
        circleSpawnJob?.cancel()
        circleSpawnJob = viewModelScope.launch {
            delay(300) // Initial delay
            while (timeRemaining > 0) {
                if (!isStrikeLocked) {
                    activeCircleIndex = (0..24).random()
                }
                delay(550)
            }
        }
    }

    fun onCircleTap(index: Int) {
        if (isStrikeLocked || gameState != GameState.PLAYING) return

        lastTapTime = System.currentTimeMillis()

        if (index == activeCircleIndex) {
            // Correct tap
            score += 10
            activeCircleIndex = -1
        } else {
            // Wrong tap
            score -= 3
            strikes++

            if (strikes >= 3) {
                triggerStrikeLock()
            }
        }
    }

    private fun triggerStrikeLock() {
        isStrikeLocked = true
        strikes = 0 // Reset strikes after lock

        viewModelScope.launch {
            delay(1000)
            isStrikeLocked = false
        }
    }

    private fun endGame() {
        timerJob?.cancel()
        circleSpawnJob?.cancel()
        gameState = GameState.FINISHED
    }

    fun restartGame() {
        gameJob?.cancel()
        timerJob?.cancel()
        circleSpawnJob?.cancel()
        startGame()
    }

    fun getRankText(): String {
        return when {
            score >= 400 -> "CHAOS MASTER"
            score >= 250 -> "REFLEX GOD"
            score >= 150 -> "SPEED DEMON"
            else -> "TRY HARDER 😅"
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameJob?.cancel()
        timerJob?.cancel()
        circleSpawnJob?.cancel()
    }
}