package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.OceanBorder
import com.example.ui.theme.OceanBorderStrong
import com.example.ui.theme.OceanDark
import com.example.ui.theme.OceanHeroGradient
import com.example.ui.theme.OceanIce
import com.example.ui.theme.OceanPastel
import com.example.ui.theme.TextBlackPure
import com.example.ui.theme.TextBlackSecondary
import com.example.ui.theme.TextBlackSolid
import com.example.ui.theme.TextWhitePure
import com.example.ui.theme.WhiteCard
import com.example.ui.theme.WhitePure

@Composable
fun TutorialDialog(
    onDismiss: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 5

    val steps = listOf(
        Pair(
            "BÀN CỜ CARO CHUẨN",
            "Trò chơi diễn ra trên bàn cờ dạng lưới vuông (11x11, 15x15 hoặc 19x19). Bạn có thể dùng 2 ngón tay để phóng to/thu nhỏ và vuốt di chuyển bàn cờ thoải mái."
        ),
        Pair(
            "QUY TẮC ĐẶT QUÂN",
            "Hai bên lần lượt đặt quân cờ X và O vào các ô còn trống. Người cầm quân X sẽ được quyền đi trước. Không thể đặt quân vào ô đã có người đánh."
        ),
        Pair(
            "MỤC TIÊU 5 QUÂN LIÊN TIẾP",
            "Người chơi nào tạo được chuỗi 5 quân cờ cùng loại liên tiếp không bị chặn (theo hàng ngang, hàng dọc hoặc đường chéo) sẽ giành chiến thắng ngay lập tức!"
        ),
        Pair(
            "ĐẤU TRÍ VỚI MÁY (AI)",
            "Hệ thống AI tích hợp 4 cấp độ thông minh từ Dễ đến Cao thủ (sử dụng thuật toán Minimax kết hợp Alpha-Beta Pruning). Hãy thử thách kỹ năng của bạn!"
        ),
        Pair(
            "TÍNH NĂNG HOÀN TÁC & GỢI Ý",
            "Sử dụng nút [ HOÀN ] (tối đa 3 lần/trận) để sửa sai nước đi, hoặc nút [ GỢI Ý ] (tối đa 3 lần/trận) để AI phân tích và đề xuất ô đánh tối ưu nhất."
        )
    )

    val currentContent = steps[currentStep - 1]

    Dialog(onDismissRequest = onDismiss) {
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
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Step Indicator Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(OceanIce)
                        .border(1.dp, OceanBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "BƯỚC $currentStep / $totalSteps",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = OceanDark
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = currentContent.first,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = TextBlackSolid,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(OceanIce)
                        .border(1.dp, OceanBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = currentContent.second,
                        fontSize = 12.sp,
                        color = TextBlackPure,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.weight(1f).testTag("tutorial_prev_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, OceanBorder)
                        ) {
                            Text("TRƯỚC", fontWeight = FontWeight.Black, color = TextBlackSolid, fontSize = 11.sp)
                        }
                    }

                    if (currentStep < totalSteps) {
                        Button(
                            onClick = { currentStep++ },
                            modifier = Modifier.weight(1f).testTag("tutorial_next_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OceanDark)
                        ) {
                            Text("TIẾP THEO", fontWeight = FontWeight.Black, color = TextWhitePure, fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).testTag("tutorial_finish_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OceanDark)
                        ) {
                            Text("HOÀN TẤT", fontWeight = FontWeight.Black, color = TextWhitePure, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
