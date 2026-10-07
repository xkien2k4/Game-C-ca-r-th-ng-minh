package com.example.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1L,
    val name: String = "Kỳ Thủ Caro",
    val avatar: String = "MASTER",
    val rating: Int = 1000,
    @ColumnInfo(name = "max_rating")
    val maxRating: Int = 1000,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    @ColumnInfo(name = "total_games")
    val totalGames: Int = 0,
    val level: Int = 1,
    val experience: Int = 0,
    @ColumnInfo(name = "win_streak")
    val winStreak: Int = 0,
    @ColumnInfo(name = "max_win_streak")
    val maxWinStreak: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: String = ""
)

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "player_x")
    val playerX: String,
    @ColumnInfo(name = "player_o")
    val playerO: String,
    val mode: String, // "AI", "LOCAL", "PRACTICE"
    @ColumnInfo(name = "board_size")
    val boardSize: Int = 15,
    val winner: String, // "X", "O", "DRAW"
    val result: String, // "WIN", "LOSS", "DRAW"
    @ColumnInfo(name = "total_moves")
    val totalMoves: Int,
    val duration: Long, // in seconds
    @ColumnInfo(name = "rating_change")
    val ratingChange: Int = 0,
    @ColumnInfo(name = "difficulty")
    val difficulty: String = "MEDIUM", // EASY, MEDIUM, HARD, MASTER
    @ColumnInfo(name = "rule_type")
    val ruleType: String = "STANDARD", // STANDARD or BLOCK_TWO_ENDS
    @ColumnInfo(name = "created_at")
    val createdAt: String
)

@Entity(
    tableName = "moves",
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["game_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["game_id"])]
)
data class MoveEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "game_id")
    val gameId: Long,
    @ColumnInfo(name = "move_number")
    val moveNumber: Int,
    val row: Int,
    val col: Int,
    val player: String, // "X" or "O"
    @ColumnInfo(name = "created_at")
    val createdAt: String = ""
)

@Entity(
    tableName = "achievements",
    indices = [Index(value = ["achievement_key"], unique = true)]
)
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "achievement_key")
    val achievementKey: String,
    val title: String,
    val description: String,
    val icon: String = "DANH_HIEU",
    val unlocked: Boolean = false,
    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: String? = null
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val id: Long = 1L,
    @ColumnInfo(name = "sound_enabled")
    val soundEnabled: Boolean = true,
    @ColumnInfo(name = "vibration_enabled")
    val vibrationEnabled: Boolean = true,
    @ColumnInfo(name = "dark_mode")
    val darkMode: Boolean = true,
    @ColumnInfo(name = "board_size")
    val boardSize: Int = 15,
    @ColumnInfo(name = "timer_seconds")
    val timerSeconds: Int = 30,
    val language: String = "vi",
    @ColumnInfo(name = "undo_enabled")
    val undoEnabled: Boolean = true,
    @ColumnInfo(name = "max_hints")
    val maxHints: Int = 3,
    @ColumnInfo(name = "rule_type")
    val ruleType: String = "STANDARD", // STANDARD, BLOCK_TWO_ENDS
    @ColumnInfo(name = "piece_style")
    val pieceStyle: String = "MODERN", // CLASSIC, MODERN
    @ColumnInfo(name = "board_effects")
    val boardEffects: Boolean = true,
    @ColumnInfo(name = "tutorial_completed")
    val tutorialCompleted: Boolean = false
)
