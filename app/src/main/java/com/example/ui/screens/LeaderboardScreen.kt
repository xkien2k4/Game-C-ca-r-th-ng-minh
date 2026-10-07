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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.PlayerEntity
import com.example.ui.components.PlayerAvatar
import com.example.ui.theme.OceanBackground
import com.example.ui.theme.OceanBorder
import com.example.ui.theme.OceanBorderStrong
import com.example.ui.theme.OceanDark
import com.example.ui.theme.OceanHeroGradient
import com.example.ui.theme.OceanIce
import com.example.ui.theme.OceanPastel
import com.example.ui.theme.OceanPrimary
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
import com.example.ui.theme.WinStreakFlame
import com.example.ui.theme.WinStreakGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    player: PlayerEntity?,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    val rating = player?.rating ?: 1000
    val totalGames = player?.totalGames ?: 0
    val wins = player?.wins ?: 0
    val losses = player?.losses ?: 0
    val draws = player?.draws ?: 0
    val winStreak = player?.winStreak ?: 0
    val maxWinStreak = player?.maxWinStreak ?: 0
    val level = player?.level ?: 1

    val winRate = if (totalGames > 0) (wins.toFloat() / totalGames.toFloat() * 100).toInt() else 0

    val tierName = when {
        rating >= 2000 -> "HUYỀN THOẠI CỜ"
        rating >= 1700 -> "ĐẠI SƯ CARO"
        rating >= 1400 -> "CAO THỦ TINH ANH"
        rating >= 1200 -> "KỲ THỦ GIỎI"
        else -> "TẬP SỰ CARO"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BẢNG XẾP HẠNG & ĐẲNG CẤP",
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
                            .testTag("leaderboard_back_button")
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

            // Player Rank Hero Card
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

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PlayerAvatar(
                                    avatarKey = player?.avatar ?: "MASTER",
                                    size = 48,
                                    borderCol = OceanDark
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = player?.name ?: "Kỳ Thủ",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = TextBlackSolid
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(OceanHeroGradient)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = tierName,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp,
                                            color = TextWhitePure
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$rating",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = OceanDark
                                )
                                Text(
                                    text = "ELO HIỆN TẠI",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextBlackMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatMiniTile("CẤP ĐỘ", "Cấp $level")
                            StatMiniTile("TỶ LỆ THẮNG", "$winRate%")
                            StatMiniTile("CHUỖI CAO NHẤT", "$maxWinStreak ván")
                        }
                    }
                }
            }

            // Leaderboard Mock Top Ladder
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(14.dp)
                        .background(OceanDark, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "BẢNG XẾP HẠNG OFFLINE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = TextBlackSolid,
                    letterSpacing = 0.5.sp
                )
            }

            val ladderPlayers = listOf(
                Triple("Đại Sư AlphaCaro", 2250, "Cấp 25"),
                Triple("Kiện Tướng Rồng Vàng", 1980, "Cấp 20"),
                Triple(player?.name ?: "Bạn", rating, "Cấp $level"),
                Triple("Kỳ Thủ Sấm Sét", 1420, "Cấp 14"),
                Triple("Chiến Binh Ninja", 1250, "Cấp 10"),
                Triple("Tập Sự Tân Thủ", 1000, "Cấp 1")
            ).sortedByDescending { it.second }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ladderPlayers.forEachIndexed { index, (name, elo, lvl) ->
                    val isCurrentPlayer = name == (player?.name ?: "Bạn")
                    val rankNum = index + 1
                    val rankColor = when (rankNum) {
                        1 -> TrophyGold
                        2 -> PieceCyanO
                        3 -> PieceRedX
                        else -> TextBlackSecondary
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrentPlayer) OceanIce else WhiteCard
                        ),
                        border = BorderStroke(
                            width = if (isCurrentPlayer) 2.dp else 1.dp,
                            color = if (isCurrentPlayer) OceanDark else OceanBorder
                        ),
                        elevation = CardDefaults.cardElevation(if (isCurrentPlayer) 2.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .then(if (rankNum <= 3) Modifier.background(OceanHeroGradient) else Modifier.background(OceanIce))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#$rankNum",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = if (rankNum <= 3) TextWhitePure else TextBlackSolid
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = name,
                                            fontWeight = if (isCurrentPlayer) FontWeight.Black else FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextBlackSolid
                                        )
                                        if (isCurrentPlayer) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(OceanDark)
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "BẠN",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = TextWhitePure
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = lvl,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextBlackSecondary
                                    )
                                }
                            }

                            Text(
                                text = "$elo ELO",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = OceanDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StatMiniTile(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlackMuted
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = TextBlackSolid
        )
    }
}
