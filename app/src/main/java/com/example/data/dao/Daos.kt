package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.entity.AchievementEntity
import com.example.data.entity.GameEntity
import com.example.data.entity.MoveEntity
import com.example.data.entity.PlayerEntity
import com.example.data.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE id = 1 LIMIT 1")
    fun getPlayerFlow(): Flow<PlayerEntity?>

    @Query("SELECT * FROM players WHERE id = 1 LIMIT 1")
    suspend fun getPlayer(): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Query("UPDATE players SET name = :name, avatar = :avatar WHERE id = 1")
    suspend fun updateProfile(name: String, avatar: String)
}

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY id DESC")
    fun getAllGamesFlow(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games ORDER BY id DESC")
    suspend fun getAllGamesSync(): List<GameEntity>

    @Query("SELECT * FROM games WHERE id = :id LIMIT 1")
    suspend fun getGameById(id: Long): GameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGames(games: List<GameEntity>)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteGameById(id: Long)

    @Query("DELETE FROM games")
    suspend fun deleteAllGames()

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getGamesCount(): Int

    @Query("SELECT * FROM games WHERE mode = :mode ORDER BY id DESC")
    fun getGamesByModeFlow(mode: String): Flow<List<GameEntity>>
}

@Dao
interface MoveDao {
    @Query("SELECT * FROM moves WHERE game_id = :gameId ORDER BY move_number ASC")
    suspend fun getMovesForGame(gameId: Long): List<MoveEntity>

    @Query("SELECT * FROM moves WHERE game_id = :gameId ORDER BY move_number ASC")
    fun getMovesForGameFlow(gameId: Long): Flow<List<MoveEntity>>

    @Query("SELECT * FROM moves ORDER BY id ASC")
    suspend fun getAllMovesSync(): List<MoveEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoves(moves: List<MoveEntity>)

    @Query("DELETE FROM moves WHERE game_id = :gameId")
    suspend fun deleteMovesForGame(gameId: Long)

    @Query("DELETE FROM moves")
    suspend fun deleteAllMoves()
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY id ASC")
    fun getAllAchievementsFlow(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements ORDER BY id ASC")
    suspend fun getAllAchievementsSync(): List<AchievementEntity>

    @Query("SELECT * FROM achievements WHERE achievement_key = :key LIMIT 1")
    suspend fun getAchievementByKey(key: String): AchievementEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("UPDATE achievements SET unlocked = 1, unlocked_at = :unlockedAt WHERE achievement_key = :key AND unlocked = 0")
    suspend fun unlockAchievement(key: String, unlockedAt: String): Int

    @Query("SELECT COUNT(*) FROM achievements WHERE unlocked = 1")
    fun getUnlockedCountFlow(): Flow<Int>
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<SettingsEntity?>

    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: SettingsEntity)
}
