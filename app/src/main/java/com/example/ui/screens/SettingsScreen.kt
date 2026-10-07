package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entity.SettingsEntity
import com.example.ui.theme.OceanBackground
import com.example.ui.theme.OceanBorder
import com.example.ui.theme.OceanBorderStrong
import com.example.ui.theme.OceanDark
import com.example.ui.theme.OceanHeroGradient
import com.example.ui.theme.OceanIce
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
fun SettingsScreen(
    settings: SettingsEntity?,
    onUpdateSettings: (SettingsEntity) -> Unit,
    onOpenBoardCustomization: () -> Unit,
    onOpenBackupRestore: () -> Unit,
    onShowTutorial: () -> Unit,
    onClearHistory: () -> Unit,
    onBack: () -> Unit
) {
    val current = settings ?: SettingsEntity()
    var showClearHistoryConfirm by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CÀI ĐẶT HỆ THỐNG",
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
                            .testTag("settings_back_button")
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

            // Sound & Effects
            SectionHeader("ÂM THANH & RUNG")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                border = BorderStroke(1.5.dp, OceanBorder),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingToggleRow(
                        title = "Hiệu ứng âm thanh khi đánh cờ",
                        subtitle = "Phát tiếng cờ gõ, thắng thua, báo giờ",
                        checked = current.soundEnabled,
                        onCheckedChange = { onUpdateSettings(current.copy(soundEnabled = it)) }
                    )
                    SettingToggleRow(
                        title = "Rung phản hồi (Haptic)",
                        subtitle = "Rung nhẹ khi chạm đặt quân cờ",
                        checked = current.vibrationEnabled,
                        onCheckedChange = { onUpdateSettings(current.copy(vibrationEnabled = it)) }
                    )
                    SettingToggleRow(
                        title = "Hiệu ứng hoạt họa bàn cờ",
                        subtitle = "Tia sáng và hiệu ứng đường thắng 5 quân",
                        checked = current.boardEffects,
                        onCheckedChange = { onUpdateSettings(current.copy(boardEffects = it)) }
                    )
                }
            }

            // Quick Links to Sub-screens
            SectionHeader("TÙY CHỈNH NÂNG CAO")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                border = BorderStroke(1.5.dp, OceanBorder),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionNavRow(
                        title = "Tùy biến Giao diện Bàn cờ & Quân cờ",
                        subtitle = "Đại dương xanh, Gỗ cổ điển, Tối giản, Cyber",
                        tag = "GIAO DIỆN",
                        onClick = onOpenBoardCustomization
                    )
                    ActionNavRow(
                        title = "Sao lưu & Khôi phục Dữ liệu",
                        subtitle = "Lưu / tải file JSON dữ liệu SQLite",
                        tag = "SAO LƯU",
                        onClick = onOpenBackupRestore
                    )
                    ActionNavRow(
                        title = "Hướng dẫn Luật chơi Caro & Mẹo thắng",
                        subtitle = "Quy tắc 5 quân, chặn 2 đầu, nước đôi",
                        tag = "HƯỚNG DẪN",
                        onClick = onShowTutorial
                    )
                }
            }

            // Data Management
            SectionHeader("QUẢN LÝ DỮ LIỆU")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                border = BorderStroke(1.5.dp, PieceRedX.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(2.dp)
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
                            text = "Xóa toàn bộ Lịch sử Trận đấu",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = PieceRedX
                        )
                        Text(
                            text = "Dọn sạch danh sách các ván đấu đã lưu",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextBlackMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PieceRedX.copy(alpha = 0.1f))
                            .border(1.dp, PieceRedX, RoundedCornerShape(8.dp))
                            .clickable { showClearHistoryConfirm = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("XÓA", fontSize = 11.sp, fontWeight = FontWeight.Black, color = PieceRedX)
                    }
                }
            }

            // App Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = OceanIce),
                border = BorderStroke(1.dp, OceanBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GAME CỜ CARO THÔNG MINH v1.0.0",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = OceanDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Trí tuệ nhân tạo AI & Lưu trữ Offline SQLite",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlackSecondary
                    )
                    Text(
                        text = "Phát triển bởi Nguyễn Vũ Xuân Kiên",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OceanDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Native Android (Kotlin & Jetpack Compose)",
                        fontSize = 10.sp,
                        color = TextBlackMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showClearHistoryConfirm) {
        AlertDialog(
            onDismissRequest = { showClearHistoryConfirm = false },
            title = { Text("XÁC NHẬN XÓA LỊCH SỬ?", fontWeight = FontWeight.Black, color = TextBlackSolid) },
            text = { Text("Toàn bộ lịch sử ván cờ sẽ bị xóa sạch khỏi cơ sở dữ liệu SQLite.", color = TextBlackPure) },
            confirmButton = {
                Button(
                    onClick = {
                        showClearHistoryConfirm = false
                        onClearHistory()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PieceRedX)
                ) {
                    Text("XÓA", fontWeight = FontWeight.Black, color = WhitePure)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryConfirm = false }) {
                    Text("HỦY", fontWeight = FontWeight.Black, color = TextBlackSolid)
                }
            }
        )
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

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextBlackSolid)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TextBlackSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = WhitePure,
                checkedTrackColor = OceanDark,
                uncheckedTrackColor = OceanIce,
                uncheckedThumbColor = TextBlackMuted
            )
        )
    }
}

@Composable
private fun ActionNavRow(
    title: String,
    subtitle: String,
    tag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextBlackSolid)
            Text(text = subtitle, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TextBlackSecondary)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(OceanIce)
                .border(1.dp, OceanBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(text = tag, fontSize = 10.sp, fontWeight = FontWeight.Black, color = OceanDark)
        }
    }
}
