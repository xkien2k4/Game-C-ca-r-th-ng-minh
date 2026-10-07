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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entity.AchievementEntity
import com.example.data.entity.PlayerEntity
import com.example.ui.components.PlayerAvatar
import com.example.ui.theme.OceanBackground
import com.example.ui.theme.OceanBorder
import com.example.ui.theme.OceanBorderLight
import com.example.ui.theme.OceanBorderStrong
import com.example.ui.theme.OceanButtonGradient
import com.example.ui.theme.OceanCardGradient
import com.example.ui.theme.OceanDark
import com.example.ui.theme.OceanDeep
import com.example.ui.theme.OceanDeepNavy
import com.example.ui.theme.OceanHeroGradient
import com.example.ui.theme.OceanIce
import com.example.ui.theme.OceanPastel
import com.example.ui.theme.OceanPrimary
import com.example.ui.theme.OceanSoft
import com.example.ui.theme.PieceCyanO
import com.example.ui.theme.PieceRedX
import com.example.ui.theme.TextBlackMuted
import com.example.ui.theme.TextBlackPure
import com.example.ui.theme.TextBlackSecondary
import com.example.ui.theme.TextBlackSolid
import com.example.ui.theme.TextOceanDark
import com.example.ui.theme.TextWhitePure
import com.example.ui.theme.TrophyGold
import com.example.ui.theme.WhiteCard
import com.example.ui.theme.WhitePure
import com.example.ui.theme.WinStreakFlame
import com.example.ui.theme.WinStreakGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    player: PlayerEntity?,
    achievements: List<AchievementEntity>,
    onPlayAiClicked: () -> Unit,
    onPlayLocalClicked: () -> Unit,
    onPracticeClicked: () -> Unit,
    onHistoryClicked: () -> Unit,
    onLeaderboardClicked: () -> Unit,
    onAchievementsClicked: () -> Unit,
    onStatisticsClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    val scrollState = rememberScrollState()

    val playerName = player?.name ?: "Kỳ Thủ Caro"
    val playerAvatar = player?.avatar ?: "MASTER"
    val playerRating = player?.rating ?: 1000
    val playerLevel = player?.level ?: 1
    val playerExp = player?.experience ?: 0
    val expNeeded = (playerLevel * 500).coerceAtLeast(1)
    val expProgress = (playerExp.toFloat() / expNeeded.toFloat()).coerceIn(0f, 1f)
    val winStreak = player?.winStreak ?: 0
    val totalGames = player?.totalGames ?: 0
    val wins = player?.wins ?: 0
    val winRate = if (totalGames > 0) (wins * 100) / totalGames else 0

    val unlockedCount = achievements.count { it.unlocked }
    val totalAchievements = achievements.size.coerceAtLeast(1)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(OceanHeroGradient)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "CỜ CARO",
                                fontWeight = FontWeight.Black,
                                color = TextWhitePure,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "THÔNG MINH",
                            fontWeight = FontWeight.Black,
                            color = OceanDark,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(OceanHeroGradient)
                            .clickable { onSettingsClicked() }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("home_settings_button")
                    ) {
                        Text(
                            text = "CÀI ĐẶT",
                            fontWeight = FontWeight.Black,
                            color = TextWhitePure,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
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

            // 1. Hero Player Profile Card (Rich Ocean Blue Gradient with sharp white/gold contrast)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProfileClicked() }
                    .testTag("home_profile_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(2.dp, OceanBorderStrong),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OceanHeroGradient)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                PlayerAvatar(
                                    avatarKey = playerAvatar,
                                    size = 52,
                                    borderCol = WhitePure
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = playerName,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp,
                                        color = TextWhitePure,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(WhitePure)
                                                .padding(horizontal = 7.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "CẤP $playerLevel",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = OceanDark
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(OceanDeepNavy.copy(alpha = 0.4f))
                                                .border(1.dp, OceanSoft, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 7.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "$playerRating ELO",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = TextWhitePure
                                            )
                                        }
                                    }
                                }
                            }

                            if (winStreak > 0) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(WinStreakGradient)
                                        .border(1.5.dp, WhitePure, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "CHUỖI $winStreak 🔥",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextWhitePure
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // EXP Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "KINH NGHIỆM: $playerExp / $expNeeded",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OceanIce
                            )
                            Text(
                                text = "${(expProgress * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = TextWhitePure
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        LinearProgressIndicator(
                            progress = { expProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = WhitePure,
                            trackColor = OceanDeepNavy.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            // Quick Stats Metric Bar (3 Tiles with rich ocean styling)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickStatCard(
                    title = "TỶ LỆ THẮNG",
                    value = "$winRate%",
                    sub = "$wins / $totalGames ván",
                    badgeColor = OceanDark,
                    modifier = Modifier.weight(1f),
                    onClick = onStatisticsClicked
                )
                QuickStatCard(
                    title = "DANH HIỆU",
                    value = "$unlockedCount/$totalAchievements",
                    sub = "Đã đạt được",
                    badgeColor = TrophyGold,
                    modifier = Modifier.weight(1f),
                    onClick = onAchievementsClicked
                )
                QuickStatCard(
                    title = "ĐẤU TRÍ",
                    value = "$totalGames",
                    sub = "Tổng số trận",
                    badgeColor = OceanPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onHistoryClicked
                )
            }

            // 2. Play Modes Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(16.dp)
                            .background(OceanDark, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CHẾ ĐỘ CHƠI ĐỈNH CAO",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = TextBlackSolid,
                        letterSpacing = 0.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(OceanIce)
                        .border(1.dp, OceanBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "OFFLINE SQLITE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = OceanDark
                    )
                }
            }

            // Play AI Button Card (Distinctive Ocean Card)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlayAiClicked() }
                    .testTag("home_play_ai_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                border = BorderStroke(2.dp, OceanBorderStrong),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Top Accent bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(OceanHeroGradient)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ĐẤU VỚI MÁY (AI THÔNG MINH)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextBlackSolid
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(OceanIce)
                                        .border(1.dp, OceanBorder, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "4 CẤP ĐỘ",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OceanDark
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Dễ • Trung bình • Khó • Kiện tướng",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextBlackSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(OceanButtonGradient)
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = "CHƠI NGAY",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = TextWhitePure
                            )
                        }
                    }
                }
            }

            // Play Local Button Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlayLocalClicked() }
                    .testTag("home_play_local_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                border = BorderStroke(1.5.dp, OceanBorder),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "HAI NGƯỜI CHƠI (PASS & PLAY)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TextBlackSolid
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Đấu trí trực tiếp 1v1 trên cùng một thiết bị",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextBlackSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(OceanIce)
                            .border(1.5.dp, OceanBorderStrong, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = "THÁCH ĐẤU",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = OceanDark
                        )
                    }
                }
            }

            // Practice Mode Button Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPracticeClicked() }
                    .testTag("home_play_practice_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = WhitePure),
                border = BorderStroke(1.5.dp, OceanBorder),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "LUYỆN TẬP THẾ CỜ & GIẢI ĐỐ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TextBlackSolid
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(OceanPastel)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "10 THẾ CỜ",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = OceanDark
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tập tìm nước thắng & chặn đòn đôi",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextBlackSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(OceanIce)
                            .border(1.5.dp, OceanBorderStrong, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = "LUYỆN TẬP",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = OceanDark
                        )
                    }
                }
            }

            // 3. Features Grid Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(16.dp)
                        .background(OceanDark, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TÍNH NĂNG NỔI BẬT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = TextBlackSolid,
                    letterSpacing = 0.5.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextFeatureTile(
                    title = "LỊCH SỬ & REPLAY",
                    subtitle = "Xem lại từng nước đi chi tiết",
                    tag = "CHI TIẾT",
                    modifier = Modifier.weight(1f),
                    onClick = onHistoryClicked
                )
                TextFeatureTile(
                    title = "BẢNG XẾP HẠNG",
                    subtitle = "Danh hiệu & xếp hạng ELO",
                    tag = "TOP",
                    modifier = Modifier.weight(1f),
                    onClick = onLeaderboardClicked
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextFeatureTile(
                    title = "DANH HIỆU THÀNH TÍCH",
                    subtitle = "$unlockedCount/$totalAchievements mục tiêu đã mở",
                    tag = "THƯỞNG",
                    modifier = Modifier.weight(1f),
                    onClick = onAchievementsClicked
                )
                TextFeatureTile(
                    title = "THỐNG KÊ CHI TIẾT",
                    subtitle = "Phân tích tỷ lệ thắng và nước đi",
                    tag = "SỐ LIỆU",
                    modifier = Modifier.weight(1f),
                    onClick = onStatisticsClicked
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextFeatureTile(
                    title = "HỒ SƠ KỲ THỦ",
                    subtitle = "Tên, đại diện & cấp độ",
                    tag = "CÁ NHÂN",
                    modifier = Modifier.weight(1f),
                    onClick = onProfileClicked
                )
                TextFeatureTile(
                    title = "CÀI ĐẶT HỆ THỐNG",
                    subtitle = "Bàn cờ, âm thanh, sao lưu",
                    tag = "TÙY CHỈNH",
                    modifier = Modifier.weight(1f),
                    onClick = onSettingsClicked
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    sub: String,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        border = BorderStroke(1.5.dp, OceanBorder),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = TextBlackMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = badgeColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = sub,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = TextBlackSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TextFeatureTile(
    title: String,
    subtitle: String,
    tag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        border = BorderStroke(1.5.dp, OceanBorder),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(OceanIce)
                        .border(1.dp, OceanBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tag,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = OceanDark
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = TextBlackSolid
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = TextBlackSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
