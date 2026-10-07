package com.example.logic

enum class PlayerPiece {
    X, O;

    val symbol: String get() = name

    fun opposite(): PlayerPiece = if (this == X) O else X
}

enum class GameState {
    IN_PROGRESS,
    X_WON,
    O_WON,
    DRAW
}

/**
 * Các luật chơi cờ Caro & Gomoku tiêu chuẩn và đầy đủ:
 * 1. STANDARD (Gomoku Tự Do): Đạt từ 5 quân liên tiếp trở lên (ngang, dọc, chéo) là thắng ngay lập tức.
 * 2. BLOCK_TWO_ENDS (Caro Truyền Thống Việt Nam): Đạt 5 quân liên tiếp và không bị đối phương chặn CẢ HAI ĐẦU.
 *    (Nếu chỉ bị chặn 1 đầu hoặc chạm mép bàn cờ thì vẫn tính là CHIẾN THẮNG).
 * 3. EXACT_FIVE (Gomoku Chuẩn 5 Quân): Phải đạt đúng 5 quân liên tiếp mới thắng (hàng 6 quân trở lên không tính thắng).
 */
enum class GameRuleType(val label: String, val description: String) {
    STANDARD("Caro Tự Do (Gomoku)", "Từ 5 quân liên tiếp trở lên là chiến thắng ngay lập tức"),
    BLOCK_TWO_ENDS("Caro Việt Nam (Chặn 2 Đầu)", "5 quân liên tiếp không bị đối phương chặn cả 2 đầu"),
    EXACT_FIVE("Gomoku Chuẩn (Đúng 5 Quân)", "Phải đạt đúng 5 quân liên tiếp (6 quân không thắng)");

    companion object {
        fun fromString(value: String): GameRuleType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: STANDARD
        }
    }
}

data class MoveRecord(
    val row: Int,
    val col: Int,
    val piece: PlayerPiece,
    val moveIndex: Int
)

data class WinningLine(
    val winner: PlayerPiece,
    val cells: List<Pair<Int, Int>>
)

class GameLogic(
    val boardSize: Int = 15,
    val ruleType: GameRuleType = GameRuleType.STANDARD
) {

    private val board: Array<Array<PlayerPiece?>> = Array(boardSize) { Array(boardSize) { null } }
    private val moveHistory = mutableListOf<MoveRecord>()

    var currentTurn: PlayerPiece = PlayerPiece.X
        private set

    var gameState: GameState = GameState.IN_PROGRESS
        private set

    var winningLine: WinningLine? = null
        private set

    val totalMoves: Int get() = moveHistory.size
    val moves: List<MoveRecord> get() = moveHistory.toList()
    val lastMove: MoveRecord? get() = moveHistory.lastOrNull()

    fun getPieceAt(row: Int, col: Int): PlayerPiece? {
        if (!isInBounds(row, col)) return null
        return board[row][col]
    }

    fun setPieceAt(row: Int, col: Int, piece: PlayerPiece?) {
        if (isInBounds(row, col)) {
            board[row][col] = piece
        }
    }

    fun isInBounds(row: Int, col: Int): Boolean {
        return row in 0 until boardSize && col in 0 until boardSize
    }

    fun isValidMove(row: Int, col: Int): Boolean {
        return gameState == GameState.IN_PROGRESS && isInBounds(row, col) && board[row][col] == null
    }

    fun makeMove(row: Int, col: Int): Boolean {
        if (!isValidMove(row, col)) return false

        val piece = currentTurn
        board[row][col] = piece
        val record = MoveRecord(row, col, piece, moveHistory.size + 1)
        moveHistory.add(record)

        val winLine = checkWinAt(row, col, piece)
        if (winLine != null) {
            winningLine = winLine
            gameState = if (piece == PlayerPiece.X) GameState.X_WON else GameState.O_WON
        } else if (isBoardFull()) {
            gameState = GameState.DRAW
        } else {
            currentTurn = currentTurn.opposite()
        }

        return true
    }

    fun undoMove(count: Int = 1): Boolean {
        if (moveHistory.isEmpty()) return false

        val undos = count.coerceAtMost(moveHistory.size)
        repeat(undos) {
            val last = moveHistory.removeAt(moveHistory.size - 1)
            board[last.row][last.col] = null
        }

        winningLine = null
        gameState = GameState.IN_PROGRESS
        currentTurn = if (moveHistory.isEmpty()) PlayerPiece.X else moveHistory.last().piece.opposite()
        return true
    }

    fun resetGame(startingPiece: PlayerPiece = PlayerPiece.X) {
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                board[r][c] = null
            }
        }
        moveHistory.clear()
        currentTurn = startingPiece
        gameState = GameState.IN_PROGRESS
        winningLine = null
    }

    fun isGameOver(): Boolean = gameState != GameState.IN_PROGRESS

    fun isBoardFull(): Boolean {
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                if (board[r][c] == null) return false
            }
        }
        return true
    }

    /**
     * Kiểm tra điều kiện chiến thắng chính xác tại ô (row, col) theo luật cờ Caro được chọn:
     * - Hàng ngang: (0, 1)
     * - Hàng dọc: (1, 0)
     * - Đường chéo chính (\): (1, 1)
     * - Đường chéo phụ (/): (1, -1)
     */
    fun checkWinAt(row: Int, col: Int, piece: PlayerPiece): WinningLine? {
        val opponent = piece.opposite()
        val directions = listOf(
            Pair(0, 1),   // Ngang
            Pair(1, 0),   // Dọc
            Pair(1, 1),   // Chéo chính (\)
            Pair(1, -1)   // Chéo phụ (/)
        )

        for ((dr, dc) in directions) {
            val lineCells = mutableListOf<Pair<Int, Int>>()
            lineCells.add(Pair(row, col))

            // 1. Quét theo chiều dương (+dr, +dc)
            var rPos = row + dr
            var cPos = col + dc
            while (isInBounds(rPos, cPos) && board[rPos][cPos] == piece) {
                lineCells.add(Pair(rPos, cPos))
                rPos += dr
                cPos += dc
            }
            // Đầu dương chỉ bị coi là chặn bởi đối thủ nếu ô kế tiếp nằm trong bàn cờ VÀ có quân của đối phương
            val posBlockedByOpponent = isInBounds(rPos, cPos) && board[rPos][cPos] == opponent

            // 2. Quét theo chiều âm (-dr, -dc)
            var rNeg = row - dr
            var cNeg = col - dc
            while (isInBounds(rNeg, cNeg) && board[rNeg][cNeg] == piece) {
                lineCells.add(Pair(rNeg, cNeg))
                rNeg -= dr
                cNeg -= dc
            }
            // Đầu âm chỉ bị coi là chặn bởi đối thủ nếu ô kế tiếp nằm trong bàn cờ VÀ có quân của đối phương
            val negBlockedByOpponent = isInBounds(rNeg, cNeg) && board[rNeg][cNeg] == opponent

            val count = lineCells.size

            when (ruleType) {
                GameRuleType.STANDARD -> {
                    // Luật Tự Do: từ 5 quân liên tiếp trở lên là thắng
                    if (count >= 5) {
                        lineCells.sortWith(compareBy({ it.first }, { it.second }))
                        return WinningLine(piece, lineCells)
                    }
                }
                GameRuleType.BLOCK_TWO_ENDS -> {
                    // Luật Caro Việt Nam (Chặn 2 đầu):
                    // Đạt 5 quân liên tiếp và KHÔNG bị quân đối phương chặn cả 2 đầu
                    if (count == 5) {
                        val isBlockedBothEnds = posBlockedByOpponent && negBlockedByOpponent
                        if (!isBlockedBothEnds) {
                            lineCells.sortWith(compareBy({ it.first }, { it.second }))
                            return WinningLine(piece, lineCells)
                        }
                    } else if (count >= 6) {
                        // Từ 6 quân liên tiếp trở lên trong Caro VN cũng tính là thắng
                        lineCells.sortWith(compareBy({ it.first }, { it.second }))
                        return WinningLine(piece, lineCells)
                    }
                }
                GameRuleType.EXACT_FIVE -> {
                    // Luật Gomoku Chuẩn: Phải đúng 5 quân liên tiếp
                    if (count == 5) {
                        lineCells.sortWith(compareBy({ it.first }, { it.second }))
                        return WinningLine(piece, lineCells)
                    }
                }
            }
        }
        return null
    }

    /**
     * Quét toàn bộ bàn cờ để tìm đường thắng nếu có (dùng cho việc khôi phục trận đấu).
     */
    fun scanWinningLine(): WinningLine? {
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                val piece = board[r][c] ?: continue
                val win = checkWinAt(r, c, piece)
                if (win != null) return win
            }
        }
        return null
    }

    fun getBoardCopy(): Array<Array<PlayerPiece?>> {
        return Array(boardSize) { r ->
            Array(boardSize) { c ->
                board[r][c]
            }
        }
    }
}
