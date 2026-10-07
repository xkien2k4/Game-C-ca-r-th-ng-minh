package com.example.ai

import com.example.logic.GameRuleType
import com.example.logic.PlayerPiece
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

enum class AiDifficulty(val label: String, val description: String) {
    EASY("Dễ", "Phù hợp cho người mới làm quen"),
    MEDIUM("Trung bình", "Biết tấn công và phòng thủ cơ bản"),
    HARD("Khó", "Tính toán trước nhiều nước cờ (Minimax)"),
    MASTER("Cao thủ", "Thuật toán Alpha-Beta & Heuristic tối ưu");

    companion object {
        fun fromString(value: String): AiDifficulty {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}

class CaroAiEngine {

    companion object {
        // Evaluation heuristic weights
        private const val SCORE_FIVE = 10_000_000
        private const val SCORE_OPEN_FOUR = 1_000_000
        private const val SCORE_BLOCKED_FOUR = 100_000
        private const val SCORE_OPEN_THREE = 50_000
        private const val SCORE_BLOCKED_THREE = 5_000
        private const val SCORE_OPEN_TWO = 1_000
        private const val SCORE_BLOCKED_TWO = 100

        private val DIRECTIONS = listOf(
            Pair(0, 1),  // Horizontal
            Pair(1, 0),  // Vertical
            Pair(1, 1),  // Diagonal \
            Pair(1, -1)  // Anti-diagonal /
        )
    }

    /**
     * Calculates the best move for AI considering the game rule.
     */
    fun findBestMove(
        board: Array<Array<PlayerPiece?>>,
        boardSize: Int,
        aiPiece: PlayerPiece,
        difficulty: AiDifficulty,
        ruleType: GameRuleType = GameRuleType.STANDARD
    ): Pair<Int, Int>? {
        val humanPiece = aiPiece.opposite()
        val emptyCandidates = getCandidateMoves(board, boardSize)

        // If board is totally empty, play near center
        if (emptyCandidates.isEmpty()) {
            val center = boardSize / 2
            return Pair(center, center)
        }

        // 1. Check if AI can win immediately in 1 move
        for (cand in emptyCandidates) {
            if (checkWillWin(board, boardSize, cand.first, cand.second, aiPiece, ruleType)) {
                return cand
            }
        }

        // 2. Check if Opponent can win immediately in 1 move -> MUST BLOCK!
        for (cand in emptyCandidates) {
            if (checkWillWin(board, boardSize, cand.first, cand.second, humanPiece, ruleType)) {
                return cand
            }
        }

        return when (difficulty) {
            AiDifficulty.EASY -> findEasyMove(board, boardSize, aiPiece, humanPiece, emptyCandidates)
            AiDifficulty.MEDIUM -> findMediumMove(board, boardSize, aiPiece, humanPiece, emptyCandidates)
            AiDifficulty.HARD -> findMinimaxMove(board, boardSize, aiPiece, depth = 2, emptyCandidates)
            AiDifficulty.MASTER -> findMinimaxMove(board, boardSize, aiPiece, depth = 3, emptyCandidates)
        }
    }

    /**
     * EASY: Evaluates simple attack and defense, with a bit of randomness among top moves.
     */
    private fun findEasyMove(
        board: Array<Array<PlayerPiece?>>,
        boardSize: Int,
        aiPiece: PlayerPiece,
        humanPiece: PlayerPiece,
        candidates: List<Pair<Int, Int>>
    ): Pair<Int, Int> {
        val scoredMoves = candidates.map { move ->
            val aiScore = evaluateCell(board, boardSize, move.first, move.second, aiPiece)
            val humanScore = evaluateCell(board, boardSize, move.first, move.second, humanPiece)
            val centerDist = calculateCenterBias(move.first, move.second, boardSize)
            val total = aiScore + humanScore * 1.1 + centerDist
            Pair(move, total)
        }.sortedByDescending { it.second }

        // Pick among top 3
        val pickRange = min(3, scoredMoves.size)
        val chosenIndex = Random.nextInt(pickRange)
        return scoredMoves[chosenIndex].first
    }

    /**
     * MEDIUM: Balances strong positional defense & aggressive threat generation.
     */
    private fun findMediumMove(
        board: Array<Array<PlayerPiece?>>,
        boardSize: Int,
        aiPiece: PlayerPiece,
        humanPiece: PlayerPiece,
        candidates: List<Pair<Int, Int>>
    ): Pair<Int, Int> {
        var bestMove = candidates.first()
        var bestScore = Double.NEGATIVE_INFINITY

        for (cand in candidates) {
            val attackScore = evaluateCell(board, boardSize, cand.first, cand.second, aiPiece)
            val defenseScore = evaluateCell(board, boardSize, cand.first, cand.second, humanPiece)
            val centerDist = calculateCenterBias(cand.first, cand.second, boardSize)

            // Value defense higher if opponent has open three or four
            val weightDefense = if (defenseScore >= SCORE_OPEN_THREE) 1.5 else 1.1
            val totalScore = attackScore + (defenseScore * weightDefense) + centerDist

            if (totalScore > bestScore) {
                bestScore = totalScore
                bestMove = cand
            }
        }
        return bestMove
    }

    /**
     * HARD / MASTER: Minimax with Alpha-Beta Pruning.
     */
    private fun findMinimaxMove(
        board: Array<Array<PlayerPiece?>>,
        boardSize: Int,
        aiPiece: PlayerPiece,
        depth: Int,
        candidates: List<Pair<Int, Int>>
    ): Pair<Int, Int> {
        val humanPiece = aiPiece.opposite()

        // Pre-sort candidates to maximize alpha-beta cutoffs
        val sortedCandidates = candidates.map { move ->
            val aScore = evaluateCell(board, boardSize, move.first, move.second, aiPiece)
            val dScore = evaluateCell(board, boardSize, move.first, move.second, humanPiece)
            Pair(move, aScore + dScore * 1.2)
        }.sortedByDescending { it.second }
            .take(12) // Limit branching factor for instant response under 50ms
            .map { it.first }

        var bestMove = sortedCandidates.first()
        var alpha = Long.MIN_VALUE
        val beta = Long.MAX_VALUE

        for (cand in sortedCandidates) {
            board[cand.first][cand.second] = aiPiece
            val score = minimax(
                board = board,
                boardSize = boardSize,
                depth = depth - 1,
                alpha = alpha,
                beta = beta,
                isMaximizing = false,
                aiPiece = aiPiece
            )
            board[cand.first][cand.second] = null

            if (score > alpha) {
                alpha = score
                bestMove = cand
            }
        }

        return bestMove
    }

    private fun minimax(
        board: Array<Array<PlayerPiece?>>,
        boardSize: Int,
        depth: Int,
        alpha: Long,
        beta: Long,
        isMaximizing: Boolean,
        aiPiece: PlayerPiece
    ): Long {
        val humanPiece = aiPiece.opposite()

        if (depth == 0) {
            return evaluateBoard(board, boardSize, aiPiece)
        }

        val candidates = getCandidateMoves(board, boardSize)
            .take(8) // Bound moves per ply

        if (candidates.isEmpty()) return 0L

        var currentAlpha = alpha
        var currentBeta = beta

        if (isMaximizing) {
            var maxEval = Long.MIN_VALUE
            for (cand in candidates) {
                // Instant win check
                if (checkWillWin(board, boardSize, cand.first, cand.second, aiPiece)) {
                    return SCORE_FIVE.toLong() * (depth + 1)
                }

                board[cand.first][cand.second] = aiPiece
                val eval = minimax(board, boardSize, depth - 1, currentAlpha, currentBeta, false, aiPiece)
                board[cand.first][cand.second] = null

                maxEval = max(maxEval, eval)
                currentAlpha = max(currentAlpha, eval)
                if (currentBeta <= currentAlpha) break
            }
            return maxEval
        } else {
            var minEval = Long.MAX_VALUE
            for (cand in candidates) {
                // Instant win check for human
                if (checkWillWin(board, boardSize, cand.first, cand.second, humanPiece)) {
                    return -SCORE_FIVE.toLong() * (depth + 1)
                }

                board[cand.first][cand.second] = humanPiece
                val eval = minimax(board, boardSize, depth - 1, currentAlpha, currentBeta, true, aiPiece)
                board[cand.first][cand.second] = null

                minEval = min(minEval, eval)
                currentBeta = min(currentBeta, eval)
                if (currentBeta <= currentAlpha) break
            }
            return minEval
        }
    }

    /**
     * Evaluates tactical value of placing [piece] at (row, col).
     */
    private fun evaluateCell(
        board: Array<Array<PlayerPiece?>>,
        boardSize: Int,
        row: Int,
        col: Int,
        piece: PlayerPiece
    ): Double {
        var totalScore = 0.0
        val opponent = piece.opposite()

        for ((dr, dc) in DIRECTIONS) {
            var count = 1
            var openEnds = 0

            // Positive direction
            var r = row + dr
            var c = col + dc
            while (isInBounds(r, c, boardSize) && board[r][c] == piece) {
                count++
                r += dr
                c += dc
            }
            if (isInBounds(r, c, boardSize) && board[r][c] == null) {
                openEnds++
            }

            // Negative direction
            r = row - dr
            c = col - dc
            while (isInBounds(r, c, boardSize) && board[r][c] == piece) {
                count++
                r -= dr
                c -= dc
            }
            if (isInBounds(r, c, boardSize) && board[r][c] == null) {
                openEnds++
            }

            totalScore += when {
                count >= 5 -> SCORE_FIVE.toDouble()
                count == 4 && openEnds == 2 -> SCORE_OPEN_FOUR.toDouble()
                count == 4 && openEnds == 1 -> SCORE_BLOCKED_FOUR.toDouble()
                count == 3 && openEnds == 2 -> SCORE_OPEN_THREE.toDouble()
                count == 3 && openEnds == 1 -> SCORE_BLOCKED_THREE.toDouble()
                count == 2 && openEnds == 2 -> SCORE_OPEN_TWO.toDouble()
                count == 2 && openEnds == 1 -> SCORE_BLOCKED_TWO.toDouble()
                else -> 10.0
            }
        }

        return totalScore
    }

    /**
     * Evaluates overall board score from perspective of [aiPiece].
     */
    private fun evaluateBoard(
        board: Array<Array<PlayerPiece?>>,
        boardSize: Int,
        aiPiece: PlayerPiece
    ): Long {
        val humanPiece = aiPiece.opposite()
        var aiScore = 0L
        var humanScore = 0L

        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                val piece = board[r][c] ?: continue
                if (piece == aiPiece) {
                    aiScore += evaluateCell(board, boardSize, r, c, aiPiece).toLong()
                } else {
                    humanScore += evaluateCell(board, boardSize, r, c, humanPiece).toLong()
                }
            }
        }
        return aiScore - (humanScore * 1.2).toLong()
    }

    /**
     * Checks if placing [piece] at (row, col) immediately wins the game.
     */
    private fun checkWillWin(
        board: Array<Array<PlayerPiece?>>,
        boardSize: Int,
        row: Int,
        col: Int,
        piece: PlayerPiece,
        ruleType: GameRuleType = GameRuleType.STANDARD
    ): Boolean {
        val opponent = piece.opposite()

        for ((dr, dc) in DIRECTIONS) {
            var count = 1

            // Positive
            var rPos = row + dr
            var cPos = col + dc
            while (isInBounds(rPos, cPos, boardSize) && board[rPos][cPos] == piece) {
                count++
                rPos += dr
                cPos += dc
            }
            val posBlockedByOpponent = isInBounds(rPos, cPos, boardSize) && board[rPos][cPos] == opponent

            // Negative
            var rNeg = row - dr
            var cNeg = col - dc
            while (isInBounds(rNeg, cNeg, boardSize) && board[rNeg][cNeg] == piece) {
                count++
                rNeg -= dr
                cNeg -= dc
            }
            val negBlockedByOpponent = isInBounds(rNeg, cNeg, boardSize) && board[rNeg][cNeg] == opponent

            when (ruleType) {
                GameRuleType.STANDARD -> {
                    if (count >= 5) return true
                }
                GameRuleType.BLOCK_TWO_ENDS -> {
                    if (count == 5) {
                        if (!(posBlockedByOpponent && negBlockedByOpponent)) return true
                    } else if (count >= 6) {
                        return true
                    }
                }
                GameRuleType.EXACT_FIVE -> {
                    if (count == 5) return true
                }
            }
        }
        return false
    }

    /**
     * Returns list of empty cells within radius of 2 from existing pieces.
     * Keeps calculations fast and localized.
     */
    private fun getCandidateMoves(
        board: Array<Array<PlayerPiece?>>,
        boardSize: Int
    ): List<Pair<Int, Int>> {
        val candidates = mutableSetOf<Pair<Int, Int>>()
        var hasPiece = false

        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                if (board[r][c] != null) {
                    hasPiece = true
                    // Add all empty neighbors within radius 2
                    for (dr in -2..2) {
                        for (dc in -2..2) {
                            val nr = r + dr
                            val nc = c + dc
                            if (isInBounds(nr, nc, boardSize) && board[nr][nc] == null) {
                                candidates.add(Pair(nr, nc))
                            }
                        }
                    }
                }
            }
        }

        if (!hasPiece) {
            val center = boardSize / 2
            return listOf(Pair(center, center))
        }

        return candidates.toList()
    }

    private fun calculateCenterBias(row: Int, col: Int, boardSize: Int): Double {
        val center = boardSize / 2.0
        val distSq = (row - center) * (row - center) + (col - center) * (col - center)
        return max(0.0, 50.0 - distSq * 0.5)
    }

    private fun isInBounds(row: Int, col: Int, size: Int): Boolean {
        return row in 0 until size && col in 0 until size
    }
}
