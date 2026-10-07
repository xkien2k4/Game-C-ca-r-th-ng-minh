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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.GameEntity
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.OceanBackground
import com.example.ui.theme.OceanBorder
import com.example.ui.theme.OceanBorderStrong
import com.example.ui.theme.OceanButtonGradient
import com.example.ui.theme.OceanDark
import com.example.ui.theme.OceanHeroGradient
import com.example.ui.theme.OceanIce
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    games: List<GameEntity>,
    onBack: () -> Unit,
    onReplayGame: (gameId: Long) -> Unit,
    onDeleteGame: (gameId: Long) -> Unit,
    onClearAllHistory: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showClearAllConfirm by remember { mutableStateOf(false) }
    var gameToDelete by remember { mutableStateOf<Long?>(null) }

    val filteredGames = remember(games, selectedFilter) {
        when (selectedFilter) {
            "AI" -> games.filter { it.mode == "AI" }
            "LOCAL" -> games.filter { it.mode == "LOCAL" }
            "WIN" -> games.filter { it.result == "WIN" }
            "LOSS" -> games.filter { it.result == "LOSS" }
            else -> games
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "LỊCH SỬ TRẬN ĐẤU (SQLITE)",
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
                            .clickable { onBack() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("history_back_button")
                    ) {
                        Text("QUAY LẠI", fontWeight = FontWeight.Black, color = TextBlackSolid, fontSize = 11.sp)
                    }
                },
                actions = {
                    if (games.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PieceRedX.copy(alpha = 0.1f))
                                .border(1.dp, PieceRedX.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { showClearAllConfirm = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("history_clear_all_button")
                        ) {
                            Text("XÓA TẤT CẢ", fontWeight = FontWeight.Black, color = PieceRedX, fontSize = 10.sp)
                        }
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
                .padding(horizontal = 16.dp)
        ) {
            // Filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                val filters = listOf(
                    Pair("ALL", "Tất cả (${games.size})"),
                    Pair("WIN", "Thắng"),
                    Pair("LOSS", "Thua"),
                    Pair("AI", "Đấu AI"),
                    Pair("LOCAL", "2 Người")
                )
                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) OceanDark else WhitePure)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) OceanDark else OceanBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedFilter = key }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) TextWhitePure else TextBlackSolid
                        )
                    }
                }
            }

            if (filteredGames.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CHƯA CÓ VÁN ĐẤU NÀO",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = TextBlackSolid
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Các trận cờ đã chơi sẽ được lưu trữ tự động tại đây.",
                            fontSize = 12.sp,
                            color = TextBlackSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize().padding(bottom = 12.dp)
                ) {
                    items(filteredGames, key = { it.id }) { game ->
                        GameHistoryCard(
                            game = game,
                            onReplay = { onReplayGame(game.id) },
                            onDelete = { gameToDelete = game.id }
                        )
                    }
                }
            }
        }
    }

    // Delete single confirm
    if (gameToDelete != null) {
        AlertDialog(
            onDismissRequest = { gameToDelete = null },
            title = { Text("XÁC NHẬN XÓA?", fontWeight = FontWeight.Black, color = TextBlackSolid) },
            text = { Text("Bạn có muốn xóa ván đấu này khỏi lịch sử SQLite?", color = TextBlackPure) },
            confirmButton = {
                Button(
                    onClick = {
                        gameToDelete?.let { onDeleteGame(it) }
                        gameToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PieceRedX)
                ) {
                    Text("XÓA", fontWeight = FontWeight.Black, color = WhitePure)
                }
            },
            dismissButton = {
                TextButton(onClick = { gameToDelete = null }) {
                    Text("HỦY", fontWeight = FontWeight.Black, color = TextBlackSolid)
                }
            }
        )
    }

    // Clear all confirm
    if (showClearAllConfirm) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirm = false },
            title = { Text("XÓA TOÀN BỘ LỊCH SỬ?", fontWeight = FontWeight.Black, color = TextBlackSolid) },
            text = { Text("Thao tác này sẽ xóa vĩnh viễn toàn bộ các ván cờ đã lưu trong SQLite.", color = TextBlackPure) },
            confirmButton = {
                Button(
                    onClick = {
                        showClearAllConfirm = false
                        onClearAllHistory()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PieceRedX)
                ) {
                    Text("XÓA HẾT", fontWeight = FontWeight.Black, color = WhitePure)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirm = false }) {
                    Text("HỦY", fontWeight = FontWeight.Black, color = TextBlackSolid)
                }
            }
        )
    }
}

@Composable
private fun GameHistoryCard(
    game: GameEntity,
    onReplay: () -> Unit,
    onDelete: () -> Unit
) {
    val resultColor = when (game.result) {
        "WIN" -> PieceCyanO
        "LOSS" -> PieceRedX
        else -> TextBlackSecondary
    }

    val resultLabel = when (game.result) {
        "WIN" -> "THẮNG"
        "LOSS" -> "THUA"
        else -> "HÒA"
    }

    val dateStr = game.createdAt

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        border = BorderStroke(1.5.dp, OceanBorder),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(resultColor.copy(alpha = 0.15f))
                            .border(1.dp, resultColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = resultLabel,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = resultColor
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (game.mode == "AI") "ĐẤU VỚI MÁY (${game.difficulty})" else "HAI NGƯỜI CHƠI",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = TextBlackSolid
                    )
                }

                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = TextBlackMuted,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${game.playerX} (X) vs ${game.playerO} (O)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlackSolid
                )
                Text(
                    text = "${game.totalMoves} nước đi",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = OceanDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PieceRedX.copy(alpha = 0.08f))
                        .clickable { onDelete() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("XÓA", fontSize = 10.sp, fontWeight = FontWeight.Black, color = PieceRedX)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(OceanHeroGradient)
                        .clickable { onReplay() }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("XEM LẠI", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextWhitePure)
                }
            }
        }
    }
}
