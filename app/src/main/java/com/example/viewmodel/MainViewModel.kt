package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entity.AchievementEntity
import com.example.data.entity.GameEntity
import com.example.data.entity.PlayerEntity
import com.example.data.entity.SettingsEntity
import com.example.data.repository.CaroRepository
import com.example.data.repository.DetailedStatsSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: CaroRepository) : ViewModel() {

    val playerState: StateFlow<PlayerEntity?> = repository.playerFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val settingsState: StateFlow<SettingsEntity?> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val achievementsState: StateFlow<List<AchievementEntity>> = repository.achievementsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val gamesHistoryState: StateFlow<List<GameEntity>> = repository.allGamesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _detailedStats = MutableStateFlow<DetailedStatsSummary?>(null)
    val detailedStats: StateFlow<DetailedStatsSummary?> = _detailedStats.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getPlayer()
            repository.getSettings()
            refreshStats()
        }
    }

    fun refreshStats() {
        viewModelScope.launch {
            _detailedStats.value = repository.calculateDetailedStats()
        }
    }

    fun updateProfile(name: String, avatar: String) {
        viewModelScope.launch {
            repository.updateProfile(name.trim().ifEmpty { "Kỳ Thủ Caro" }, avatar)
        }
    }

    fun updateSettings(settings: SettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(settings)
        }
    }

    fun setTutorialCompleted() {
        viewModelScope.launch {
            repository.setTutorialCompleted()
        }
    }

    fun deleteGame(gameId: Long) {
        viewModelScope.launch {
            repository.deleteGame(gameId)
            refreshStats()
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.deleteAllHistory()
            refreshStats()
        }
    }

    suspend fun exportBackupJson(): String {
        return repository.exportBackupJson()
    }

    suspend fun restoreBackupJson(jsonString: String): Boolean {
        val success = repository.importRestoreJson(jsonString)
        if (success) {
            refreshStats()
        }
        return success
    }
}
