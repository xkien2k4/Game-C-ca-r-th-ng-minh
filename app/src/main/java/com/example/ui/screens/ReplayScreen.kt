package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.entity.GameEntity
import com.example.data.entity.MoveEntity
import com.example.data.repository.CaroRepository
import com.example.logic.MoveRecord
import com.example.logic.PlayerPiece
import com.example.ui.components.CaroBoardView
import com.example.ui.theme.OceanBackground
import com.example.ui.theme.OceanBorder
import com.example.ui.theme.OceanBorderStrong
import com.example.ui.theme.OceanButtonGradient
import com.example.ui.theme.OceanDark
import com.example.ui.theme.OceanHeroGradient
import com.example.ui.theme.OceanIce
import com.example.ui.theme.OceanPastel
import com.example.ui.theme.PieceCyanO
import com.example.ui.theme.PieceRedX
import com.example.ui.theme.TextBlackMuted
import com.example.ui.theme.TextBlackPure
import com.example.ui.theme.TextBlackSecondary
import com.example.ui.theme.TextBlackSolid
import com.example.ui.theme.TextWhitePure
import com.example.ui.theme.WhiteCard
import com.example.ui.theme.WhitePure
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplayScreen(
    gameId: Long,
    repository: CaroRepository,
    isDarkTheme: Boolean,
    onBack: () -> Unit
) {
    var game by remember { mutableStateOf<GameEntity?>(null) }
    var moves by remember { mutableStateOf<List<MoveEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var currentStep by remember { mutableIntStateOf(0) }
    var isAutoPlaying by remember { mutableStateOf(false) }
    var playSpeedMs by remember { mutableStateOf(700L) }

    LaunchedEffect(gameId) {
        isLoading = true
        game = repository.getGameById(gameId)
        moves = repository.getMovesForGame(gameId)
        currentStep = moves.size
        isLoading = false
    }

    LaunchedEffect(isAutoPlaying, currentStep, playSpeedMs) {
        if (isAutoPlaying) {
            if (currentStep < moves.size) {
                delay(playSpeedMs)
                currentStep++
            } else {
                isAutoPlaying = false
            }
        }
    }

    val boardSize = game?.boardSize ?: 15

    // Reconstruct board at currentStep
    val boardState = remember(moves, currentStep, boardSize) {
        val board = Array(boardSize) { arrayOfNulls<PlayerPiece>(boardSize) }
        for (i in 0 until currentStep.coerceAtMost(moves.size)) {
            val m = moves[i]
            if (m.row in 0 until boardSize && m.col in 0 until boardSize) {
                board[m.row][m.col] = if (m.player == "X") PlayerPiece.X else PlayerPiece.O
            }
        }
        board
    }

    val lastMoveRecord = remember(moves, currentStep) {
        if (currentStep > 0 && currentStep <= moves.size) {
            val m = moves[currentStep - 1]
            MoveRecord(
                row = m.row,
                col = m.col,
                piece = if (m.player == "X") PlayerPiece.X else PlayerPiece.O,
                moveIndex = m.moveNumber
            )
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "XEM LẠI VÁN ĐẤU",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = TextBlackSolid
                        )
                        if (game != null) {
                            Text(
                                text = "${game!!.playerX} (X) vs ${game!!.playerO} (O)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextBlackSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(OceanIce)
                            .border(1.dp, OceanBorder, RoundedCornerShape(8.dp))
                            .clickable { onBack() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("replay_back_button")
                    ) {
                        Text("QUAY LẠI", fontWeight = FontWeight.Black, color = TextBlackSolid, fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OceanIce
                )
            )
        },
        containerColor = OceanBackground
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = OceanDark)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Info Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WhitePure),
                    border = BorderStroke(1.5.dp, OceanBorder),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NƯỚC: $currentStep / ${moves.size}",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = OceanDark
                        )
                        if (lastMoveRecord != null) {
                            Text(
                                text = "Nước vừa đi: Quân ${lastMoveRecord.piece.symbol} (${(lastMoveRecord.col + 'A'.code).toChar()}${lastMoveRecord.row + 1})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (lastMoveRecord.piece == PlayerPiece.X) PieceRedX else PieceCyanO
                            )
                        } else {
                            Text(
                                text = "Bàn cờ ban đầu",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextBlackMuted
                            )
                        }
                    }
                }

                // Board
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CaroBoardView(
                        boardSize = boardSize,
                        boardState = boardState,
                        lastMove = lastMoveRecord,
                        winningLine = null,
                        hintCell = null,
                        isInteractive = false,
                        isDarkTheme = isDarkTheme,
                        onCellClicked = { _, _ -> },
                        modifier = Modifier.padding(2.dp)
                    )
                }

                // Controls
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Slider
                    if (moves.isNotEmpty()) {
                        Slider(
                            value = currentStep.toFloat(),
                            onValueChange = {
                                isAutoPlaying = false
                                currentStep = it.toInt()
                            },
                            valueRange = 0f..moves.size.toFloat(),
                            steps = (moves.size - 1).coerceAtLeast(0),
                            colors = SliderDefaults.colors(
                                thumbColor = OceanDark,
                                activeTrackColor = OceanDark,
                                inactiveTrackColor = OceanIce
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("replay_slider")
                        )
                    }

                    // Button controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                isAutoPlaying = false
                                currentStep = 0
                            },
                            enabled = currentStep > 0,
                            modifier = Modifier.weight(1f).testTag("replay_start_btn"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, OceanBorder)
                        ) {
                            Text("<< ĐẦU", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextBlackSolid)
                        }

                        OutlinedButton(
                            onClick = {
                                isAutoPlaying = false
                                if (currentStep > 0) currentStep--
                            },
                            enabled = currentStep > 0,
                            modifier = Modifier.weight(1f).testTag("replay_prev_btn"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, OceanBorder)
                        ) {
                            Text("< LÙI", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextBlackSolid)
                        }

                        Button(
                            onClick = { isAutoPlaying = !isAutoPlaying },
                            modifier = Modifier.weight(1f).testTag("replay_play_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OceanDark)
                        ) {
                            Text(
                                text = if (isAutoPlaying) "DỪNG" else "PHÁT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = TextWhitePure
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                isAutoPlaying = false
                                if (currentStep < moves.size) currentStep++
                            },
                            enabled = currentStep < moves.size,
                            modifier = Modifier.weight(1f).testTag("replay_next_btn"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, OceanBorder)
                        ) {
                            Text("TIẾP >", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextBlackSolid)
                        }

                        OutlinedButton(
                            onClick = {
                                isAutoPlaying = false
                                currentStep = moves.size
                            },
                            enabled = currentStep < moves.size,
                            modifier = Modifier.weight(1f).testTag("replay_end_btn"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, OceanBorder)
                        ) {
                            Text("CUỐI >>", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextBlackSolid)
                        }
                    }
                }
            }
        }
    }
}
