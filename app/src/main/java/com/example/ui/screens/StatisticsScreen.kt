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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.DetailedStatsSummary
import com.example.data.repository.DifficultyStats
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
import com.example.ui.theme.WinStreakFlame
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    mainViewModel: MainViewModel,
    onBack: () -> Unit
) {
    val stats by mainViewModel.detailedStats.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        mainViewModel.refreshStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "THỐNG KÊ CHI TIẾT (SQLITE)",
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
                            .testTag("stats_back_button")
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
        if (stats == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = OceanDark)
            }
        } else {
            val s = stats!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(modifier = Modifier.height(2.dp))

                // Overview Hero Card
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
                            Text(
                                text = "TỔNG QUAN CHIẾN TÍCH",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = TextBlackSolid
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatBox("TỔNG TRẬN", "${s.totalGames}", TextBlackSolid)
                                StatBox("THẮNG", "${s.wins}", PieceCyanO)
                                StatBox("THUA", "${s.losses}", PieceRedX)
                                StatBox("HÒA", "${s.draws}", TextBlackSecondary)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatBox("TỶ LỆ THẮNG", "${s.winRate}%", OceanDark)
                                StatBox("TỔNG NƯỚC ĐI", "${s.totalMoves}", TextBlackSolid)
                                StatBox("CHUỖI KỶ LỤC", "${s.maxWinStreak}", WinStreakFlame)
                                StatBox("ELO CAO NHẤT", "${s.maxRating}", TrophyGold)
                            }
                        }
                    }
                }

                // AI Stats by Difficulty
                SectionTitle("KẾT QUẢ ĐẤU VỚI MÁY THEO CẤP ĐỘ")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    s.difficultyStats.forEach { diff ->
                        val diffTitle = when (diff.difficulty.uppercase()) {
                            "EASY" -> "DỄ (TẬP SỰ)"
                            "MEDIUM" -> "TRUNG BÌNH (CHIẾN THUẬT)"
                            "HARD" -> "KHÓ (KIỆN TƯỚNG)"
                            "MASTER" -> "CAO THỦ (ALPHA-BETA)"
                            else -> diff.difficulty
                        }
                        DifficultyStatCard(diffTitle, diff)
                    }
                }

                // Moves & Duration stats
                SectionTitle("THÔNG SỐ NƯỚC ĐI & THỜI LƯỢNG")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WhitePure),
                    border = BorderStroke(1.5.dp, OceanBorder),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailRow("Tổng số nước cờ đã đi:", "${s.totalMoves} nước")
                        DetailRow("Trung bình nước đi / ván:", "${(s.avgMovesPerGame * 10).toInt() / 10f} nước")
                        DetailRow("Thời lượng trung bình mỗi ván:", formatDuration(s.avgDurationSeconds))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextBlackMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = color)
    }
}

@Composable
private fun SectionTitle(title: String) {
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
private fun DifficultyStatCard(title: String, diffStats: DifficultyStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WhitePure),
        border = BorderStroke(1.dp, OceanBorder),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Black, fontSize = 12.sp, color = TextBlackSolid)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${diffStats.wins} Thắng • ${diffStats.losses} Thua • ${diffStats.draws} Hòa (${diffStats.totalGames} ván)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextBlackSecondary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(OceanIce)
                    .border(1.dp, OceanBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${diffStats.winRate}% THẮNG",
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    color = OceanDark
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextBlackSecondary)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextBlackSolid)
    }
}

private fun formatDuration(seconds: Long): String {
    val mins = seconds / 60
    val hours = mins / 60
    val remMins = mins % 60
    return if (hours > 0) "${hours}h ${remMins}m" else "${mins} phút"
}
