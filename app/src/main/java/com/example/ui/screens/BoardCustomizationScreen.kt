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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.SettingsEntity
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.OceanBackground
import com.example.ui.theme.OceanBorder
import com.example.ui.theme.OceanBorderStrong
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
import com.example.ui.theme.WhiteCard
import com.example.ui.theme.WhitePure

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardCustomizationScreen(
    settings: SettingsEntity?,
    onSaveSettings: (SettingsEntity) -> Unit,
    onBack: () -> Unit
) {
    val current = settings ?: SettingsEntity()
    var selectedSize by remember(current.boardSize) { mutableStateOf(current.boardSize) }
    var selectedPieceStyle by remember(current.pieceStyle) { mutableStateOf(current.pieceStyle) }
    var effectsEnabled by remember(current.boardEffects) { mutableStateOf(current.boardEffects) }
    var soundEnabled by remember(current.soundEnabled) { mutableStateOf(current.soundEnabled) }
    var vibrationEnabled by remember(current.vibrationEnabled) { mutableStateOf(current.vibrationEnabled) }

    var isSavedToast by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "TÙY CHỈNH BÀN CỜ",
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
                            .testTag("custom_back_button")
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

            // Board Size Selection
            SectionHeader("KÍCH THƯỚC BÀN CỜ MẶC ĐỊNH")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair(15, "15x15\nChuẩn Quốc Tế"),
                    Pair(19, "19x19\nBàn Cờ Lớn"),
                    Pair(11, "11x11\nĐánh Nhanh")
                ).forEach { (size, label) ->
                    val isSelected = selectedSize == size
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedSize = size }
                            .testTag("custom_board_size_$size"),
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
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = if (isSelected) OceanDark else TextBlackSolid,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Piece Style Selection
            SectionHeader("PHONG CÁCH QUÂN CỜ X & O")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple("MODERN", "Hiện Đại Thanh Lịch", "Nét vẽ mảnh, bóng đổ tinh tế"),
                    Triple("BOLD", "Đậm Nét Rõ Ràng", "Nét dày tương phản cao"),
                    Triple("CLASSIC", "Cổ Điển Truyền Thống", "Chữ thập & vòng tròn nét đều")
                ).forEach { (styleKey, title, desc) ->
                    val isSelected = selectedPieceStyle == styleKey
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedPieceStyle = styleKey }
                            .testTag("piece_style_$styleKey"),
                        shape = RoundedCornerShape(12.dp),
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
                                Text(text = title, fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextBlackSolid)
                                Text(text = desc, fontSize = 10.sp, color = TextBlackSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("X", fontWeight = FontWeight.Black, fontSize = 16.sp, color = PieceRedX)
                                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                Text("O", fontWeight = FontWeight.Black, fontSize = 16.sp, color = PieceCyanO)
                            }
                        }
                    }
                }
            }

            // Effects & Toggles
            SectionHeader("HIỆU ỨNG TƯƠNG TÁC")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                border = BorderStroke(1.5.dp, OceanBorder),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Hiệu ứng hoạt họa bàn cờ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextBlackSolid)
                        Switch(
                            checked = effectsEnabled,
                            onCheckedChange = { effectsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = WhitePure,
                                checkedTrackColor = OceanDark,
                                uncheckedTrackColor = OceanIce
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Âm thanh tiếng gõ cờ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextBlackSolid)
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = { soundEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = WhitePure,
                                checkedTrackColor = OceanDark,
                                uncheckedTrackColor = OceanIce
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Rung khi chạm đặt quân", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextBlackSolid)
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { vibrationEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = WhitePure,
                                checkedTrackColor = OceanDark,
                                uncheckedTrackColor = OceanIce
                            )
                        )
                    }
                }
            }

            // Save CTA Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OceanHeroGradient)
                    .clickable {
                        onSaveSettings(
                            current.copy(
                                boardSize = selectedSize,
                                pieceStyle = selectedPieceStyle,
                                boardEffects = effectsEnabled,
                                soundEnabled = soundEnabled,
                                vibrationEnabled = vibrationEnabled
                            )
                        )
                        isSavedToast = true
                    }
                    .testTag("save_customization_button"),
                contentAlignment = Alignment.Center
            ) {
                Text("LƯU TÙY CHỈNH BÀN CỜ", fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextWhitePure)
            }

            if (isSavedToast) {
                Text(
                    text = "Đã lưu cài đặt bàn cờ thành công!",
                    color = EmeraldGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
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
