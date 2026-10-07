package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.PlayerEntity
import com.example.logic.GameState
import com.example.logic.PlayerPiece
import com.example.ui.components.CaroBoardView
import com.example.ui.components.GameResultDialog
import com.example.ui.components.InGamePlayerCard
import com.example.ui.theme.OceanBackground
import com.example.ui.theme.OceanBorder
import com.example.ui.theme.OceanBorderStrong
import com.example.ui.theme.OceanButtonGradient
import com.example.ui.theme.OceanDark
import com.example.ui.theme.OceanHeroGradient
import com.example.ui.theme.OceanIce
import com.example.ui.theme.PieceCyanO
import com.example.ui.theme.PieceRedX
import com.example.ui.theme.TextBlackPure
import com.example.ui.theme.TextBlackSecondary
import com.example.ui.theme.TextBlackSolid
import com.example.ui.theme.TextWhitePure
import com.example.ui.theme.WhitePure
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaroGameScreen(
    gameViewModel: GameViewModel,
    player: PlayerEntity?,
    isDarkTheme: Boolean,
    onBackToHome: () -> Unit,
    onViewHistory: () -> Unit
) {
    val uiState by gameViewModel.uiState.collectAsState()
    var showSurrenderConfirm by remember { mutableStateOf(false) }
    var showRestartConfirm by remember { mutableStateOf(false) }

    BackHandler {
        if (uiState.gameState == GameState.IN_PROGRESS && uiState.totalMoves > 0) {
            showSurrenderConfirm = true
        } else {
            onBackToHome()
        }
    }

    val isTurnX = uiState.currentTurn == PlayerPiece.X
    val playerAvatar = player?.avatar ?: "MASTER"
    val isAiMode = uiState.mode == "AI"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isAiMode) "ĐẤU VỚI MÁY • ${uiState.difficulty.label.uppercase()}" else "HAI NGƯỜI CHƠI",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = TextBlackSolid
                        )
                        Text(
                            text = "BÀN ${uiState.boardSize}x${uiState.boardSize} • ${uiState.ruleType.label.uppercase()} • NƯỚC: ${uiState.totalMoves}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextBlackSecondary
                        )
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(OceanIce)
                            .border(1.dp, OceanBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                if (uiState.gameState == GameState.IN_PROGRESS && uiState.totalMoves > 0) {
                                    showSurrenderConfirm = true
                                } else {
                                    onBackToHome()
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("game_back_button")
                    ) {
                        Text("THOÁT", fontWeight = FontWeight.Black, color = TextBlackSolid, fontSize = 11.sp)
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(OceanIce)
                            .border(1.dp, OceanBorder, RoundedCornerShape(8.dp))
                            .clickable { showRestartConfirm = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("game_restart_appbar_button")
                    ) {
                        Text("ĐÁNH LẠI", fontWeight = FontWeight.Black, color = OceanDark, fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OceanIce
                )
            )
        },
        containerColor = OceanBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Players Info Cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InGamePlayerCard(
                        name = uiState.playerXName,
                        piece = PlayerPiece.X,
                        avatarKey = if (isAiMode && uiState.humanPiece == PlayerPiece.X) playerAvatar else if (isAiMode) "WARRIOR" else playerAvatar,
                        isCurrentTurn = isTurnX && uiState.gameState == GameState.IN_PROGRESS,
                        timeRemaining = if (uiState.timeLimitSeconds > 0) uiState.timeRemainingX else null,
                        rating = if (isAiMode && uiState.humanPiece == PlayerPiece.X) player?.rating else null,
                        modifier = Modifier.weight(1f).testTag("player_card_x")
                    )

                    InGamePlayerCard(
                        name = uiState.playerOName,
                        piece = PlayerPiece.O,
                        avatarKey = if (isAiMode && uiState.humanPiece == PlayerPiece.O) playerAvatar else if (isAiMode) "WARRIOR" else "SAMURAI",
                        isCurrentTurn = !isTurnX && uiState.gameState == GameState.IN_PROGRESS,
                        timeRemaining = if (uiState.timeLimitSeconds > 0) uiState.timeRemainingO else null,
                        rating = if (isAiMode && uiState.humanPiece == PlayerPiece.O) player?.rating else null,
                        modifier = Modifier.weight(1f).testTag("player_card_o")
                    )
                }

                AnimatedVisibility(
                    visible = uiState.isAiThinking,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(OceanIce)
                            .border(1.5.dp, OceanBorderStrong, RoundedCornerShape(10.dp))
                            .padding(vertical = 6.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(14.dp),
                                color = OceanDark
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "AI ĐANG SUY NGHĨ NƯỚC ĐI TỐI ƯU...",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = OceanDark
                            )
                        }
                    }
                }
            }

            // 2. Caro Board View
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CaroBoardView(
                    boardSize = uiState.boardSize,
                    boardState = gameViewModel.boardState,
                    lastMove = uiState.lastMove,
                    winningLine = uiState.winningLine,
                    hintCell = uiState.hintCell,
                    pieceStyle = gameViewModel.currentSettings.pieceStyle,
                    boardEffects = gameViewModel.currentSettings.boardEffects,
                    isInteractive = uiState.gameState == GameState.IN_PROGRESS && !uiState.isAiThinking,
                    isDarkTheme = isDarkTheme,
                    onCellClicked = { r, c ->
                        gameViewModel.onCellClicked(r, c)
                    },
                    modifier = Modifier.padding(2.dp)
                )
            }

            // 3. Action Buttons with rich ocean styling
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Undo Button
                Button(
                    onClick = { gameViewModel.undoMove() },
                    enabled = uiState.totalMoves > 0 && uiState.gameState == GameState.IN_PROGRESS && !uiState.isAiThinking && uiState.remainingUndos > 0,
                    modifier = Modifier.weight(1f).testTag("game_undo_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OceanIce),
                    border = BorderStroke(1.5.dp, OceanBorder)
                ) {
                    val undoLabel = if (isAiMode) "HOÀN (${uiState.remainingUndos})" else "HOÀN"
                    Text(text = undoLabel, fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextBlackSolid)
                }

                // Hint Button
                Button(
                    onClick = { gameViewModel.requestHint() },
                    enabled = uiState.gameState == GameState.IN_PROGRESS && !uiState.isAiThinking && uiState.remainingHints > 0,
                    modifier = Modifier.weight(1f).testTag("game_hint_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OceanIce),
                    border = BorderStroke(1.5.dp, OceanBorderStrong)
                ) {
                    Text(text = "GỢI Ý (${uiState.remainingHints})", fontSize = 11.sp, fontWeight = FontWeight.Black, color = OceanDark)
                }

                // Restart Button
                OutlinedButton(
                    onClick = { showRestartConfirm = true },
                    modifier = Modifier.weight(1f).testTag("game_restart_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, OceanBorder)
                ) {
                    Text(text = "ĐÁNH LẠI", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextBlackSolid)
                }

                // Surrender Button
                OutlinedButton(
                    onClick = { showSurrenderConfirm = true },
                    enabled = uiState.gameState == GameState.IN_PROGRESS,
                    modifier = Modifier.weight(1f).testTag("game_surrender_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, PieceRedX.copy(alpha = 0.6f))
                ) {
                    Text(text = "XIN THUA", fontSize = 11.sp, fontWeight = FontWeight.Black, color = PieceRedX)
                }
            }
        }
    }

    // Surrender Dialog
    if (showSurrenderConfirm) {
        AlertDialog(
            onDismissRequest = { showSurrenderConfirm = false },
            title = { Text(text = "XÁC NHẬN ĐẦU HÀNG?", fontWeight = FontWeight.Black, color = TextBlackSolid) },
            text = { Text("Bạn có chắc chắn muốn nhận thua ván cờ hiện tại không?", color = TextBlackPure) },
            confirmButton = {
                Button(
                    onClick = {
                        showSurrenderConfirm = false
                        gameViewModel.surrender()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PieceRedX)
                ) {
                    Text("ĐẦU HÀNG", fontWeight = FontWeight.Black, color = WhitePure)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSurrenderConfirm = false }) {
                    Text("HỦY", fontWeight = FontWeight.Black, color = TextBlackSolid)
                }
            }
        )
    }

    // Restart Dialog
    if (showRestartConfirm) {
        AlertDialog(
            onDismissRequest = { showRestartConfirm = false },
            title = { Text(text = "BẮT ĐẦU LẠI VÁN CỜ?", fontWeight = FontWeight.Black, color = TextBlackSolid) },
            text = { Text("Trận cờ hiện tại sẽ được khởi động lại từ đầu.", color = TextBlackPure) },
            confirmButton = {
                Button(
                    onClick = {
                        showRestartConfirm = false
                        gameViewModel.restartCurrentMatch()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OceanDark)
                ) {
                    Text("ĐỒNG Ý", fontWeight = FontWeight.Black, color = WhitePure)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestartConfirm = false }) {
                    Text("HỦY", fontWeight = FontWeight.Black, color = TextBlackSolid)
                }
            }
        )
    }

    // Game Result Dialog
    if (uiState.gameState != GameState.IN_PROGRESS) {
        val winnerStr = when (uiState.gameState) {
            GameState.X_WON -> "X"
            GameState.O_WON -> "O"
            GameState.DRAW -> "DRAW"
            else -> ""
        }

        GameResultDialog(
            winner = winnerStr,
            humanSymbol = uiState.humanPiece.symbol,
            mode = uiState.mode,
            summary = uiState.summaryResult,
            onPlayAgain = { gameViewModel.restartCurrentMatch() },
            onViewHistory = onViewHistory,
            onHome = onBackToHome
        )
    }
}
