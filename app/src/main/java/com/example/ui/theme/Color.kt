package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// PALETTE XANH NƯỚC BIỂN ĐẶC SẮC & HÀI HÒA
// (Rich & Vibrant Ocean Blue Theme)
// ==========================================

// Deep Ocean Blues (Tông Xanh Biển Sâu - Dùng cho Header, Gradient, Nút Chính)
val OceanDeepNavy = Color(0xFF082F49)        // Deep navy ocean
val OceanDeep = Color(0xFF0369A1)            // Deep marine blue
val OceanDark = Color(0xFF0284C7)            // Rich azure ocean blue
val OceanPrimary = Color(0xFF0EA5E9)         // Vibrant sky ocean blue
val OceanLight = Color(0xFF38BDF8)           // Cyan-tinted ocean wave
val OceanSoft = Color(0xFF7DD3FC)            // Soft ocean blue
val OceanPastel = Color(0xFFBAE6FD)          // Pastel ocean accent
val OceanIce = Color(0xFFE0F2FE)             // Ice ocean blue container
val OceanBackground = Color(0xFFEAF5FF)      // Distinct airy ocean blue background (không bị trắng toát)
val OceanSurfaceLight = Color(0xFFF0F8FF)    // Alice ocean surface

// Clean Surfaces & Cards (Thẻ trắng viền xanh đại dương sang trọng)
val WhitePure = Color(0xFFFFFFFF)            // Pure white
val WhiteCard = Color(0xFFFFFFFF)            // Clean card surface
val OceanCardTint = Color(0xFFF4FAFF)        // Ocean tinted surface
val OceanCardSelected = Color(0xFFE0F2FE)    // Selected card highlight

// Borders & Dividers (Viền xanh nước biển sắc nét)
val OceanBorder = Color(0xFF7DD3FC)          // Crisp ocean border
val OceanBorderStrong = Color(0xFF0284C7)    // Strong ocean border
val OceanBorderLight = Color(0xFFBAE6FD)     // Subtle ocean border
val WhiteBorder = Color(0xFFE2E8F0)          // Neutral border

// Text & Typography (Chữ chức năng Đen rõ nét, Chữ Header Trắng tinh tế)
val TextBlackSolid = Color(0xFF0F172A)       // Crisp black text for functions & readability
val TextBlackPure = Color(0xFF0F172A)        // Slate-900 pure dark
val TextBlackSecondary = Color(0xFF334155)   // Slate-700 secondary dark
val TextBlackMuted = Color(0xFF64748B)       // Slate-500 muted description
val TextWhitePure = Color(0xFFFFFFFF)        // White text on ocean headers/buttons
val TextOceanDark = Color(0xFF0369A1)        // Blue accent text

// Game Pieces & Accents
val PieceRedX = Color(0xFFE11D48)            // Vibrant Crimson Ruby Red for X
val PieceCyanO = Color(0xFF0284C7)           // Vivid Ocean Azure for O
val TrophyGold = Color(0xFFF59E0B)           // Radiant Amber Trophy Gold
val WinStreakFlame = Color(0xFFF97316)       // Fiery Orange Flame
val EmeraldGreen = Color(0xFF10B981)         // Victory / Success Green

// Board Colors
val BoardBgClean = Color(0xFFF8FCFF)         // Crisp cool board surface
val BoardGridSlate = Color(0xFF94A3B8)       // Elegant grid line
val BoardStarDot = Color(0xFF0284C7)         // Star coordinates dot

// Gradients
val OceanHeroGradient = Brush.horizontalGradient(
    listOf(Color(0xFF0369A1), Color(0xFF0284C7), Color(0xFF0EA5E9))
)

val OceanCardGradient = Brush.verticalGradient(
    listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
)

val OceanButtonGradient = Brush.horizontalGradient(
    listOf(Color(0xFF0284C7), Color(0xFF0EA5E9))
)

val OceanAccentGradient = Brush.horizontalGradient(
    listOf(Color(0xFF0EA5E9), Color(0xFF38BDF8))
)

val WinStreakGradient = Brush.horizontalGradient(
    listOf(Color(0xFFF97316), Color(0xFFFB923C))
)

// Legacy Aliases for seamless compatibility
val SoftOceanBlueLightest = OceanBackground
val SoftOceanBlueLight = OceanIce
val SoftOceanBlueMedium = OceanPastel
val SoftOceanBluePrimary = OceanPrimary
val SoftOceanBlueDark = OceanDark
val SoftOceanBlueDeeper = OceanDeep
val SoftOceanBorder = OceanBorder
val SoftOceanBorderLight = OceanBorderLight
val WhiteSurface = OceanBackground
val CaroBluePrimary = OceanDark
val CaroBlueDark = OceanDeep
val CaroBlueLight = OceanIce
val BoardWoodLight = BoardBgClean
val BoardWoodDark = OceanIce
val BoardGridLight = BoardGridSlate
val BoardGridDark = BoardGridSlate
