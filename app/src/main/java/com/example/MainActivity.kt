package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.CaroDatabase
import com.example.data.repository.CaroRepository
import com.example.logic.GameRuleType
import com.example.ui.screens.AchievementScreen
import com.example.ui.screens.BackupRestoreScreen
import com.example.ui.screens.BoardCustomizationScreen
import com.example.ui.screens.CaroGameScreen
import com.example.ui.screens.GameModeScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReplayScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.TrainingScreen
import com.example.ui.screens.TutorialDialog
import com.example.ui.theme.CaroMasterTheme
import com.example.ui.theme.SoftOceanBlueDark
import com.example.ui.theme.SoftOceanBlueLight
import com.example.ui.theme.SoftOceanBlueMedium
import com.example.ui.theme.SoftOceanBorder
import com.example.ui.theme.TextBlackMuted
import com.example.ui.theme.TextBlackPure
import com.example.ui.theme.TextBlackSecondary
import com.example.ui.theme.TextBlackSolid
import com.example.ui.theme.WhiteBorder
import com.example.ui.theme.WhiteCard
import com.example.ui.theme.WhitePure
import com.example.ui.theme.WhiteSurface
import com.example.util.SoundEffectHelper
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.MainViewModel

enum class AppScreen {
    SPLASH,
    HOME,
    GAME_MODE,
    GAME_PLAY,
    TRAINING,
    HISTORY,
    REPLAY,
    LEADERBOARD,
    STATISTICS,
    ACHIEVEMENTS,
    PROFILE,
    SETTINGS,
    BOARD_CUSTOMIZATION,
    BACKUP_RESTORE
}

class MainActivity : ComponentActivity() {

    private lateinit var soundHelper: SoundEffectHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = CaroDatabase.getDatabase(applicationContext)
        val repository = CaroRepository(database)
        soundHelper = SoundEffectHelper(applicationContext)

        val mainViewModel = MainViewModel(repository)
        val gameViewModel = GameViewModel(repository, soundHelper)

        setContent {
            val settings by mainViewModel.settingsState.collectAsState()
            val isDarkTheme = settings?.darkMode ?: false

            CaroMasterTheme(darkTheme = isDarkTheme) {
                CaroMasterApp(
                    mainViewModel = mainViewModel,
                    gameViewModel = gameViewModel,
                    repository = repository,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundHelper.release()
    }
}

@Composable
fun CaroMasterApp(
    mainViewModel: MainViewModel,
    gameViewModel: GameViewModel,
    repository: CaroRepository,
    isDarkTheme: Boolean
) {
    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
    var selectedGameMode by remember { mutableStateOf("AI") }
    var replayGameId by remember { mutableStateOf(0L) }
    var showTutorialDialog by remember { mutableStateOf(false) }

    val player by mainViewModel.playerState.collectAsState()
    val achievements by mainViewModel.achievementsState.collectAsState()
    val gamesHistory by mainViewModel.gamesHistoryState.collectAsState()
    val settings by mainViewModel.settingsState.collectAsState()

    if (currentScreen != AppScreen.HOME && currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.GAME_PLAY && currentScreen != AppScreen.TRAINING) {
        BackHandler {
            currentScreen = AppScreen.HOME
        }
    }

    val showBottomBar = currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.HISTORY,
        AppScreen.ACHIEVEMENTS,
        AppScreen.PROFILE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = WhiteSurface,
        bottomBar = {
            if (showBottomBar) {
                CustomTextBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        if (screen == AppScreen.GAME_MODE) {
                            selectedGameMode = "AI"
                        }
                        currentScreen = screen
                    }
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.padding(innerPadding),
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                AppScreen.SPLASH -> {
                    SplashScreen(
                        onSplashFinished = {
                            currentScreen = AppScreen.HOME
                            if (settings?.tutorialCompleted != true) {
                                showTutorialDialog = true
                            }
                        }
                    )
                }

                AppScreen.HOME -> {
                    HomeScreen(
                        player = player,
                        achievements = achievements,
                        onPlayAiClicked = {
                            selectedGameMode = "AI"
                            currentScreen = AppScreen.GAME_MODE
                        },
                        onPlayLocalClicked = {
                            selectedGameMode = "LOCAL"
                            currentScreen = AppScreen.GAME_MODE
                        },
                        onPracticeClicked = {
                            currentScreen = AppScreen.TRAINING
                        },
                        onHistoryClicked = { currentScreen = AppScreen.HISTORY },
                        onLeaderboardClicked = { currentScreen = AppScreen.LEADERBOARD },
                        onAchievementsClicked = { currentScreen = AppScreen.ACHIEVEMENTS },
                        onStatisticsClicked = { currentScreen = AppScreen.STATISTICS },
                        onProfileClicked = { currentScreen = AppScreen.PROFILE },
                        onSettingsClicked = { currentScreen = AppScreen.SETTINGS }
                    )
                }

                AppScreen.GAME_MODE -> {
                    GameModeScreen(
                        mode = selectedGameMode,
                        defaultBoardSize = settings?.boardSize ?: 15,
                        defaultTimerSeconds = settings?.timerSeconds ?: 30,
                        defaultPlayerName = player?.name ?: "Kỳ Thủ Caro",
                        defaultRuleType = settings?.ruleType ?: "STANDARD",
                        onBack = { currentScreen = AppScreen.HOME },
                        onStartGame = { mode, diff, rule, bSize, piece, pXName, pOName, timerSecs ->
                            gameViewModel.startNewGame(
                                mode = mode,
                                difficulty = diff,
                                ruleType = rule,
                                boardSize = bSize,
                                humanPiece = piece,
                                playerXName = pXName,
                                playerOName = pOName,
                                timeLimitSeconds = timerSecs
                            )
                            currentScreen = AppScreen.GAME_PLAY
                        }
                    )
                }

                AppScreen.GAME_PLAY -> {
                    CaroGameScreen(
                        gameViewModel = gameViewModel,
                        player = player,
                        isDarkTheme = isDarkTheme,
                        onBackToHome = { currentScreen = AppScreen.HOME },
                        onViewHistory = { currentScreen = AppScreen.HISTORY }
                    )
                }

                AppScreen.TRAINING -> {
                    TrainingScreen(
                        gameViewModel = gameViewModel,
                        isDarkTheme = isDarkTheme,
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.HISTORY -> {
                    HistoryScreen(
                        games = gamesHistory,
                        onBack = { currentScreen = AppScreen.HOME },
                        onReplayGame = { id ->
                            replayGameId = id
                            currentScreen = AppScreen.REPLAY
                        },
                        onDeleteGame = { id -> mainViewModel.deleteGame(id) },
                        onClearAllHistory = { mainViewModel.clearAllHistory() }
                    )
                }

                AppScreen.REPLAY -> {
                    ReplayScreen(
                        gameId = replayGameId,
                        repository = repository,
                        isDarkTheme = isDarkTheme,
                        onBack = { currentScreen = AppScreen.HISTORY }
                    )
                }

                AppScreen.LEADERBOARD -> {
                    LeaderboardScreen(
                        player = player,
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.STATISTICS -> {
                    StatisticsScreen(
                        mainViewModel = mainViewModel,
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.ACHIEVEMENTS -> {
                    AchievementScreen(
                        achievements = achievements,
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.PROFILE -> {
                    ProfileScreen(
                        player = player,
                        onUpdateProfile = { name, avatar ->
                            mainViewModel.updateProfile(name, avatar)
                        },
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        settings = settings,
                        onUpdateSettings = { s -> mainViewModel.updateSettings(s) },
                        onOpenBoardCustomization = { currentScreen = AppScreen.BOARD_CUSTOMIZATION },
                        onOpenBackupRestore = { currentScreen = AppScreen.BACKUP_RESTORE },
                        onShowTutorial = { showTutorialDialog = true },
                        onClearHistory = { mainViewModel.clearAllHistory() },
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.BOARD_CUSTOMIZATION -> {
                    BoardCustomizationScreen(
                        settings = settings,
                        onSaveSettings = { s -> mainViewModel.updateSettings(s) },
                        onBack = { currentScreen = AppScreen.SETTINGS }
                    )
                }

                AppScreen.BACKUP_RESTORE -> {
                    BackupRestoreScreen(
                        mainViewModel = mainViewModel,
                        onBack = { currentScreen = AppScreen.SETTINGS }
                    )
                }
            }
        }
    }

    if (showTutorialDialog) {
        TutorialDialog(
            onDismiss = {
                showTutorialDialog = false
                mainViewModel.setTutorialCompleted()
            }
        )
    }
}

/**
 * Custom Text-Only Bottom Navigation Bar conforming strictly to requirements:
 * - Font đậm (Bold/Black)
 * - Đường gạch dưới tab đang chọn (Underline indicator)
 * - Background nhẹ khi chọn (Subtle soft ocean blue pill)
 * - Chữ màu đen, nền trắng ngà trang nhã
 * - KHÔNG có icon / emoji
 */
@Composable
fun CustomTextBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    Surface(
        color = WhitePure,
        tonalElevation = 6.dp,
        border = BorderStroke(1.5.dp, SoftOceanBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextNavTab(
                label = "TRANG CHỦ",
                isSelected = currentScreen == AppScreen.HOME,
                onClick = { onNavigate(AppScreen.HOME) }
            )
            TextNavTab(
                label = "CHƠI CỜ",
                isSelected = currentScreen == AppScreen.GAME_MODE,
                onClick = { onNavigate(AppScreen.GAME_MODE) }
            )
            TextNavTab(
                label = "LỊCH SỬ",
                isSelected = currentScreen == AppScreen.HISTORY,
                onClick = { onNavigate(AppScreen.HISTORY) }
            )
            TextNavTab(
                label = "DANH HIỆU",
                isSelected = currentScreen == AppScreen.ACHIEVEMENTS,
                onClick = { onNavigate(AppScreen.ACHIEVEMENTS) }
            )
            TextNavTab(
                label = "HỒ SƠ",
                isSelected = currentScreen == AppScreen.PROFILE,
                onClick = { onNavigate(AppScreen.PROFILE) }
            )
        }
    }
}

@Composable
fun TextNavTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) SoftOceanBlueLight else Color.Transparent
            )
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) SoftOceanBorder else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            color = if (isSelected) SoftOceanBlueDark else TextBlackSecondary
        )
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(3.dp)
                .background(
                    if (isSelected) SoftOceanBlueDark else Color.Transparent,
                    RoundedCornerShape(1.5.dp)
                )
        )
    }
}
