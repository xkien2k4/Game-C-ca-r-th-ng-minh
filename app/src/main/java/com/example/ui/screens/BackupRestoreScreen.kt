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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
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
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    mainViewModel: MainViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var backupJsonText by remember { mutableStateOf("") }
    var restoreInputText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessStatus by remember { mutableStateOf(true) }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SAO LƯU & KHÔI PHỤC SQLITE",
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
                            .testTag("backup_back_button")
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

            // Export Section
            SectionHeader("XUẤT DỮ LIỆU SAO LƯU (EXPORT JSON)")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                border = BorderStroke(1.5.dp, OceanBorder),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Xuất toàn bộ dữ liệu kỳ thủ, lịch sử ván đấu, thành tích và cài đặt sang chuỗi JSON an toàn.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextBlackSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(OceanHeroGradient)
                            .clickable {
                                coroutineScope.launch {
                                    val json = mainViewModel.exportBackupJson()
                                    backupJsonText = json
                                    statusMessage = "Đã xuất dữ liệu SQLite thành công!"
                                    isSuccessStatus = true
                                }
                            }
                            .testTag("backup_export_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("TẠO MÃ SAO LƯU JSON", fontWeight = FontWeight.Black, fontSize = 12.sp, color = TextWhitePure)
                    }

                    if (backupJsonText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = backupJsonText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Dữ liệu JSON Sao lưu", fontWeight = FontWeight.Bold, color = OceanDark) },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp)
                        )
                    }
                }
            }

            // Import Section
            SectionHeader("KHÔI PHỤC DỮ LIỆU (IMPORT JSON)")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                border = BorderStroke(1.5.dp, OceanBorder),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Dán chuỗi JSON sao lưu để nạp lại toàn bộ hồ sơ, thành tích và lịch sử ván cờ vào SQLite.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextBlackSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = restoreInputText,
                        onValueChange = { restoreInputText = it },
                        placeholder = { Text("Dán chuỗi JSON đã sao lưu vào đây...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("backup_import_input"),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (restoreInputText.isNotBlank()) OceanDark else OceanIce)
                            .clickable(enabled = restoreInputText.isNotBlank()) {
                                showRestoreConfirmDialog = true
                            }
                            .testTag("backup_import_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "KHÔI PHỤC VÀO CƠ SỞ DỮ LIỆU",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = if (restoreInputText.isNotBlank()) TextWhitePure else TextBlackMuted
                        )
                    }
                }
            }

            // Status message
            if (statusMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSuccessStatus) EmeraldGreen.copy(alpha = 0.1f) else PieceRedX.copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(1.dp, if (isSuccessStatus) EmeraldGreen else PieceRedX)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(10.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = statusMessage!!,
                            color = if (isSuccessStatus) EmeraldGreen else PieceRedX,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirmDialog = false },
            title = { Text("XÁC NHẬN KHÔI PHỤC?", fontWeight = FontWeight.Black, color = TextBlackSolid) },
            text = { Text("Dữ liệu hiện tại sẽ được cập nhật/thay thế bằng dữ liệu từ chuỗi JSON sao lưu.", color = TextBlackPure) },
            confirmButton = {
                Button(
                    onClick = {
                        showRestoreConfirmDialog = false
                        coroutineScope.launch {
                            val success = mainViewModel.restoreBackupJson(restoreInputText)
                            isSuccessStatus = success
                            statusMessage = if (success) "Khôi phục dữ liệu thành công!" else "Lỗi: Định dạng JSON không hợp lệ!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OceanDark)
                ) {
                    Text("TIẾN HÀNH", fontWeight = FontWeight.Black, color = WhitePure)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirmDialog = false }) {
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
