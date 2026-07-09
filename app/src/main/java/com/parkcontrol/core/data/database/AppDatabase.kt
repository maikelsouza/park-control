package com.parkcontrol.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.parkcontrol.features.monthlyCustomers.data.local.dao.MonthlyCustomerDao
import com.parkcontrol.features.monthlyCustomers.data.local.entity.CustomerPlateEntity
import com.parkcontrol.features.monthlyCustomers.data.local.entity.MonthlyCustomerEntity
import com.parkcontrol.features.parking.data.local.dao.ParkingRecordDao
import com.parkcontrol.features.parking.data.local.entity.ParkingRecordEntity

@Database(
    entities = [MonthlyCustomerEntity::class, CustomerPlateEntity::class, ParkingRecordEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun monthlyCustomerDao(): MonthlyCustomerDao

    abstract fun parkingRecordDao(): ParkingRecordDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val monthlyCustomerColumns = db.getColumnNames("monthly_customers")
                val hasCurrentMonthlyCustomerSchema = CURRENT_MONTHLY_CUSTOMER_COLUMNS.all(monthlyCustomerColumns::contains)
                val hasLegacyMonthlyCustomerSchema = LEGACY_MONTHLY_CUSTOMER_COLUMNS.all(monthlyCustomerColumns::contains)
                val hasCustomerPlatesTable = db.hasTable("customer_plates")

                if (hasCurrentMonthlyCustomerSchema && hasCustomerPlatesTable) {
                    db.execSQL("CREATE INDEX IF NOT EXISTS `index_customer_plates_customerId` ON `customer_plates` (`customerId`)")
                    db.execSQL("CREATE INDEX IF NOT EXISTS `index_customer_plates_plate` ON `customer_plates` (`plate`)")
                    return
                }

                val now = System.currentTimeMillis()

                if (hasLegacyMonthlyCustomerSchema) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `legacy_customer_plates` (
                            `customerId` INTEGER NOT NULL,
                            `plate` TEXT NOT NULL,
                            `isPrimary` INTEGER NOT NULL,
                            `createdAt` INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )
                    db.execSQL(
                        """
                        INSERT INTO `legacy_customer_plates` (`customerId`, `plate`, `isPrimary`, `createdAt`)
                        SELECT `id`, TRIM(`licensePlate`), 1, $now
                        FROM `monthly_customers`
                        WHERE TRIM(`licensePlate`) != ''
                        """.trimIndent()
                    )
                }

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `monthly_customers_new` (
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

                when {
                    hasCurrentMonthlyCustomerSchema -> {
                        db.execSQL(
                            """
                            INSERT INTO `monthly_customers_new` (`id`, `name`, `phone`, `monthlyFeeCents`, `dueDay`, `isActive`, `createdAt`, `updatedAt`)
                            SELECT `id`, `name`, `phone`, `monthlyFeeCents`, `dueDay`, `isActive`, `createdAt`, `updatedAt`
                            FROM `monthly_customers`
                            """.trimIndent()
                        )
                    }

                    hasLegacyMonthlyCustomerSchema -> {
                        db.execSQL(
                            """
                            INSERT INTO `monthly_customers_new` (`id`, `name`, `phone`, `monthlyFeeCents`, `dueDay`, `isActive`, `createdAt`, `updatedAt`)
                            SELECT `id`, `name`, `phone`, 0, 1, 1, $now, $now
                            FROM `monthly_customers`
                            """.trimIndent()
                        )
                    }
                }

                db.execSQL("DROP TABLE IF EXISTS `customer_plates`")
                db.execSQL("DROP TABLE IF EXISTS `monthly_customers`")
                db.execSQL("ALTER TABLE `monthly_customers_new` RENAME TO `monthly_customers`")
                db.execSQL(
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
                if (hasLegacyMonthlyCustomerSchema) {
                    db.execSQL(
                        """
                        INSERT INTO `customer_plates` (`customerId`, `plate`, `isPrimary`, `createdAt`)
                        SELECT `customerId`, `plate`, `isPrimary`, `createdAt`
                        FROM `legacy_customer_plates`
                        """.trimIndent()
                    )
                    db.execSQL("DROP TABLE IF EXISTS `legacy_customer_plates`")
                }
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_customer_plates_customerId` ON `customer_plates` (`customerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_customer_plates_plate` ON `customer_plates` (`plate`)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `monthly_customers` ADD COLUMN `isMonthly` INTEGER NOT NULL DEFAULT 1")
                db.execSQL(
                    """
                    UPDATE `monthly_customers`
                    SET `isMonthly` = CASE WHEN `monthlyFeeCents` > 0 THEN 1 ELSE 0 END
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `monthly_customers_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `phone` TEXT NOT NULL,
                        `isMonthly` INTEGER NOT NULL,
                        `monthlyFeeCents` INTEGER,
                        `dueDay` INTEGER,
                        `isActive` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO `monthly_customers_new` (`id`, `name`, `phone`, `isMonthly`, `monthlyFeeCents`, `dueDay`, `isActive`, `createdAt`, `updatedAt`)
                    SELECT `id`, `name`, `phone`, `isMonthly`, `monthlyFeeCents`, `dueDay`, `isActive`, `createdAt`, `updatedAt`
                    FROM `monthly_customers`
                    """.trimIndent()
                )

                db.execSQL("DROP TABLE `monthly_customers`")
                db.execSQL("ALTER TABLE `monthly_customers_new` RENAME TO `monthly_customers`")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `parking_records` (
                        `id` TEXT NOT NULL,
                        `customerId` INTEGER,
                        `licensePlate` TEXT NOT NULL,
                        `phone` TEXT NOT NULL,
                        `entryTimeMillis` INTEGER NOT NULL,
                        `exitTimeMillis` INTEGER,
                        `status` TEXT NOT NULL,
                        `amountPaid` REAL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`customerId`) REFERENCES `monthly_customers`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_parking_records_customerId` ON `parking_records` (`customerId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_parking_records_licensePlate` ON `parking_records` (`licensePlate`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_parking_records_status` ON `parking_records` (`status`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_parking_records_entryTimeMillis` ON `parking_records` (`entryTimeMillis`)")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "parkcontrol_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

private val CURRENT_MONTHLY_CUSTOMER_COLUMNS = setOf(
    "id",
    "name",
    "phone",
    "monthlyFeeCents",
    "dueDay",
    "isActive",
    "createdAt",
    "updatedAt"
)

private val LEGACY_MONTHLY_CUSTOMER_COLUMNS = setOf(
    "id",
    "name",
    "phone",
    "licensePlate",
    "model",
    "brand",
    "color"
)

private fun SupportSQLiteDatabase.hasTable(tableName: String): Boolean {
    return query("SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = '$tableName' LIMIT 1").use { cursor ->
        cursor.moveToFirst()
    }
}

private fun SupportSQLiteDatabase.getColumnNames(tableName: String): Set<String> {
    return query("PRAGMA table_info(`$tableName`)").use { cursor ->
        val nameIndex = cursor.getColumnIndex("name")
        buildSet {
            while (cursor.moveToNext()) {
                add(cursor.getString(nameIndex))
            }
        }
    }
}

