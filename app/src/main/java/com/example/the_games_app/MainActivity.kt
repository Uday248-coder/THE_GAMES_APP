package com.example.the_games_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.the_games_app.ui.theme.ChaosTapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChaosTapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val viewModel: GameViewModel = viewModel()

                    // Initialize ViewModel with Context for DataStore
                    LaunchedEffect(Unit) {
                        viewModel.initialize(context)
                    }

                    // Navigate between screens based on game state
                    when (viewModel.gameState) {
                        GameState.MENU -> MenuScreen(
                            onStartGame = { viewModel.startGame() },
                            highScore = viewModel.highScore
                        )
                        GameState.PLAYING -> GameScreen(viewModel)
                        GameState.FINISHED -> ResultsScreen(viewModel)
                    }
                }
            }
        }
    }
}