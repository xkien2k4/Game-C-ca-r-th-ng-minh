package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.CaroDatabase
import com.example.data.repository.CaroRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: CaroDatabase
    private lateinit var repository: CaroRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CaroDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CaroRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAppNameStringResource() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Caro Master", appName)
    }

    @Test
    fun testBackupAndRestoreJson() = runBlocking {
        val player = repository.getPlayer()
        assertNotNull(player)

        // Record a match
        repository.recordGameFinished(
            playerX = "Kỳ Thủ",
            playerO = "Máy AI",
            mode = "AI",
            boardSize = 15,
            winner = "X",
            result = "WIN",
            totalMoves = 11,
            duration = 30,
            difficulty = "MEDIUM",
            ruleType = "STANDARD",
            moveList = listOf(Pair(7, 7), Pair(8, 8)),
            humanPlayerSymbol = "X"
        )

        val backupJson = repository.exportBackupJson()
        assertTrue(backupJson.contains("CaroMaster"))
        assertTrue(backupJson.contains("Kỳ Thủ"))

        // Restore backup
        val restoreResult = repository.importRestoreJson(backupJson)
        assertTrue(restoreResult)

        // Check stats after restore
        val stats = repository.calculateDetailedStats()
        assertEquals(1, stats.totalGames)
        assertEquals(1, stats.wins)
    }
}
