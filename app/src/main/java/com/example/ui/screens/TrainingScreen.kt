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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TrainingData
import com.example.data.TrainingPuzzle
import com.example.ui.components.CaroBoardView
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.OceanBackground
import com.example.ui.theme.OceanBorder
import com.example.ui.theme.OceanBorderStrong
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
import com.example.ui.theme.TrophyGold
import com.example.ui.theme.WhiteCard
import com.example.ui.theme.WhitePure
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    gameViewModel: GameViewModel,
    isDarkTheme: Boolean,
    onBack: () -> Unit
) {
    var selectedPuzzle by remember { mutableStateOf<TrainingPuzzle?>(null) }
    val uiState by gameViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedPuzzle == null) "CHẾ ĐỘ LUYỆN TẬP THẾ CỜ" else selectedPuzzle!!.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = TextBlackSolid
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(OceanIce)
                            .border(1.dp, OceanBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                if (selectedPuzzle != null) {
                                    selectedPuzzle = null
                                } else {
                                    onBack()
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("training_back_button")
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
        if (selectedPuzzle == null) {
            // Puzzle Selection List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "DANH SÁCH 10 BÀI TOÁN THẾ CỜ",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = TextBlackSolid,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(TrainingData.PUZZLES) { puzzle ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                selectedPuzzle = puzzle
                                gameViewModel.startPracticePuzzle(puzzle)
                            }
                            .testTag("puzzle_card_${puzzle.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = WhitePure),
                        border = BorderStroke(1.5.dp, OceanBorder),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(OceanHeroGradient)
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "#${puzzle.id}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextWhitePure
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = puzzle.title,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = TextBlackSolid
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = puzzle.description,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextBlackSecondary
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OceanDark)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "GIẢI",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = TextWhitePure
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // In Puzzle Solve Screen
            val p = selectedPuzzle!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Puzzle Instructions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WhitePure),
                    border = BorderStroke(1.5.dp, OceanBorderStrong),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MỤC TIÊU: ${p.title.uppercase()}",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = TextBlackSolid
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PieceRedX.copy(alpha = 0.15f))
                                    .border(1.dp, PieceRedX, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LƯỢT QUÂN X",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PieceRedX
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = p.description,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextBlackSecondary
                        )
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
                        boardSize = 15,
                        boardState = gameViewModel.boardState,
                        lastMove = uiState.lastMove,
                        winningLine = uiState.winningLine,
                        hintCell = uiState.hintCell,
                        isInteractive = true,
                        isDarkTheme = isDarkTheme,
                        onCellClicked = { r, c ->
                            gameViewModel.onCellClicked(r, c)
                        },
                        modifier = Modifier.padding(2.dp)
                    )
                }

                // Bottom actions
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { gameViewModel.startPracticePuzzle(p) },
                        modifier = Modifier.weight(1f).testTag("training_reset_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, OceanBorder)
                    ) {
                        Text("THỬ LẠI", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextBlackSolid)
                    }

                    Button(
                        onClick = { gameViewModel.requestHint() },
                        modifier = Modifier.weight(1f).testTag("training_hint_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OceanDark)
                    ) {
                        Text("GỢI Ý NƯỚC ĐI", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextWhitePure)
                    }
                }
            }
        }
    }
}
