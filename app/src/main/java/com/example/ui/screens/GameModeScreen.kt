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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ai.AiDifficulty
import com.example.logic.GameRuleType
import com.example.logic.PlayerPiece
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
import com.example.ui.theme.TextBlackPure
import com.example.ui.theme.TextBlackSecondary
import com.example.ui.theme.TextBlackSolid
import com.example.ui.theme.TextWhitePure
import com.example.ui.theme.WhiteCard
import com.example.ui.theme.WhitePure

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameModeScreen(
    mode: String,
    defaultBoardSize: Int,
    defaultTimerSeconds: Int,
    defaultPlayerName: String,
    defaultRuleType: String,
    onBack: () -> Unit,
    onStartGame: (
        mode: String,
        difficulty: AiDifficulty,
        ruleType: GameRuleType,
        boardSize: Int,
        humanPiece: PlayerPiece,
        playerXName: String,
        playerOName: String,
        timerSeconds: Int
    ) -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf(AiDifficulty.MEDIUM) }
    var selectedRule by remember { mutableStateOf(GameRuleType.fromString(defaultRuleType)) }
    var selectedBoardSize by remember { mutableIntStateOf(defaultBoardSize) }
    var selectedPiece by remember { mutableStateOf(PlayerPiece.X) }
    var selectedTimer by remember { mutableIntStateOf(defaultTimerSeconds) }

    var localPlayerXName by remember { mutableStateOf(defaultPlayerName.ifEmpty { "Người chơi 1" }) }
    var localPlayerOName by remember { mutableStateOf("Người chơi 2") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (mode == "AI") "THIẾT LẬP ĐẤU VỚI MÁY" else "THIẾT LẬP 2 NGƯỜI CHƠI",
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
                            .testTag("mode_back_button")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // AI Difficulty Selection (Only for AI mode)
            if (mode == "AI") {
                SectionHeader("CẤP ĐỘ TRÍ TUỆ NHÂN TẠO (AI)")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AiDifficulty.entries.forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedDifficulty = diff }
                                .testTag("difficulty_${diff.name.lowercase()}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) OceanIce else WhiteCard
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) OceanDark else OceanBorder
                            ),
                            elevation = CardDefaults.cardElevation(if (isSelected) 2.dp else 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = diff.label,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = TextBlackSolid
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = diff.description,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextBlackSecondary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) OceanDark else OceanIce)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isSelected) "ĐÃ CHỌN" else "CHỌN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) TextWhitePure else TextBlackSolid
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Two-player names
            if (mode == "LOCAL") {
                SectionHeader("TÊN KỲ THỦ ĐẤU TRÍ")
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WhitePure),
                    border = BorderStroke(1.5.dp, OceanBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = localPlayerXName,
                            onValueChange = { localPlayerXName = it },
                            label = { Text("Người chơi 1 (Quân X - Đi trước)", fontWeight = FontWeight.Bold, color = PieceRedX) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("player_x_input")
                        )
                        OutlinedTextField(
                            value = localPlayerOName,
                            onValueChange = { localPlayerOName = it },
                            label = { Text("Người chơi 2 (Quân O - Đi sau)", fontWeight = FontWeight.Bold, color = PieceCyanO) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("player_o_input")
                        )
                    }
                }
            }

            // Piece Selection for AI Mode
            if (mode == "AI") {
                SectionHeader("CHỌN QUÂN CỜ CỦA BẠN")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isX = selectedPiece == PlayerPiece.X
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedPiece = PlayerPiece.X }
                            .testTag("select_piece_x"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isX) OceanIce else WhitePure),
                        border = BorderStroke(
                            width = if (isX) 2.dp else 1.dp,
                            color = if (isX) PieceRedX else OceanBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "QUÂN X", fontWeight = FontWeight.Black, fontSize = 18.sp, color = PieceRedX)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "ĐI TRƯỚC", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextBlackSolid)
                        }
                    }

                    val isO = selectedPiece == PlayerPiece.O
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedPiece = PlayerPiece.O }
                            .testTag("select_piece_o"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isO) OceanIce else WhitePure),
                        border = BorderStroke(
                            width = if (isO) 2.dp else 1.dp,
                            color = if (isO) PieceCyanO else OceanBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "QUÂN O", fontWeight = FontWeight.Black, fontSize = 18.sp, color = PieceCyanO)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "ĐI SAU", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextBlackSolid)
                        }
                    }
                }
            }

            // Board Size Selection
            SectionHeader("KÍCH THƯỚC BÀN CỜ")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair(15, "15x15\n(Chuẩn)"),
                    Pair(19, "19x19\n(Rộng)"),
                    Pair(11, "11x11\n(Nhanh)")
                ).forEach { (size, label) ->
                    val isSelected = selectedBoardSize == size
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedBoardSize = size }
                            .testTag("board_size_$size"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) OceanIce else WhitePure),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) OceanDark else OceanBorder
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (isSelected) OceanDark else TextBlackSolid,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Rule Type Selection
            SectionHeader("LUẬT CHƠI CARO")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GameRuleType.entries.forEach { rule ->
                    val isSelected = selectedRule == rule
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedRule = rule }
                            .testTag("rule_${rule.name.lowercase()}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) OceanIce else WhitePure),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) OceanDark else OceanBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = rule.label, fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextBlackSolid)
                                Text(text = rule.description, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TextBlackSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) OceanDark else OceanIce)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isSelected) "CHỌN" else "ĐỔI",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) TextWhitePure else TextBlackSolid
                                )
                            }
                        }
                    }
                }
            }

            // Timer Limit
            SectionHeader("THỜI GIAN MỖI NƯỚC ĐI")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair(0, "Vô hạn"),
                    Pair(30, "30s"),
                    Pair(60, "60s"),
                    Pair(90, "90s")
                ).forEach { (seconds, label) ->
                    val isSelected = selectedTimer == seconds
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTimer = seconds }
                            .testTag("timer_$seconds"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) OceanIce else WhitePure),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) OceanDark else OceanBorder
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = if (isSelected) OceanDark else TextBlackSolid
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Start Game CTA Button (Rich Ocean Gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(OceanHeroGradient)
                    .clickable {
                        val pX = if (mode == "AI") {
                            if (selectedPiece == PlayerPiece.X) defaultPlayerName.ifEmpty { "Kỳ Thủ" } else "AI (${selectedDifficulty.label})"
                        } else {
                            localPlayerXName.ifEmpty { "Người chơi 1" }
                        }

                        val pO = if (mode == "AI") {
                            if (selectedPiece == PlayerPiece.O) defaultPlayerName.ifEmpty { "Kỳ Thủ" } else "AI (${selectedDifficulty.label})"
                        } else {
                            localPlayerOName.ifEmpty { "Người chơi 2" }
                        }

                        onStartGame(
                            mode,
                            selectedDifficulty,
                            selectedRule,
                            selectedBoardSize,
                            selectedPiece,
                            pX,
                            pO,
                            selectedTimer
                        )
                    }
                    .testTag("start_game_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "BẮT ĐẦU VÁN ĐẤU",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = TextWhitePure,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(14.dp)
                .background(OceanDark, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = TextBlackSolid,
            letterSpacing = 0.5.sp
        )
    }
}
