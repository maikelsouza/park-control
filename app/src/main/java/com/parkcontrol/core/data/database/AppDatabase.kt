package com.parkcontrol.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.parkcontrol.features.monthlyCustomers.data.local.dao.MonthlyCustomerDao
import com.parkcontrol.features.monthlyCustomers.data.local.entity.CustomerPlateEntity
import com.parkcontrol.features.monthlyCustomers.data.local.entity.MonthlyCustomerEntity
import com.parkcontrol.features.parking.data.local.dao.ParkingRecordDao
import com.parkcontrol.features.parking.data.local.entity.ParkingRecordEntity

@Database(
    entities = [MonthlyCustomerEntity::class, CustomerPlateEntity::class, ParkingRecordEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun monthlyCustomerDao(): MonthlyCustomerDao

    abstract fun parkingRecordDao(): ParkingRecordDao

    companion object {
        private val CREATE_PHONE_UNIQUE_INDEX_CALLBACK = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                ensurePhoneUniqueIndex(db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                ensurePhoneUniqueIndex(db)
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
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(CREATE_PHONE_UNIQUE_INDEX_CALLBACK)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

private fun ensurePhoneUniqueIndex(db: SupportSQLiteDatabase) {
    db.execSQL(
        "CREATE UNIQUE INDEX IF NOT EXISTS `index_monthly_customers_phone` ON `monthly_customers` (`phone`) WHERE TRIM(`phone`) != ''"
    )
}

