package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.repository.GameSummaryResult
import com.example.logic.PlayerPiece
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.OceanBorder
import com.example.ui.theme.OceanBorderLight
import com.example.ui.theme.OceanBorderStrong
import com.example.ui.theme.OceanButtonGradient
import com.example.ui.theme.OceanDark
import com.example.ui.theme.OceanDeep
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

val AVAILABLE_AVATARS = listOf(
    Pair("MASTER", "ĐẠI SƯ"),
    Pair("DRAGON", "RỒNG"),
    Pair("TIGER", "MÃNH HỔ"),
    Pair("PHOENIX", "PHƯỢNG"),
    Pair("NINJA", "NINJA"),
    Pair("SAMURAI", "SAMURAI"),
    Pair("CHAMPION", "QUÁN QUÂN"),
    Pair("ELITE", "TINH ANH"),
    Pair("WARRIOR", "CHIẾN BINH")
)

fun getAvatarLabel(avatarKey: String): String {
    return AVAILABLE_AVATARS.find { it.first == avatarKey }?.second ?: "KỲ THỦ"
}

@Composable
fun PlayerAvatar(
    avatarKey: String,
    modifier: Modifier = Modifier,
    size: Int = 48,
    borderCol: Color = OceanDark
) {
    val label = getAvatarLabel(avatarKey)
    val shortCode = if (label.length > 3) label.take(2).uppercase() else label

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(OceanIce)
            .border(2.dp, borderCol, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = shortCode,
            fontSize = (size * 0.28).sp,
            fontWeight = FontWeight.Black,
            color = TextBlackPure,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun InGamePlayerCard(
    name: String,
    piece: PlayerPiece,
    avatarKey: String,
    isCurrentTurn: Boolean,
    timeRemaining: Int?,
    rating: Int?,
    modifier: Modifier = Modifier
) {
    val pieceColor = if (piece == PlayerPiece.X) PieceRedX else PieceCyanO
    val isLowTime = timeRemaining != null && timeRemaining <= 5 && isCurrentTurn

    val borderModifier = if (isCurrentTurn) {
        Modifier.border(
            width = 2.dp,
            color = if (isLowTime) PieceRedX else OceanBorderStrong,
            shape = RoundedCornerShape(16.dp)
        )
    } else {
        Modifier.border(
            width = 1.dp,
            color = OceanBorder,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Card(
        modifier = modifier.then(borderModifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentTurn) OceanIce else WhiteCard
        ),
        elevation = CardDefaults.cardElevation(if (isCurrentTurn) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                PlayerAvatar(
                    avatarKey = avatarKey,
                    size = 36,
                    borderCol = pieceColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "[ ${piece.symbol} ]",
                            fontWeight = FontWeight.Black,
                            color = pieceColor,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextBlackSolid,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (rating != null) {
                        Text(
                            text = "$rating Elo",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OceanDeep
                        )
                    }
                }
            }

            if (timeRemaining != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isLowTime) PieceRedX.copy(alpha = 0.15f) else OceanPastel)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${timeRemaining}s",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isLowTime) PieceRedX else TextBlackPure
                    )
                }
            } else if (isCurrentTurn) {
                Text(
                    text = "LƯỢT ĐI",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = TextWhitePure,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(OceanDark)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun GameResultDialog(
    winner: String,
    humanSymbol: String,
    mode: String,
    summary: GameSummaryResult?,
    onPlayAgain: () -> Unit,
    onViewHistory: () -> Unit,
    onHome: () -> Unit
) {
    val isHumanWinner = (winner == humanSymbol) || (winner == "TIMEOUT_WIN")
    val isDraw = winner == "DRAW"

    val title = when {
        isDraw -> "KẾT QUẢ: HÒA CỜ"
        mode == "LOCAL" -> "QUÂN $winner CHIẾN THẮNG"
        isHumanWinner -> "CHIẾN THẮNG XUẤT SẮC"
        else -> "KẾT QUẢ: THẤT BẠI"
    }

    val subtitle = when {
        isDraw -> "Hai bên bất phân thắng bại"
        mode == "LOCAL" -> "Đã hoàn thành chuỗi 5 quân liên tiếp"
        isHumanWinner -> "Bạn đã đánh bại đối thủ với 5 quân liên tiếp"
        else -> "Đối thủ đã hoàn thành chuỗi 5 quân trước"
    }

    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = WhitePure,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, OceanBorderStrong, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .then(if (isHumanWinner) Modifier.background(OceanHeroGradient) else Modifier.background(OceanIce))
                        .border(1.dp, OceanBorderStrong, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isHumanWinner) TextWhitePure else TextBlackSolid,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextBlackPure,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (summary != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = OceanIce),
                            border = BorderStroke(1.5.dp, OceanBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "RATING ELO", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextBlackPure)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${summary.newRating}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = TextBlackSolid
                                    )
                                    val rChange = summary.ratingChange
                                    Text(
                                        text = if (rChange >= 0) " (+$rChange)" else " ($rChange)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (rChange >= 0) PieceCyanO else PieceRedX
                                    )
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = OceanIce),
                            border = BorderStroke(1.5.dp, OceanBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "KINH NGHIỆM", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextBlackPure)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "+${summary.expGained} EXP",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = TrophyGold
                                )
                            }
                        }
                    }

                    if (summary.unlockedAchievements.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(OceanIce)
                                .border(1.dp, OceanBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "MỞ KHÓA ${summary.unlockedAchievements.size} THÀNH TÍCH MỚI 🔥",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = TextBlackSolid,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OceanHeroGradient)
                        .clickable { onPlayAgain() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CHƠI TRẬN MỚI",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = TextWhitePure
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewHistory,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, OceanBorder)
                    ) {
                        Text(text = "LỊCH SỬ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextBlackSolid)
                    }

                    OutlinedButton(
                        onClick = onHome,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, OceanBorder)
                    ) {
                        Text(text = "TRANG CHỦ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextBlackSolid)
                    }
                }
            }
        }
    }
}

@Composable
fun AvatarPickerDialog(
    currentAvatar: String,
    onAvatarSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "CHỌN DANH XƯNG ĐẠI DIỆN",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = TextBlackSolid
            )
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(AVAILABLE_AVATARS) { (key, label) ->
                    val isSelected = key == currentAvatar
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) OceanIce else WhitePure)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) OceanDark else OceanBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onAvatarSelected(key)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) OceanDark else TextBlackSolid,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = OceanIce)
            ) {
                Text("ĐÓNG", fontWeight = FontWeight.Black, color = TextBlackSolid)
            }
        }
    )
}
