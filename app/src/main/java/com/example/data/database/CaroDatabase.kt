package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AchievementDao
import com.example.data.dao.GameDao
import com.example.data.dao.MoveDao
import com.example.data.dao.PlayerDao
import com.example.data.dao.SettingsDao
import com.example.data.entity.AchievementEntity
import com.example.data.entity.GameEntity
import com.example.data.entity.MoveEntity
import com.example.data.entity.PlayerEntity
import com.example.data.entity.SettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        PlayerEntity::class,
        GameEntity::class,
        MoveEntity::class,
        AchievementEntity::class,
        SettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CaroDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun gameDao(): GameDao
    abstract fun moveDao(): MoveDao
    abstract fun achievementDao(): AchievementDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        const val DATABASE_NAME = "caro_master.db"

        @Volatile
        private var INSTANCE: CaroDatabase? = null

        fun getDatabase(context: Context): CaroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CaroDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(false)
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: CaroDatabase) {
            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            // Seed initial Player
            database.playerDao().insertPlayer(
                PlayerEntity(
                    id = 1L,
                    name = "Kỳ Thủ Caro",
                    avatar = "MASTER",
                    rating = 1000,
                    maxRating = 1000,
                    wins = 0,
                    losses = 0,
                    draws = 0,
                    totalGames = 0,
                    level = 1,
                    experience = 0,
                    winStreak = 0,
                    maxWinStreak = 0,
                    createdAt = dateStr
                )
            )

            // Seed initial Settings
            database.settingsDao().insertOrUpdate(
                SettingsEntity(
                    id = 1L,
                    soundEnabled = true,
                    vibrationEnabled = true,
                    darkMode = true,
                    boardSize = 15,
                    timerSeconds = 30,
                    language = "vi",
                    undoEnabled = true,
                    maxHints = 3,
                    ruleType = "STANDARD",
                    pieceStyle = "MODERN",
                    boardEffects = true,
                    tutorialCompleted = false
                )
            )

            // Seed standard Achievements
            val initialAchievements = listOf(
                AchievementEntity(
                    achievementKey = "FIRST_WIN",
                    title = "Chiến Thắng Đầu Tiên",
                    description = "Giành chiến thắng trận cờ Caro đầu tiên trong sự nghiệp",
                    icon = "CHIEN_THANG"
                ),
                AchievementEntity(
                    achievementKey = "WIN_5_GAMES",
                    title = "Kỳ Thủ Tinh Anh",
                    description = "Thắng 5 trận cờ bất kỳ",
                    icon = "TINH_ANH"
                ),
                AchievementEntity(
                    achievementKey = "WIN_10_GAMES",
                    title = "Cao Thủ Bất Bại",
                    description = "Thắng 10 trận cờ bất kỳ",
                    icon = "CAO_THU"
                ),
                AchievementEntity(
                    achievementKey = "WIN_STREAK_5",
                    title = "Chuỗi Thắng 5 Trận",
                    description = "Đạt chuỗi 5 chiến thắng liên tiếp không để thua",
                    icon = "CHUOI_5"
                ),
                AchievementEntity(
                    achievementKey = "WIN_STREAK_10",
                    title = "Bất Khả Chiến Bại",
                    description = "Đạt chuỗi 10 chiến thắng liên tiếp",
                    icon = "CHUOI_10"
                ),
                AchievementEntity(
                    achievementKey = "REACH_1500_RATING",
                    title = "Đẳng Cấp 1500 Elo",
                    description = "Chạm mốc 1500 điểm xếp hạng Elo",
                    icon = "ELO_1500"
                ),
                AchievementEntity(
                    achievementKey = "REACH_2000_RATING",
                    title = "Huyền Thoại 2000 Elo",
                    description = "Chạm mốc 2000 điểm xếp hạng Elo",
                    icon = "ELO_2000"
                ),
                AchievementEntity(
                    achievementKey = "FAST_WIN",
                    title = "Đòn Quyết Định Nhanh",
                    description = "Chiến thắng đối thủ trong dưới 15 nước đi",
                    icon = "TIA_CHOP"
                ),
                AchievementEntity(
                    achievementKey = "MASTER_DEFEATED",
                    title = "Vượt Mặt Đại Sư",
                    description = "Chiến thắng AI ở độ khó Cao Thủ (Master)",
                    icon = "DAI_SU"
                ),
                AchievementEntity(
                    achievementKey = "PRACTICE_MASTER",
                    title = "Chuyên Gia Luyện Tập",
                    description = "Hoàn thành xuất sắc tất cả 10 bài luyện tập thế cờ",
                    icon = "LUYEN_TAP"
                )
            )
            database.achievementDao().insertAll(initialAchievements)
        }
    }
}
