package com.example.the_games_app

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

enum class GameState {
    MENU, PLAYING, FINISHED
}


data class Particle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val id: Int,
    var alpha: Float = 1f
)

class GameViewModel : ViewModel() {

    var gameState by mutableStateOf(GameState.MENU)
        private set


    var score by mutableIntStateOf(0)
        private set

    var highScore by mutableIntStateOf(0)
        private set


    var strikes by mutableIntStateOf(0)
        private set


    var timeRemaining by mutableIntStateOf(15)
        private set


    var activeCircleIndex by mutableIntStateOf(-1)
        private set


    var isStrikeLocked by mutableStateOf(false)
        private set

    var lastTapTime by mutableLongStateOf(0L)
        private set

    // Combo system
    var combo by mutableIntStateOf(0)
        private set

    var multiplier by mutableIntStateOf(1)
        private set

    var showComboAnimation by mutableStateOf(false)
        private set


    var particles = mutableStateListOf<Particle>()
        private set


    private var spawnInterval by mutableLongStateOf(550L)


    private var timerJob: Job? = null
    private var circleSpawnJob: Job? = null
    private var difficultyRampJob: Job? = null
    private var particleUpdateJob: Job? = null


    private var highScoreManager: HighScoreManager? = null


    fun initialize(context: Context) {
        highScoreManager = HighScoreManager(context)
        viewModelScope.launch {
            highScore = highScoreManager?.highScore?.firstOrNull() ?: 0
        }
    }


    fun startGame() {

        score = 0
        strikes = 0
        timeRemaining = 20
        activeCircleIndex = -1
        isStrikeLocked = false
        combo = 0
        multiplier = 1
        spawnInterval = 550L
        particles.clear()
        showComboAnimation = false

        gameState = GameState.PLAYING


        startTimer()
        startCircleSpawning()
        startDifficultyRamp()
        startParticleUpdates()
    }


    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (timeRemaining > 0 && gameState == GameState.PLAYING) {
                delay(1000)
                timeRemaining--
            }
            if (gameState == GameState.PLAYING) {
                endGame()
            }
        }
    }
    private fun startCircleSpawning() {
        circleSpawnJob?.cancel()
        circleSpawnJob = viewModelScope.launch {
            delay(300) // Initial delay
            while (timeRemaining > 0 && gameState == GameState.PLAYING) {
                if (!isStrikeLocked) {
                    activeCircleIndex = (0..24).random()
                }
                delay(spawnInterval)
            }
        }
    }


    private fun startDifficultyRamp() {
        difficultyRampJob?.cancel()
        difficultyRampJob = viewModelScope.launch {
            while (timeRemaining > 0 && gameState == GameState.PLAYING) {
                delay(5000) // Every 5 seconds
                if (spawnInterval > 250L) {
                    spawnInterval = maxOf(250L, spawnInterval - 50L)
                }
            }
        }
    }


    private fun startParticleUpdates() {
        particleUpdateJob?.cancel()
        particleUpdateJob = viewModelScope.launch {
            while (gameState == GameState.PLAYING) {
                delay(16) // ~60 FPS
                updateParticles()
            }
        }
    }


    private fun updateParticles() {
        particles.removeAll { it.alpha <= 0f }
        particles.forEach { particle ->
            particle.alpha = maxOf(0f, particle.alpha - 0.02f)
        }
    }


    fun onCircleTap(index: Int, x: Float, y: Float) {
        if (isStrikeLocked || gameState != GameState.PLAYING) return

        lastTapTime = System.currentTimeMillis()

        if (index == activeCircleIndex) {

            combo++
            updateMultiplier()

            val points = 10 * multiplier
            score += points


            spawnParticles(x, y)


            if (combo >= 3) {
                showComboAnimation = true
                viewModelScope.launch {
                    delay(800)
                    showComboAnimation = false
                }
            }

            activeCircleIndex = -1
        } else {

            score = maxOf(0, score - 3) // Don't go negative
            strikes++
            combo = 0
            multiplier = 1

            if (strikes >= 3) {
                triggerStrikeLock()
            }
        }
    }


    private fun updateMultiplier() {
        multiplier = when {
            combo >= 7 -> 4  // 7+ hits: 4x
            combo >= 5 -> 3  // 5-6 hits: 3x
            combo >= 3 -> 2  // 3-4 hits: 2x
            else -> 1        // <3 hits: 1x
        }
    }


    private fun spawnParticles(x: Float, y: Float) {
        val particleCount = 12
        val baseId = System.currentTimeMillis().toInt()

        repeat(particleCount) { i ->
            val angle = (360f / particleCount) * i
            val radians = Math.toRadians(angle.toDouble())
            val speed = 3f + (Math.random() * 2).toFloat()

            val particle = Particle(
                x = x,
                y = y,
                velocityX = (Math.cos(radians) * speed).toFloat(),
                velocityY = (Math.sin(radians) * speed).toFloat(),
                id = baseId + i
            )
            particles.add(particle)
        }


        if (particles.size > 100) {
            particles.removeRange(0, particles.size - 100)
        }
    }


    private fun triggerStrikeLock() {
        isStrikeLocked = true
        strikes = 0
        combo = 0
        multiplier = 1

        viewModelScope.launch {
            delay(1000)
            isStrikeLocked = false
        }
    }


    private fun endGame() {
        // Cancel all jobs
        timerJob?.cancel()
        circleSpawnJob?.cancel()
        difficultyRampJob?.cancel()
        particleUpdateJob?.cancel()


        viewModelScope.launch {
            highScoreManager?.saveHighScore(score)
            highScore = highScoreManager?.highScore?.firstOrNull() ?: highScore
        }

        gameState = GameState.FINISHED
    }


    fun restartGame() {
        cancelAllJobs()
        startGame()
    }


    fun backToMenu() {
        cancelAllJobs()
        particles.clear()
        gameState = GameState.MENU
    }


    private fun cancelAllJobs() {
        timerJob?.cancel()
        circleSpawnJob?.cancel()
        difficultyRampJob?.cancel()
        particleUpdateJob?.cancel()
    }


    fun getRankText(): String {
        return when {
            score >= 400 -> "CHAOS MASTER"
            score >= 250 -> "REFLEX GOD"
            score >= 150 -> "SPEED DEMON"
            else -> "TRY HARDER 😅"
        }
    }


    fun isNewHighScore(): Boolean = score > highScore


    override fun onCleared() {
        super.onCleared()
        cancelAllJobs()
    }
}