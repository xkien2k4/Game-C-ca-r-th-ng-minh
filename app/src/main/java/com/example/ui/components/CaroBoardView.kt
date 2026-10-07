package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.logic.MoveRecord
import com.example.logic.PlayerPiece
import com.example.logic.WinningLine
import com.example.ui.theme.BoardBgClean
import com.example.ui.theme.BoardGridSlate
import com.example.ui.theme.BoardStarDot
import com.example.ui.theme.PieceCyanO
import com.example.ui.theme.PieceRedX
import com.example.ui.theme.SoftOceanBlueDark
import com.example.ui.theme.SoftOceanBlueLight
import com.example.ui.theme.SoftOceanBluePrimary
import com.example.ui.theme.SoftOceanBorder
import com.example.ui.theme.TrophyGold
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhitePure
import kotlin.math.floor

@Composable
fun CaroBoardView(
    boardSize: Int,
    boardState: Array<Array<PlayerPiece?>>,
    lastMove: MoveRecord?,
    winningLine: WinningLine?,
    hintCell: Pair<Int, Int>? = null,
    pieceStyle: String = "MODERN",
    boardEffects: Boolean = true,
    isInteractive: Boolean,
    isDarkTheme: Boolean,
    onCellClicked: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember(boardSize) { mutableFloatStateOf(1f) }
    var offset by remember(boardSize) { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 3.5f)
        if (scale > 1f) {
            val maxOffset = (scale - 1f) * 400f
            offset = Offset(
                x = (offset.x + offsetChange.x).coerceIn(-maxOffset, maxOffset),
                y = (offset.y + offsetChange.y).coerceIn(-maxOffset, maxOffset)
            )
        } else {
            offset = Offset.Zero
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "boardPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val boardBgColor = WhitePure
    val gridLineColor = BoardGridSlate
    val starDotColor = BoardStarDot

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(boardBgColor)
            .border(
                width = 2.dp,
                color = SoftOceanBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("caro_board_container"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
                .transformable(state = transformState)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(boardSize, isInteractive, scale, offset) {
                        if (isInteractive) {
                            detectTapGestures { tapOffset ->
                                val totalSize = size.width.toFloat()
                                val margin = totalSize * 0.04f
                                val boardActiveWidth = totalSize - 2 * margin
                                val cellSize = boardActiveWidth / boardSize

                                val adjustedX = (tapOffset.x - margin)
                                val adjustedY = (tapOffset.y - margin)

                                val col = floor(adjustedX / cellSize).toInt()
                                val row = floor(adjustedY / cellSize).toInt()

                                if (row in 0 until boardSize && col in 0 until boardSize) {
                                    onCellClicked(row, col)
                                }
                            }
                        }
                    }
                    .testTag("caro_board_canvas")
            ) {
                val canvasWidth = size.width
                val margin = canvasWidth * 0.04f
                val activeSize = canvasWidth - 2 * margin
                val cellSize = activeSize / boardSize

                // 1. Grid Lines
                for (i in 0..boardSize) {
                    val pos = margin + i * cellSize
                    drawLine(
                        color = gridLineColor,
                        start = Offset(pos, margin),
                        end = Offset(pos, margin + activeSize),
                        strokeWidth = if (i == 0 || i == boardSize) 2.2f else 1.0f
                    )
                    drawLine(
                        color = gridLineColor,
                        start = Offset(margin, pos),
                        end = Offset(margin + activeSize, pos),
                        strokeWidth = if (i == 0 || i == boardSize) 2.2f else 1.0f
                    )
                }

                // 2. Star Reference Dots
                val starPoints = getStarPoints(boardSize)
                for ((sr, sc) in starPoints) {
                    val cx = margin + (sc + 0.5f) * cellSize
                    val cy = margin + (sr + 0.5f) * cellSize
                    drawCircle(
                        color = starDotColor,
                        radius = cellSize * 0.08f,
                        center = Offset(cx, cy)
                    )
                }

                // 3. Hint Highlight
                if (hintCell != null && hintCell.first in 0 until boardSize && hintCell.second in 0 until boardSize) {
                    val hcx = margin + (hintCell.second + 0.5f) * cellSize
                    val hcy = margin + (hintCell.first + 0.5f) * cellSize
                    drawCircle(
                        color = SoftOceanBlueLight.copy(alpha = 0.6f * glowAlpha),
                        radius = cellSize * 0.46f,
                        center = Offset(hcx, hcy)
                    )
                    drawCircle(
                        color = SoftOceanBlueDark,
                        radius = cellSize * 0.44f,
                        center = Offset(hcx, hcy),
                        style = Stroke(width = 3f)
                    )
                }

                // 4. Placed Pieces
                val isModern = pieceStyle == "MODERN"
                for (r in 0 until boardSize) {
                    for (c in 0 until boardSize) {
                        val piece = boardState[r][c] ?: continue
                        val cx = margin + (c + 0.5f) * cellSize
                        val cy = margin + (r + 0.5f) * cellSize
                        val pieceRadius = cellSize * 0.38f

                        if (piece == PlayerPiece.X) {
                            drawPieceX(cx, cy, pieceRadius, isModern, boardEffects)
                        } else {
                            drawPieceO(cx, cy, pieceRadius, isModern, boardEffects)
                        }
                    }
                }

                // 5. Last Move Glow Indicator
                if (boardEffects && lastMove != null && lastMove.row in 0 until boardSize && lastMove.col in 0 until boardSize) {
                    val cx = margin + (lastMove.col + 0.5f) * cellSize
                    val cy = margin + (lastMove.row + 0.5f) * cellSize
                    val markerRadius = cellSize * 0.44f

                    val glowColor = if (lastMove.piece == PlayerPiece.X) PieceRedX else PieceCyanO
                    drawCircle(
                        color = glowColor.copy(alpha = 0.2f * glowAlpha),
                        radius = markerRadius * 1.15f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = glowColor.copy(alpha = glowAlpha),
                        radius = markerRadius,
                        center = Offset(cx, cy),
                        style = Stroke(width = 2.5f)
                    )
                }

                // 6. Winning 5-in-a-row Line
                if (winningLine != null && winningLine.cells.isNotEmpty()) {
                    val sortedCells = winningLine.cells
                    for ((wr, wc) in sortedCells) {
                        val cx = margin + (wc + 0.5f) * cellSize
                        val cy = margin + (wr + 0.5f) * cellSize
                        drawCircle(
                            color = TrophyGold.copy(alpha = 0.4f),
                            radius = cellSize * 0.46f,
                            center = Offset(cx, cy)
                        )
                        drawCircle(
                            color = TrophyGold,
                            radius = cellSize * 0.46f,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3.5f)
                        )
                    }

                    val firstCell = sortedCells.first()
                    val lastCell = sortedCells.last()
                    val startOffset = Offset(
                        margin + (firstCell.second + 0.5f) * cellSize,
                        margin + (firstCell.first + 0.5f) * cellSize
                    )
                    val endOffset = Offset(
                        margin + (lastCell.second + 0.5f) * cellSize,
                        margin + (lastCell.first + 0.5f) * cellSize
                    )

                    drawLine(
                        color = TrophyGold,
                        start = startOffset,
                        end = endOffset,
                        strokeWidth = cellSize * 0.16f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawPieceX(cx: Float, cy: Float, radius: Float, isModern: Boolean, effects: Boolean) {
    val strokeWidth = if (isModern) radius * 0.42f else radius * 0.32f
    val offsetDist = radius * 0.72f

    if (effects) {
        drawLine(
            color = Color.Black.copy(alpha = 0.15f),
            start = Offset(cx - offsetDist + 1.2f, cy - offsetDist + 1.2f),
            end = Offset(cx + offsetDist + 1.2f, cy + offsetDist + 1.2f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.Black.copy(alpha = 0.15f),
            start = Offset(cx + offsetDist + 1.2f, cy - offsetDist + 1.2f),
            end = Offset(cx - offsetDist + 1.2f, cy + offsetDist + 1.2f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }

    val xColor = if (isModern) PieceRedX else Color(0xFFDC2626)
    drawLine(
        color = xColor,
        start = Offset(cx - offsetDist, cy - offsetDist),
        end = Offset(cx + offsetDist, cy + offsetDist),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
    drawLine(
        color = xColor,
        start = Offset(cx + offsetDist, cy - offsetDist),
        end = Offset(cx - offsetDist, cy + offsetDist),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawPieceO(cx: Float, cy: Float, radius: Float, isModern: Boolean, effects: Boolean) {
    val strokeWidth = if (isModern) radius * 0.42f else radius * 0.32f

    if (effects) {
        drawCircle(
            color = Color.Black.copy(alpha = 0.15f),
            radius = radius * 0.85f,
            center = Offset(cx + 1.2f, cy + 1.2f),
            style = Stroke(width = strokeWidth)
        )
    }

    val oColor = if (isModern) PieceCyanO else Color(0xFF0284C7)
    drawCircle(
        color = oColor,
        radius = radius * 0.85f,
        center = Offset(cx, cy),
        style = Stroke(width = strokeWidth)
    )
}

private fun getStarPoints(boardSize: Int): List<Pair<Int, Int>> {
    return when (boardSize) {
        15 -> listOf(
            Pair(3, 3), Pair(3, 11), Pair(7, 7), Pair(11, 3), Pair(11, 11)
        )
        19 -> listOf(
            Pair(3, 3), Pair(3, 9), Pair(3, 15),
            Pair(9, 3), Pair(9, 9), Pair(9, 15),
            Pair(15, 3), Pair(15, 9), Pair(15, 15)
        )
        10 -> listOf(
            Pair(2, 2), Pair(2, 7), Pair(7, 2), Pair(7, 7)
        )
        else -> listOf(Pair(boardSize / 2, boardSize / 2))
    }
}
