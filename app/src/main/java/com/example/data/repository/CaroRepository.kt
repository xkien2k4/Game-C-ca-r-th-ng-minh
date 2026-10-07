package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.database.CaroDatabase
import com.example.data.entity.AchievementEntity
import com.example.data.entity.GameEntity
import com.example.data.entity.MoveEntity
import com.example.data.entity.PlayerEntity
import com.example.data.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

data class DifficultyStats(
    val difficulty: String,
    val totalGames: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val winRate: Int
)

data class DetailedStatsSummary(
    val totalGames: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val winRate: Int,
    val totalMoves: Int,
    val avgMovesPerGame: Float,
    val avgDurationSeconds: Long,
    val maxWinStreak: Int,
    val maxRating: Int,
    val difficultyStats: List<DifficultyStats>
)

class CaroRepository(private val database: CaroDatabase) {

    private val playerDao = database.playerDao()
    private val gameDao = database.gameDao()
    private val moveDao = database.moveDao()
    private val achievementDao = database.achievementDao()
    private val settingsDao = database.settingsDao()

    val playerFlow: Flow<PlayerEntity?> = playerDao.getPlayerFlow()

    suspend fun getPlayer(): PlayerEntity {
        var player = playerDao.getPlayer()
        if (player == null) {
            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            player = PlayerEntity(
                id = 1L,
                name = "Kỳ Thủ Caro",
                avatar = "MASTER",
                rating = 1000,
                maxRating = 1000,
                wins = 0,
                losses = 0,
                draws = 0,
                totalGames = 0,
                level = 1,
                experience = 0,
                winStreak = 0,
                maxWinStreak = 0,
                createdAt = dateStr
            )
            playerDao.insertPlayer(player)
        }
        return player
    }

    suspend fun updateProfile(name: String, avatar: String) {
        playerDao.updateProfile(name, avatar)
    }

    val allGamesFlow: Flow<List<GameEntity>> = gameDao.getAllGamesFlow()

    fun getMovesForGameFlow(gameId: Long): Flow<List<MoveEntity>> =
        moveDao.getMovesForGameFlow(gameId)

    suspend fun getGameById(id: Long): GameEntity? = gameDao.getGameById(id)

    suspend fun getMovesForGame(gameId: Long): List<MoveEntity> =
        moveDao.getMovesForGame(gameId)

    suspend fun deleteGame(gameId: Long) {
        database.withTransaction {
            moveDao.deleteMovesForGame(gameId)
            gameDao.deleteGameById(gameId)
        }
    }

    suspend fun deleteAllHistory() {
        database.withTransaction {
            moveDao.deleteAllMoves()
            gameDao.deleteAllGames()
        }
    }

    val achievementsFlow: Flow<List<AchievementEntity>> = achievementDao.getAllAchievementsFlow()

    val settingsFlow: Flow<SettingsEntity?> = settingsDao.getSettingsFlow()

    suspend fun getSettings(): SettingsEntity {
        var settings = settingsDao.getSettings()
        if (settings == null) {
            settings = SettingsEntity(
                id = 1L,
                soundEnabled = true,
                vibrationEnabled = true,
                darkMode = true,
                boardSize = 15,
                timerSeconds = 30,
                language = "vi",
                undoEnabled = true,
                maxHints = 3,
                ruleType = "STANDARD",
                pieceStyle = "MODERN",
                boardEffects = true,
                tutorialCompleted = false
            )
            settingsDao.insertOrUpdate(settings)
        }
        return settings
    }

    suspend fun updateSettings(settings: SettingsEntity) {
        settingsDao.insertOrUpdate(settings)
    }

    suspend fun setTutorialCompleted() {
        val current = getSettings()
        updateSettings(current.copy(tutorialCompleted = true))
    }

    suspend fun calculateDetailedStats(): DetailedStatsSummary {
        val player = getPlayer()
        val allGames = gameDao.getAllGamesSync()

        val totalGames = allGames.size
        val wins = allGames.count { it.result == "WIN" }
        val losses = allGames.count { it.result == "LOSS" }
        val draws = allGames.count { it.result == "DRAW" }
        val winRate = if (totalGames > 0) (wins.toFloat() / totalGames.toFloat() * 100).toInt() else 0

        val totalMoves = allGames.sumOf { it.totalMoves }
        val avgMoves = if (totalGames > 0) totalMoves.toFloat() / totalGames.toFloat() else 0f
        val totalDuration = allGames.sumOf { it.duration }
        val avgDuration = if (totalGames > 0) totalDuration / totalGames else 0L

        // Breakdown by AI Difficulty
        val diffList = listOf("EASY", "MEDIUM", "HARD", "MASTER")
        val diffStats = diffList.map { diff ->
            val gForDiff = allGames.filter { it.mode == "AI" && it.difficulty == diff }
            val gCount = gForDiff.size
            val gWins = gForDiff.count { it.result == "WIN" }
            val gLosses = gForDiff.count { it.result == "LOSS" }
            val gDraws = gForDiff.count { it.result == "DRAW" }
            val gWinRate = if (gCount > 0) (gWins.toFloat() / gCount.toFloat() * 100).toInt() else 0
            val diffLabel = when (diff) {
                "EASY" -> "AI Dễ"
                "MEDIUM" -> "AI Trung Bình"
                "HARD" -> "AI Khó"
                "MASTER" -> "AI Cao Thủ"
                else -> diff
            }
            DifficultyStats(
                difficulty = diffLabel,
                totalGames = gCount,
                wins = gWins,
                losses = gLosses,
                draws = gDraws,
                winRate = gWinRate
            )
        }

        return DetailedStatsSummary(
            totalGames = totalGames,
            wins = wins,
            losses = losses,
            draws = draws,
            winRate = winRate,
            totalMoves = totalMoves,
            avgMovesPerGame = avgMoves,
            avgDurationSeconds = avgDuration,
            maxWinStreak = player.maxWinStreak,
            maxRating = max(player.rating, player.maxRating),
            difficultyStats = diffStats
        )
    }

    suspend fun recordGameFinished(
        playerX: String,
        playerO: String,
        mode: String,
        boardSize: Int,
        winner: String,
        result: String,
        totalMoves: Int,
        duration: Long,
        difficulty: String,
        ruleType: String,
        moveList: List<Pair<Int, Int>>,
        humanPlayerSymbol: String
    ): GameSummaryResult {
        return database.withTransaction {
            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            val currentPlayer = getPlayer()

            var ratingChange = 0
            var expGained = 0

            when (result) {
                "WIN" -> {
                    ratingChange = when (difficulty) {
                        "EASY" -> 15
                        "MEDIUM" -> 25
                        "HARD" -> 35
                        "MASTER" -> 45
                        else -> 25
                    }
                    expGained = 100
                }
                "LOSS" -> {
                    ratingChange = -15
                    expGained = 25
                }
                "DRAW" -> {
                    ratingChange = 5
                    expGained = 50
                }
            }

            val newRating = (currentPlayer.rating + ratingChange).coerceAtLeast(0)
            val newMaxRating = max(currentPlayer.maxRating, newRating)
            val newTotalGames = currentPlayer.totalGames + 1
            val newWins = if (result == "WIN") currentPlayer.wins + 1 else currentPlayer.wins
            val newLosses = if (result == "LOSS") currentPlayer.losses + 1 else currentPlayer.losses
            val newDraws = if (result == "DRAW") currentPlayer.draws + 1 else currentPlayer.draws

            val newWinStreak = if (result == "WIN") currentPlayer.winStreak + 1 else 0
            val newMaxWinStreak = max(currentPlayer.maxWinStreak, newWinStreak)

            var totalExp = currentPlayer.experience + expGained
            var currentLevel = currentPlayer.level
            var expForNextLevel = currentLevel * 500

            while (totalExp >= expForNextLevel) {
                totalExp -= expForNextLevel
                currentLevel++
                expForNextLevel = currentLevel * 500
            }

            val updatedPlayer = currentPlayer.copy(
                rating = newRating,
                maxRating = newMaxRating,
                wins = newWins,
                losses = newLosses,
                draws = newDraws,
                totalGames = newTotalGames,
                level = currentLevel,
                experience = totalExp,
                winStreak = newWinStreak,
                maxWinStreak = newMaxWinStreak
            )
            playerDao.updatePlayer(updatedPlayer)

            val gameEntity = GameEntity(
                playerX = playerX,
                playerO = playerO,
                mode = mode,
                boardSize = boardSize,
                winner = winner,
                result = result,
                totalMoves = totalMoves,
                duration = duration,
                ratingChange = ratingChange,
                difficulty = difficulty,
                ruleType = ruleType,
                createdAt = dateStr
            )
            val gameId = gameDao.insertGame(gameEntity)

            val moveEntities = moveList.mapIndexed { index, (row, col) ->
                val symbol = if (index % 2 == 0) "X" else "O"
                MoveEntity(
                    gameId = gameId,
                    moveNumber = index + 1,
                    row = row,
                    col = col,
                    player = symbol,
                    createdAt = dateStr
                )
            }
            if (moveEntities.isNotEmpty()) {
                moveDao.insertMoves(moveEntities)
            }

            val newUnlockedAchievements = mutableListOf<String>()

            suspend fun checkAndUnlock(key: String) {
                val updatedCount = achievementDao.unlockAchievement(key, dateStr)
                if (updatedCount > 0) {
                    newUnlockedAchievements.add(key)
                }
            }

            if (newWins >= 1) checkAndUnlock("FIRST_WIN")
            if (newWins >= 5) checkAndUnlock("WIN_5_GAMES")
            if (newWins >= 10) checkAndUnlock("WIN_10_GAMES")
            if (newWinStreak >= 5) checkAndUnlock("WIN_STREAK_5")
            if (newWinStreak >= 10) checkAndUnlock("WIN_STREAK_10")
            if (newRating >= 1500) checkAndUnlock("REACH_1500_RATING")
            if (newRating >= 2000) checkAndUnlock("REACH_2000_RATING")
            if (result == "WIN" && totalMoves <= 15) checkAndUnlock("FAST_WIN")
            if (result == "WIN" && mode == "AI" && difficulty == "MASTER") checkAndUnlock("MASTER_DEFEATED")

            GameSummaryResult(
                gameId = gameId,
                ratingChange = ratingChange,
                newRating = newRating,
                expGained = expGained,
                newLevel = currentLevel,
                newExperience = totalExp,
                unlockedAchievements = newUnlockedAchievements
            )
        }
    }

    suspend fun exportBackupJson(): String {
        val player = getPlayer()
        val games = gameDao.getAllGamesSync()
        val moves = moveDao.getAllMovesSync()
        val achievements = achievementDao.getAllAchievementsSync()
        val settings = getSettings()

        val rootObj = JSONObject()
        rootObj.put("app", "CaroMaster")
        rootObj.put("version", 2)
        rootObj.put("exportTime", System.currentTimeMillis())

        // Player JSON
        val playerObj = JSONObject().apply {
            put("id", player.id)
            put("name", player.name)
            put("avatar", player.avatar)
            put("rating", player.rating)
            put("maxRating", player.maxRating)
            put("wins", player.wins)
            put("losses", player.losses)
            put("draws", player.draws)
            put("totalGames", player.totalGames)
            put("level", player.level)
            put("experience", player.experience)
            put("winStreak", player.winStreak)
            put("maxWinStreak", player.maxWinStreak)
            put("createdAt", player.createdAt)
        }
        rootObj.put("player", playerObj)

        // Games JSON
        val gamesArray = JSONArray()
        for (g in games) {
            val gObj = JSONObject().apply {
                put("id", g.id)
                put("playerX", g.playerX)
                put("playerO", g.playerO)
                put("mode", g.mode)
                put("boardSize", g.boardSize)
                put("winner", g.winner)
                put("result", g.result)
                put("totalMoves", g.totalMoves)
                put("duration", g.duration)
                put("ratingChange", g.ratingChange)
                put("difficulty", g.difficulty)
                put("ruleType", g.ruleType)
                put("createdAt", g.createdAt)
            }
            gamesArray.put(gObj)
        }
        rootObj.put("games", gamesArray)

        // Moves JSON
        val movesArray = JSONArray()
        for (m in moves) {
            val mObj = JSONObject().apply {
                put("id", m.id)
                put("gameId", m.gameId)
                put("moveNumber", m.moveNumber)
                put("row", m.row)
                put("col", m.col)
                put("player", m.player)
                put("createdAt", m.createdAt)
            }
            movesArray.put(mObj)
        }
        rootObj.put("moves", movesArray)

        // Achievements JSON
        val achArray = JSONArray()
        for (a in achievements) {
            val aObj = JSONObject().apply {
                put("id", a.id)
                put("achievementKey", a.achievementKey)
                put("title", a.title)
                put("description", a.description)
                put("icon", a.icon)
                put("unlocked", a.unlocked)
                put("unlockedAt", a.unlockedAt ?: "")
            }
            achArray.put(aObj)
        }
        rootObj.put("achievements", achArray)

        // Settings JSON
        val setObj = JSONObject().apply {
            put("id", settings.id)
            put("soundEnabled", settings.soundEnabled)
            put("vibrationEnabled", settings.vibrationEnabled)
            put("darkMode", settings.darkMode)
            put("boardSize", settings.boardSize)
            put("timerSeconds", settings.timerSeconds)
            put("language", settings.language)
            put("undoEnabled", settings.undoEnabled)
            put("maxHints", settings.maxHints)
            put("ruleType", settings.ruleType)
            put("pieceStyle", settings.pieceStyle)
            put("boardEffects", settings.boardEffects)
            put("tutorialCompleted", settings.tutorialCompleted)
        }
        rootObj.put("settings", setObj)

        return rootObj.toString(2)
    }

    suspend fun importRestoreJson(jsonString: String): Boolean {
        return try {
            val rootObj = JSONObject(jsonString)
            if (!rootObj.has("app") || rootObj.getString("app") != "CaroMaster") {
                return false
            }

            database.withTransaction {
                // Restore Player
                if (rootObj.has("player")) {
                    val pObj = rootObj.getJSONObject("player")
                    val player = PlayerEntity(
                        id = 1L,
                        name = pObj.optString("name", "Kỳ Thủ Caro"),
                        avatar = pObj.optString("avatar", "MASTER"),
                        rating = pObj.optInt("rating", 1000),
                        maxRating = pObj.optInt("maxRating", 1000),
                        wins = pObj.optInt("wins", 0),
                        losses = pObj.optInt("losses", 0),
                        draws = pObj.optInt("draws", 0),
                        totalGames = pObj.optInt("totalGames", 0),
                        level = pObj.optInt("level", 1),
                        experience = pObj.optInt("experience", 0),
                        winStreak = pObj.optInt("winStreak", 0),
                        maxWinStreak = pObj.optInt("maxWinStreak", 0),
                        createdAt = pObj.optString("createdAt", "")
                    )
                    playerDao.insertPlayer(player)
                }

                // Clear & Restore Games & Moves
                gameDao.deleteAllGames()
                moveDao.deleteAllMoves()

                if (rootObj.has("games")) {
                    val gArr = rootObj.getJSONArray("games")
                    val gamesList = mutableListOf<GameEntity>()
                    for (i in 0 until gArr.length()) {
                        val gObj = gArr.getJSONObject(i)
                        gamesList.add(
                            GameEntity(
                                id = gObj.optLong("id", 0L),
                                playerX = gObj.optString("playerX", "X"),
                                playerO = gObj.optString("playerO", "O"),
                                mode = gObj.optString("mode", "AI"),
                                boardSize = gObj.optInt("boardSize", 15),
                                winner = gObj.optString("winner", "X"),
                                result = gObj.optString("result", "WIN"),
                                totalMoves = gObj.optInt("totalMoves", 0),
                                duration = gObj.optLong("duration", 0L),
                                ratingChange = gObj.optInt("ratingChange", 0),
                                difficulty = gObj.optString("difficulty", "MEDIUM"),
                                ruleType = gObj.optString("ruleType", "STANDARD"),
                                createdAt = gObj.optString("createdAt", "")
                            )
                        )
                    }
                    if (gamesList.isNotEmpty()) {
                        gameDao.insertAllGames(gamesList)
                    }
                }

                if (rootObj.has("moves")) {
                    val mArr = rootObj.getJSONArray("moves")
                    val movesList = mutableListOf<MoveEntity>()
                    for (i in 0 until mArr.length()) {
                        val mObj = mArr.getJSONObject(i)
                        movesList.add(
                            MoveEntity(
                                id = mObj.optLong("id", 0L),
                                gameId = mObj.optLong("gameId", 0L),
                                moveNumber = mObj.optInt("moveNumber", 1),
                                row = mObj.optInt("row", 0),
                                col = mObj.optInt("col", 0),
                                player = mObj.optString("player", "X"),
                                createdAt = mObj.optString("createdAt", "")
                            )
                        )
                    }
                    if (movesList.isNotEmpty()) {
                        moveDao.insertMoves(movesList)
                    }
                }

                // Restore Achievements
                if (rootObj.has("achievements")) {
                    val aArr = rootObj.getJSONArray("achievements")
                    val achList = mutableListOf<AchievementEntity>()
                    for (i in 0 until aArr.length()) {
                        val aObj = aArr.getJSONObject(i)
                        achList.add(
                            AchievementEntity(
                                id = aObj.optLong("id", 0L),
                                achievementKey = aObj.getString("achievementKey"),
                                title = aObj.getString("title"),
                                description = aObj.getString("description"),
                                icon = aObj.optString("icon", "DANH_HIEU"),
                                unlocked = aObj.optBoolean("unlocked", false),
                                unlockedAt = if (aObj.has("unlockedAt") && aObj.getString("unlockedAt").isNotEmpty()) aObj.getString("unlockedAt") else null
                            )
                        )
                    }
                    if (achList.isNotEmpty()) {
                        achievementDao.insertOrUpdateAll(achList)
                    }
                }

                // Restore Settings
                if (rootObj.has("settings")) {
                    val sObj = rootObj.getJSONObject("settings")
                    val settings = SettingsEntity(
                        id = 1L,
                        soundEnabled = sObj.optBoolean("soundEnabled", true),
                        vibrationEnabled = sObj.optBoolean("vibrationEnabled", true),
                        darkMode = sObj.optBoolean("darkMode", true),
                        boardSize = sObj.optInt("boardSize", 15),
                        timerSeconds = sObj.optInt("timerSeconds", 30),
                        language = sObj.optString("language", "vi"),
                        undoEnabled = sObj.optBoolean("undoEnabled", true),
                        maxHints = sObj.optInt("maxHints", 3),
                        ruleType = sObj.optString("ruleType", "STANDARD"),
                        pieceStyle = sObj.optString("pieceStyle", "MODERN"),
                        boardEffects = sObj.optBoolean("boardEffects", true),
                        tutorialCompleted = sObj.optBoolean("tutorialCompleted", false)
                    )
                    settingsDao.insertOrUpdate(settings)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}

data class GameSummaryResult(
    val gameId: Long,
    val ratingChange: Int,
    val newRating: Int,
    val expGained: Int,
    val newLevel: Int,
    val newExperience: Int,
    val unlockedAchievements: List<String>
)
