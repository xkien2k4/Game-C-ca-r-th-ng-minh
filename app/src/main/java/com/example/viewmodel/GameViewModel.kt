package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiDifficulty
import com.example.ai.CaroAiEngine
import com.example.data.TrainingPuzzle
import com.example.data.entity.SettingsEntity
import com.example.data.repository.CaroRepository
import com.example.data.repository.GameSummaryResult
import com.example.logic.GameLogic
import com.example.logic.GameRuleType
import com.example.logic.GameState
import com.example.logic.MoveRecord
import com.example.logic.PlayerPiece
import com.example.logic.WinningLine
import com.example.util.SoundEffectHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GameUiState(
    val mode: String = "AI", // "AI", "LOCAL", "PRACTICE"
    val difficulty: AiDifficulty = AiDifficulty.MEDIUM,
    val ruleType: GameRuleType = GameRuleType.STANDARD,
    val boardSize: Int = 15,
    val humanPiece: PlayerPiece = PlayerPiece.X,
    val playerXName: String = "Người chơi X",
    val playerOName: String = "Máy (AI)",
    val currentTurn: PlayerPiece = PlayerPiece.X,
    val gameState: GameState = GameState.IN_PROGRESS,
    val isAiThinking: Boolean = false,
    val lastMove: MoveRecord? = null,
    val winningLine: WinningLine? = null,
    val totalMoves: Int = 0,
    val timeLimitSeconds: Int = 30,
    val timeRemainingX: Int? = 30,
    val timeRemainingO: Int? = 30,
    val gameDurationSeconds: Long = 0,
    val remainingUndos: Int = 3,
    val remainingHints: Int = 3,
    val hintCell: Pair<Int, Int>? = null,
    val summaryResult: GameSummaryResult? = null,
    val isGameOverDialogVisible: Boolean = false,
    val winnerSymbol: String? = null,
    val currentPuzzle: TrainingPuzzle? = null,
    val practiceResultText: String? = null,
    val isPracticePassed: Boolean? = null
)

class GameViewModel(
    private val repository: CaroRepository,
    private val soundHelper: SoundEffectHelper
) : ViewModel() {

    private val aiEngine = CaroAiEngine()
    private var gameLogic = GameLogic(15, GameRuleType.STANDARD)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val boardState: Array<Array<PlayerPiece?>> get() = gameLogic.getBoardCopy()

    private var timerJob: Job? = null
    private var hintJob: Job? = null
    private var gameStartTime: Long = 0
    private var isGameSaved = false

    var currentSettings: SettingsEntity = SettingsEntity()
        private set

    init {
        viewModelScope.launch {
            repository.settingsFlow.collect { settings ->
                if (settings != null) {
                    currentSettings = settings
                }
            }
        }
    }

    fun startNewGame(
        mode: String = "AI",
        difficulty: AiDifficulty = AiDifficulty.MEDIUM,
        ruleType: GameRuleType = GameRuleType.STANDARD,
        boardSize: Int = 15,
        humanPiece: PlayerPiece = PlayerPiece.X,
        playerXName: String = "Người chơi X",
        playerOName: String = "Máy (AI)",
        timeLimitSeconds: Int = 30
    ) {
        timerJob?.cancel()
        hintJob?.cancel()
        gameLogic = GameLogic(boardSize, ruleType)
        gameLogic.resetGame(PlayerPiece.X)
        isGameSaved = false
        gameStartTime = System.currentTimeMillis()

        val initialTimer = if (timeLimitSeconds > 0) timeLimitSeconds else null
        val maxHints = currentSettings.maxHints

        _uiState.value = GameUiState(
            mode = mode,
            difficulty = difficulty,
            ruleType = ruleType,
            boardSize = boardSize,
            humanPiece = humanPiece,
            playerXName = playerXName,
            playerOName = playerOName,
            currentTurn = PlayerPiece.X,
            gameState = GameState.IN_PROGRESS,
            isAiThinking = false,
            lastMove = null,
            winningLine = null,
            totalMoves = 0,
            timeLimitSeconds = timeLimitSeconds,
            timeRemainingX = initialTimer,
            timeRemainingO = initialTimer,
            gameDurationSeconds = 0,
            remainingUndos = if (mode == "AI") 3 else if (currentSettings.undoEnabled) 999 else 0,
            remainingHints = maxHints,
            hintCell = null,
            summaryResult = null,
            isGameOverDialogVisible = false,
            winnerSymbol = null,
            currentPuzzle = null,
            practiceResultText = null,
            isPracticePassed = null
        )

        startTurnTimer()

        if (mode == "AI" && humanPiece == PlayerPiece.O) {
            triggerAiMove()
        }
    }

    fun startPracticePuzzle(puzzle: TrainingPuzzle) {
        timerJob?.cancel()
        hintJob?.cancel()
        gameLogic = GameLogic(puzzle.boardSize, GameRuleType.STANDARD)
        gameLogic.resetGame(puzzle.playerPiece)
        isGameSaved = false
        gameStartTime = System.currentTimeMillis()

        // Place initial preset puzzle pieces
        for ((r, c, p) in puzzle.initialPieces) {
            gameLogic.setPieceAt(r, c, p)
        }

        _uiState.value = GameUiState(
            mode = "PRACTICE",
            difficulty = AiDifficulty.MEDIUM,
            ruleType = GameRuleType.STANDARD,
            boardSize = puzzle.boardSize,
            humanPiece = puzzle.playerPiece,
            playerXName = "Kỳ Thủ",
            playerOName = "Thế Cờ #${puzzle.id}",
            currentTurn = puzzle.playerPiece,
            gameState = GameState.IN_PROGRESS,
            isAiThinking = false,
            lastMove = null,
            winningLine = null,
            totalMoves = 0,
            timeLimitSeconds = 0,
            timeRemainingX = null,
            timeRemainingO = null,
            remainingUndos = 3,
            remainingHints = 1,
            hintCell = null,
            summaryResult = null,
            isGameOverDialogVisible = false,
            winnerSymbol = null,
            currentPuzzle = puzzle,
            practiceResultText = null,
            isPracticePassed = null
        )
    }

    fun onCellClicked(row: Int, col: Int) {
        val state = _uiState.value
        if (state.gameState != GameState.IN_PROGRESS) return
        if (state.isAiThinking) return

        if (state.mode == "AI" && state.currentTurn != state.humanPiece) return

        if (!gameLogic.isValidMove(row, col)) return

        if (state.mode == "PRACTICE" && state.currentPuzzle != null) {
            // Check practice answer
            val puzzle = state.currentPuzzle
            val isCorrect = puzzle.correctMoves.any { it.first == row && it.second == col }
            gameLogic.makeMove(row, col)
            soundHelper.playMoveSound(currentSettings.soundEnabled, currentSettings.vibrationEnabled)
            updateGameStateAfterMove()

            if (isCorrect) {
                soundHelper.playWinSound(currentSettings.soundEnabled, currentSettings.vibrationEnabled)
                _uiState.value = _uiState.value.copy(
                    practiceResultText = "CHÍNH XÁC! ${puzzle.explanation} (+${puzzle.rewardExp} EXP)",
                    isPracticePassed = true
                )
                // Add exp
                viewModelScope.launch {
                    val p = repository.getPlayer()
                    var exp = p.experience + puzzle.rewardExp
                    var lvl = p.level
                    var needed = lvl * 500
                    while (exp >= needed) {
                        exp -= needed
                        lvl++
                        needed = lvl * 500
                    }
                    val updated = p.copy(experience = exp, level = lvl)
                    repository.updateProfile(updated.name, updated.avatar)
                }
            } else {
                soundHelper.playLossSound(currentSettings.soundEnabled, currentSettings.vibrationEnabled)
                _uiState.value = _uiState.value.copy(
                    practiceResultText = "CHƯA ĐÚNG! Nước đi này chưa tối ưu. Hãy thử lại!",
                    isPracticePassed = false
                )
            }
            return
        }

        val moved = gameLogic.makeMove(row, col)
        if (moved) {
            soundHelper.playMoveSound(currentSettings.soundEnabled, currentSettings.vibrationEnabled)
            updateGameStateAfterMove()

            if (gameLogic.gameState == GameState.IN_PROGRESS) {
                if (state.mode == "AI") {
                    triggerAiMove()
                } else {
                    resetTurnTimer()
                }
            } else {
                handleGameOver()
            }
        }
    }

    fun requestHint() {
        val state = _uiState.value
        if (state.gameState != GameState.IN_PROGRESS || state.isAiThinking) return
        if (state.remainingHints <= 0) return

        val boardCopy = gameLogic.getBoardCopy()
        val turnPiece = state.currentTurn

        viewModelScope.launch {
            val bestMove = withContext(Dispatchers.Default) {
                aiEngine.findBestMove(
                    board = boardCopy,
                    boardSize = state.boardSize,
                    aiPiece = turnPiece,
                    difficulty = AiDifficulty.MASTER,
                    ruleType = state.ruleType
                )
            }

            if (bestMove != null) {
                hintJob?.cancel()
                _uiState.value = state.copy(
                    hintCell = bestMove,
                    remainingHints = state.remainingHints - 1
                )
                soundHelper.playButtonClick(currentSettings.soundEnabled)

                // Auto remove highlight after 2 seconds
                hintJob = viewModelScope.launch {
                    delay(2000)
                    _uiState.value = _uiState.value.copy(hintCell = null)
                }
            }
        }
    }

    private fun triggerAiMove() {
        val state = _uiState.value
        _uiState.value = state.copy(isAiThinking = true)

        viewModelScope.launch {
            delay(350)

            val boardCopy = gameLogic.getBoardCopy()
            val aiPiece = if (state.humanPiece == PlayerPiece.X) PlayerPiece.O else PlayerPiece.X

            val bestMove = withContext(Dispatchers.Default) {
                aiEngine.findBestMove(
                    board = boardCopy,
                    boardSize = state.boardSize,
                    aiPiece = aiPiece,
                    difficulty = state.difficulty,
                    ruleType = state.ruleType
                )
            }

            if (bestMove != null && gameLogic.gameState == GameState.IN_PROGRESS) {
                gameLogic.makeMove(bestMove.first, bestMove.second)
                soundHelper.playMoveSound(currentSettings.soundEnabled, currentSettings.vibrationEnabled)
                updateGameStateAfterMove()

                if (gameLogic.gameState == GameState.IN_PROGRESS) {
                    resetTurnTimer()
                } else {
                    handleGameOver()
                }
            }

            _uiState.value = _uiState.value.copy(isAiThinking = false)
        }
    }

    fun undoMove() {
        val state = _uiState.value
        if (state.isAiThinking) return
        if (gameLogic.totalMoves == 0) return
        if (state.gameState != GameState.IN_PROGRESS) return
        if (state.remainingUndos <= 0) return

        val countToUndo = if (state.mode == "AI") 2 else 1
        val undone = gameLogic.undoMove(countToUndo)
        if (undone) {
            _uiState.value = state.copy(
                currentTurn = gameLogic.currentTurn,
                gameState = gameLogic.gameState,
                lastMove = gameLogic.lastMove,
                winningLine = null,
                totalMoves = gameLogic.totalMoves,
                remainingUndos = state.remainingUndos - 1,
                hintCell = null,
                isGameOverDialogVisible = false
            )
            resetTurnTimer()
        }
    }

    fun surrender() {
        val state = _uiState.value
        if (state.gameState != GameState.IN_PROGRESS) return

        timerJob?.cancel()
        hintJob?.cancel()
        val winner = if (state.currentTurn == PlayerPiece.X) PlayerPiece.O else PlayerPiece.X
        val winnerSymbol = winner.symbol

        _uiState.value = state.copy(
            gameState = if (winner == PlayerPiece.X) GameState.X_WON else GameState.O_WON,
            winnerSymbol = winnerSymbol
        )
        handleGameOver(customWinner = winnerSymbol)
    }

    private fun updateGameStateAfterMove() {
        val state = _uiState.value
        _uiState.value = state.copy(
            currentTurn = gameLogic.currentTurn,
            gameState = gameLogic.gameState,
            lastMove = gameLogic.lastMove,
            winningLine = gameLogic.winningLine,
            totalMoves = gameLogic.totalMoves,
            hintCell = null
        )
    }

    private fun startTurnTimer() {
        val limit = _uiState.value.timeLimitSeconds
        if (limit <= 0) return

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.gameState == GameState.IN_PROGRESS) {
                delay(1000)
                val state = _uiState.value
                val isTurnX = state.currentTurn == PlayerPiece.X

                val currentRemaining = if (isTurnX) state.timeRemainingX else state.timeRemainingO
                if (currentRemaining != null) {
                    val nextRemaining = currentRemaining - 1
                    if (nextRemaining <= 0) {
                        handleTimeout(timedOutPlayer = state.currentTurn)
                        break
                    } else {
                        _uiState.value = if (isTurnX) {
                            state.copy(timeRemainingX = nextRemaining)
                        } else {
                            state.copy(timeRemainingO = nextRemaining)
                        }
                    }
                }
            }
        }
    }

    private fun resetTurnTimer() {
        val limit = _uiState.value.timeLimitSeconds
        if (limit > 0) {
            _uiState.value = _uiState.value.copy(
                timeRemainingX = limit,
                timeRemainingO = limit
            )
        }
    }

    private fun handleTimeout(timedOutPlayer: PlayerPiece) {
        val state = _uiState.value
        val winner = timedOutPlayer.opposite()
        _uiState.value = state.copy(
            gameState = if (winner == PlayerPiece.X) GameState.X_WON else GameState.O_WON,
            winnerSymbol = winner.symbol
        )
        handleGameOver(customWinner = winner.symbol)
    }

    private fun handleGameOver(customWinner: String? = null) {
        if (isGameSaved) return
        isGameSaved = true
        timerJob?.cancel()
        hintJob?.cancel()

        val state = _uiState.value
        if (state.mode == "PRACTICE") return

        val duration = (System.currentTimeMillis() - gameStartTime) / 1000

        val winnerSymbol = customWinner ?: when (gameLogic.gameState) {
            GameState.X_WON -> "X"
            GameState.O_WON -> "O"
            GameState.DRAW -> "DRAW"
            else -> "DRAW"
        }

        val result = if (winnerSymbol == "DRAW") {
            "DRAW"
        } else if (state.mode == "LOCAL") {
            "WIN"
        } else if (winnerSymbol == state.humanPiece.symbol) {
            "WIN"
        } else {
            "LOSS"
        }

        if (result == "WIN") {
            soundHelper.playWinSound(currentSettings.soundEnabled, currentSettings.vibrationEnabled)
        } else if (result == "LOSS") {
            soundHelper.playLossSound(currentSettings.soundEnabled, currentSettings.vibrationEnabled)
        }

        viewModelScope.launch {
            val moveList = gameLogic.moves.map { Pair(it.row, it.col) }

            val summary = repository.recordGameFinished(
                playerX = state.playerXName,
                playerO = state.playerOName,
                mode = state.mode,
                boardSize = state.boardSize,
                winner = winnerSymbol,
                result = result,
                totalMoves = gameLogic.totalMoves,
                duration = duration,
                difficulty = state.difficulty.name,
                ruleType = state.ruleType.name,
                moveList = moveList,
                humanPlayerSymbol = state.humanPiece.symbol
            )

            _uiState.value = _uiState.value.copy(
                winnerSymbol = winnerSymbol,
                gameDurationSeconds = duration,
                summaryResult = summary,
                isGameOverDialogVisible = true
            )
        }
    }

    fun dismissGameOverDialog() {
        _uiState.value = _uiState.value.copy(isGameOverDialogVisible = false)
    }

    fun restartCurrentMatch() {
        val state = _uiState.value
        startNewGame(
            mode = state.mode,
            difficulty = state.difficulty,
            ruleType = state.ruleType,
            boardSize = state.boardSize,
            humanPiece = state.humanPiece,
            playerXName = state.playerXName,
            playerOName = state.playerOName,
            timeLimitSeconds = state.timeLimitSeconds
        )
    }
}
