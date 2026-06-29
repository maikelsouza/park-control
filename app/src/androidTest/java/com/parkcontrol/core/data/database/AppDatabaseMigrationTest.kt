package com.parkcontrol.core.data.database

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @After
    fun tearDown() {
        deleteDatabase(LEGACY_DB_NAME)
        deleteDatabase(CURRENT_DB_NAME)
    }

    @Test
    fun migratesLegacyVersionOneSchemaToVersionTwo() {
        createLegacyVersionOneDatabase(LEGACY_DB_NAME)

        val database = openMigratedDatabase(LEGACY_DB_NAME)
        try {
            val sqliteDb = database.openHelper.writableDatabase

            sqliteDb.query("SELECT `name`, `phone`, `monthlyFeeCents`, `dueDay`, `isActive` FROM `monthly_customers`").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("Cliente Legado", cursor.getString(0))
                assertEquals("11999999999", cursor.getString(1))
                assertEquals(0, cursor.getInt(2))
                assertEquals(1, cursor.getInt(3))
                assertEquals(1, cursor.getInt(4))
            }

            sqliteDb.query("SELECT `customerId`, `plate`, `isPrimary` FROM `customer_plates`").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(1, cursor.getInt(0))
                assertEquals("ABC1D23", cursor.getString(1))
                assertEquals(1, cursor.getInt(2))
            }

            assertEquals(2, sqliteDb.version)
        } finally {
            database.close()
        }
    }

    @Test
    fun migratesCurrentVersionOneSchemaWithoutLosingData() {
        createCurrentVersionOneDatabase(CURRENT_DB_NAME)

        val database = openMigratedDatabase(CURRENT_DB_NAME)
        try {
            val sqliteDb = database.openHelper.writableDatabase

            sqliteDb.query("SELECT `name`, `monthlyFeeCents`, `dueDay`, `isActive` FROM `monthly_customers`").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("Cliente Atual", cursor.getString(0))
                assertEquals(15990, cursor.getInt(1))
                assertEquals(10, cursor.getInt(2))
                assertEquals(1, cursor.getInt(3))
            }

            sqliteDb.query("SELECT `customerId`, `plate`, `isPrimary` FROM `customer_plates`").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(7, cursor.getInt(0))
                assertEquals("XYZ9K88", cursor.getString(1))
                assertEquals(1, cursor.getInt(2))
            }

            assertEquals(2, sqliteDb.version)
        } finally {
            database.close()
        }
    }

    private fun openMigratedDatabase(databaseName: String): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()
            .also { it.openHelper.writableDatabase }
    }

    private fun createLegacyVersionOneDatabase(databaseName: String) {
        val database = createRawDatabase(databaseName)
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `monthly_customers` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `phone` TEXT NOT NULL,
                `licensePlate` TEXT NOT NULL,
                `model` TEXT NOT NULL,
                `brand` TEXT NOT NULL,
                `color` TEXT NOT NULL
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            INSERT INTO `monthly_customers` (`id`, `name`, `phone`, `licensePlate`, `model`, `brand`, `color`)
            VALUES (1, 'Cliente Legado', '11999999999', ' ABC1D23 ', 'Model S', 'Tesla', 'Preto')
            """.trimIndent()
        )
        database.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        database.execSQL("INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (42, '$LEGACY_V1_HASH')")
        database.version = 1
        database.close()
    }

    private fun createCurrentVersionOneDatabase(databaseName: String) {
        val database = createRawDatabase(databaseName)
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `monthly_customers` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `phone` TEXT NOT NULL,
                `monthlyFeeCents` INTEGER NOT NULL,
                `dueDay` INTEGER NOT NULL,
                `isActive` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `customer_plates` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `customerId` INTEGER NOT NULL,
                `plate` TEXT NOT NULL,
                `isPrimary` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                FOREIGN KEY(`customerId`) REFERENCES `monthly_customers`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_customer_plates_customerId` ON `customer_plates` (`customerId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_customer_plates_plate` ON `customer_plates` (`plate`)")
        database.execSQL(
            """
            INSERT INTO `monthly_customers` (`id`, `name`, `phone`, `monthlyFeeCents`, `dueDay`, `isActive`, `createdAt`, `updatedAt`)
            VALUES (7, 'Cliente Atual', '11888887777', 15990, 10, 1, 1710000000000, 1710000005000)
            """.trimIndent()
        )
        database.execSQL(
            """
            INSERT INTO `customer_plates` (`id`, `customerId`, `plate`, `isPrimary`, `createdAt`)
            VALUES (3, 7, 'XYZ9K88', 1, 1710000000000)
            """.trimIndent()
        )
        database.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        database.execSQL("INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (42, '$CURRENT_V1_HASH')")
        database.version = 1
        database.close()
    }

    private fun createRawDatabase(databaseName: String): SQLiteDatabase {
        deleteDatabase(databaseName)
        val databaseFile = context.getDatabasePath(databaseName)
        databaseFile.parentFile?.mkdirs()
        return SQLiteDatabase.openOrCreateDatabase(databaseFile, null)
    }

    private fun deleteDatabase(databaseName: String) {
        context.deleteDatabase(databaseName)
        val databaseFile = context.getDatabasePath(databaseName)
        listOf("", "-wal", "-shm", "-journal").forEach { suffix ->
            File(databaseFile.absolutePath + suffix).delete()
        }
    }

    private companion object {
        const val LEGACY_DB_NAME = "migration_legacy_test.db"
        const val CURRENT_DB_NAME = "migration_current_test.db"
        const val LEGACY_V1_HASH = "abd1700beea01ab65db242f7d1d9549a"
        const val CURRENT_V1_HASH = "f3f5151b10945dc1a080bcdea7ca98d4"
    }
}

