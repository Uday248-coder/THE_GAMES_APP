package com.example.the_games_app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore extension
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chaos_tap_prefs")


class HighScoreManager(private val context: Context) {

    private companion object {
        val HIGH_SCORE_KEY = intPreferencesKey("high_score")
    }


    val highScore: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[HIGH_SCORE_KEY] ?: 0
    }


    suspend fun saveHighScore(score: Int) {
        context.dataStore.edit { prefs ->
            val currentHigh = prefs[HIGH_SCORE_KEY] ?: 0
            if (score > currentHigh) {
                prefs[HIGH_SCORE_KEY] = score
            }
        }
    }
}