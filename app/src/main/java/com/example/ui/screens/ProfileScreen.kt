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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.PlayerEntity
import com.example.ui.components.AvatarPickerDialog
import com.example.ui.components.PlayerAvatar
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
import com.example.ui.theme.TrophyGold
import com.example.ui.theme.WhiteCard
import com.example.ui.theme.WhitePure

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    player: PlayerEntity?,
    onUpdateProfile: (name: String, avatar: String) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    var nameInput by remember(player?.name) { mutableStateOf(player?.name ?: "Kỳ Thủ Caro") }
    var selectedAvatar by remember(player?.avatar) { mutableStateOf(player?.avatar ?: "MASTER") }
    var isAvatarPickerVisible by remember { mutableStateOf(false) }
    var isSavedMessageVisible by remember { mutableStateOf(false) }

    val level = player?.level ?: 1
    val exp = player?.experience ?: 0
    val expNeeded = level * 500
    val expProgress = if (expNeeded > 0) exp.toFloat() / expNeeded.toFloat() else 0f
    val rating = player?.rating ?: 1000
    val wins = player?.wins ?: 0
    val losses = player?.losses ?: 0
    val draws = player?.draws ?: 0
    val totalGames = player?.totalGames ?: 0
    val winRate = if (totalGames > 0) (wins.toFloat() / totalGames.toFloat() * 100).toInt() else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HỒ SƠ KỲ THỦ",
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
                            .testTag("profile_back_button")
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

            // Avatar & Edit Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                border = BorderStroke(2.dp, OceanBorderStrong),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(OceanHeroGradient)
                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PlayerAvatar(
                            avatarKey = selectedAvatar,
                            size = 68,
                            borderCol = OceanDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(OceanIce)
                                .border(1.dp, OceanBorder, RoundedCornerShape(8.dp))
                                .clickable { isAvatarPickerVisible = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("change_avatar_button")
                        ) {
                            Text(
                                text = "ĐỔI DANH XƯNG",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = OceanDark
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Tên Kỳ Thủ", fontWeight = FontWeight.Bold, color = OceanDark) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(OceanHeroGradient)
                                .clickable {
                                    onUpdateProfile(nameInput, selectedAvatar)
                                    isSavedMessageVisible = true
                                }
                                .testTag("profile_save_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "LƯU THAY ĐỔI",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = TextWhitePure
                            )
                        }

                        if (isSavedMessageVisible) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Đã lưu hồ sơ thành công vào SQLite!",
                                color = EmeraldGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Level & EXP Card
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
                        Text(text = "CẤP ĐỘ HIỆN TẠI: CẤP $level", fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextBlackSolid)
                        Text(text = "$rating ELO", fontWeight = FontWeight.Black, fontSize = 13.sp, color = OceanDark)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "EXP: $exp / $expNeeded", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TextBlackSecondary)
                        Text(text = "${(expProgress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Black, color = OceanDark)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { expProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = OceanDark,
                        trackColor = OceanIce
                    )
                }
            }

            // Match Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                border = BorderStroke(1.5.dp, OceanBorder),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "TỔNG QUAN CHIẾN ĐẤU", fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextBlackSolid)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TỔNG", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextBlackMuted)
                            Text("$totalGames", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextBlackSolid)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("THẮNG", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextBlackMuted)
                            Text("$wins", fontSize = 14.sp, fontWeight = FontWeight.Black, color = PieceCyanO)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("THUA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextBlackMuted)
                            Text("$losses", fontSize = 14.sp, fontWeight = FontWeight.Black, color = PieceRedX)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("HÒA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextBlackMuted)
                            Text("$draws", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextBlackSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TỶ LỆ THẮNG", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextBlackMuted)
                            Text("$winRate%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = OceanDark)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (isAvatarPickerVisible) {
        AvatarPickerDialog(
            currentAvatar = selectedAvatar,
            onAvatarSelected = {
                selectedAvatar = it
                isAvatarPickerVisible = false
            },
            onDismiss = { isAvatarPickerVisible = false }
        )
    }
}
