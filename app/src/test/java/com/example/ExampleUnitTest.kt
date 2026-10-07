package com.example

import com.example.ai.AiDifficulty
import com.example.ai.CaroAiEngine
import com.example.data.TrainingData
import com.example.logic.GameLogic
import com.example.logic.GameRuleType
import com.example.logic.GameState
import com.example.logic.PlayerPiece
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CaroGameLogicTest {

    @Test
    fun testInitialGameState() {
        val game = GameLogic(15, GameRuleType.STANDARD)
        assertEquals(GameState.IN_PROGRESS, game.gameState)
        assertEquals(PlayerPiece.X, game.currentTurn)
        assertEquals(0, game.totalMoves)
        assertNull(game.winningLine)
        assertTrue(game.isValidMove(7, 7))
        assertFalse(game.isValidMove(-1, 0))
        assertFalse(game.isValidMove(15, 15))
    }

    @Test
    fun testStandardRuleFiveInARowWins() {
        val game = GameLogic(15, GameRuleType.STANDARD)
        // 5 consecutive horizontal moves for X
        for (c in 0..4) {
            game.setPieceAt(7, c, PlayerPiece.X)
        }
        val winLine = game.checkWinAt(7, 2, PlayerPiece.X)
        assertNotNull(winLine)
        assertEquals(PlayerPiece.X, winLine?.winner)
        assertEquals(5, winLine?.cells?.size)
    }

    @Test
    fun testStandardRuleOverlineWins() {
        val game = GameLogic(15, GameRuleType.STANDARD)
        // 6 consecutive vertical moves for X
        for (r in 0..5) {
            game.setPieceAt(r, 7, PlayerPiece.X)
        }
        val winLine = game.checkWinAt(3, 7, PlayerPiece.X)
        assertNotNull(winLine)
        assertEquals(6, winLine?.cells?.size)
    }

    @Test
    fun testBlockTwoEndsRule_BlockedBothEndsDoesNotWin() {
        val game = GameLogic(15, GameRuleType.BLOCK_TWO_ENDS)
        // Setup opponent blocking both ends of 5 X pieces
        game.setPieceAt(7, 1, PlayerPiece.O) // Left block
        game.setPieceAt(7, 7, PlayerPiece.O) // Right block

        // 5 X pieces between (7, 2) and (7, 6)
        for (c in 2..6) {
            game.setPieceAt(7, c, PlayerPiece.X)
        }

        val winLine = game.checkWinAt(7, 4, PlayerPiece.X)
        // Blocked by opponent on both ends -> should NOT win
        assertNull(winLine)
    }

    @Test
    fun testBlockTwoEndsRule_BlockedOneEndWins() {
        val game = GameLogic(15, GameRuleType.BLOCK_TWO_ENDS)
        // Opponent blocks ONLY left end
        game.setPieceAt(7, 1, PlayerPiece.O)
        // Right end (7, 7) is empty!

        // 5 X pieces between (7, 2) and (7, 6)
        for (c in 2..6) {
            game.setPieceAt(7, c, PlayerPiece.X)
        }

        val winLine = game.checkWinAt(7, 4, PlayerPiece.X)
        // Only 1 end blocked -> WINS!
        assertNotNull(winLine)
        assertEquals(5, winLine?.cells?.size)
    }

    @Test
    fun testBlockTwoEndsRule_BoardBorderEdgeWins() {
        val game = GameLogic(15, GameRuleType.BLOCK_TWO_ENDS)
        // 5 X pieces starting at edge of board (7, 0) to (7, 4)
        for (c in 0..4) {
            game.setPieceAt(7, c, PlayerPiece.X)
        }
        // Left end is out-of-bounds (board edge), right end (7, 5) has an opponent piece
        game.setPieceAt(7, 5, PlayerPiece.O)

        val winLine = game.checkWinAt(7, 2, PlayerPiece.X)
        // Hitting edge of board is NOT blocked by opponent -> WINS!
        assertNotNull(winLine)
    }

    @Test
    fun testExactFiveRule() {
        val game = GameLogic(15, GameRuleType.EXACT_FIVE)
        // 5 in a row -> WINS
        for (c in 2..6) {
            game.setPieceAt(5, c, PlayerPiece.X)
        }
        val winFive = game.checkWinAt(5, 4, PlayerPiece.X)
        assertNotNull(winFive)

        // 6 in a row under EXACT_FIVE -> does NOT win
        game.setPieceAt(5, 7, PlayerPiece.X)
        val winSix = game.checkWinAt(5, 4, PlayerPiece.X)
        assertNull(winSix)
    }

    @Test
    fun testDiagonalWin() {
        val game = GameLogic(15, GameRuleType.STANDARD)
        // Main diagonal (1, 1), (2, 2), (3, 3), (4, 4), (5, 5)
        for (i in 1..5) {
            game.setPieceAt(i, i, PlayerPiece.O)
        }
        val winLine = game.checkWinAt(3, 3, PlayerPiece.O)
        assertNotNull(winLine)
        assertEquals(PlayerPiece.O, winLine?.winner)
    }

    @Test
    fun testAntiDiagonalWin() {
        val game = GameLogic(15, GameRuleType.STANDARD)
        // Anti-diagonal (1, 5), (2, 4), (3, 3), (4, 2), (5, 1)
        for (i in 1..5) {
            game.setPieceAt(i, 6 - i, PlayerPiece.X)
        }
        val winLine = game.checkWinAt(3, 3, PlayerPiece.X)
        assertNotNull(winLine)
        assertEquals(PlayerPiece.X, winLine?.winner)
    }

    @Test
    fun testMakeMoveAndUndo() {
        val game = GameLogic(15, GameRuleType.STANDARD)
        assertTrue(game.makeMove(7, 7)) // X plays
        assertEquals(PlayerPiece.O, game.currentTurn)
        assertEquals(1, game.totalMoves)

        assertTrue(game.makeMove(7, 8)) // O plays
        assertEquals(PlayerPiece.X, game.currentTurn)
        assertEquals(2, game.totalMoves)

        // Cannot play on occupied cell
        assertFalse(game.makeMove(7, 7))

        // Undo 1 move
        assertTrue(game.undoMove(1))
        assertEquals(1, game.totalMoves)
        assertEquals(PlayerPiece.O, game.currentTurn)
        assertNull(game.getPieceAt(7, 8))
        assertNotNull(game.getPieceAt(7, 7))
    }

    @Test
    fun testTrainingPuzzlesData() {
        assertEquals(10, TrainingData.PUZZLES.size)
        for (puzzle in TrainingData.PUZZLES) {
            assertTrue(puzzle.id in 1..10)
            assertTrue(puzzle.correctMoves.isNotEmpty())
            assertTrue(puzzle.initialPieces.isNotEmpty())
            assertTrue(puzzle.title.isNotEmpty())
            assertTrue(puzzle.description.isNotEmpty())
        }
    }

    @Test
    fun testAiFindsImmediateWin() {
        val ai = CaroAiEngine()
        val board = Array(15) { Array<PlayerPiece?>(15) { null } }

        // AI has 4 in a row: (7, 2), (7, 3), (7, 4), (7, 5)
        board[7][2] = PlayerPiece.O
        board[7][3] = PlayerPiece.O
        board[7][4] = PlayerPiece.O
        board[7][5] = PlayerPiece.O

        val winMove = ai.findBestMove(board, 15, PlayerPiece.O, AiDifficulty.MASTER)
        assertNotNull(winMove)
        assertTrue(winMove == Pair(7, 1) || winMove == Pair(7, 6))
    }

    @Test
    fun testAiBlocksImmediateHumanWin() {
        val ai = CaroAiEngine()
        val board = Array(15) { Array<PlayerPiece?>(15) { null } }

        // Human has 4 in a row: (7, 2), (7, 3), (7, 4), (7, 5)
        board[7][2] = PlayerPiece.X
        board[7][3] = PlayerPiece.X
        board[7][4] = PlayerPiece.X
        board[7][5] = PlayerPiece.X

        val blockMove = ai.findBestMove(board, 15, PlayerPiece.O, AiDifficulty.MASTER)
        assertNotNull(blockMove)
        assertTrue(blockMove == Pair(7, 1) || blockMove == Pair(7, 6))
    }
}
