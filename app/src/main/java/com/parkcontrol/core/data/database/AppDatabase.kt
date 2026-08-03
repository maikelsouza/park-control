package com.parkcontrol.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.parkcontrol.features.monthlyCustomers.data.local.dao.CustomerVehicleDao
import com.parkcontrol.features.monthlyCustomers.data.local.dao.MonthlyCustomerDao
import com.parkcontrol.features.monthlyCustomers.data.local.entity.CustomerVehicleEntity
import com.parkcontrol.features.monthlyCustomers.data.local.entity.MonthlyCustomerEntity
import com.parkcontrol.features.parking.data.local.dao.ParkingRecordDao
import com.parkcontrol.features.parking.data.local.entity.ParkingRecordEntity

@Database(
    entities = [MonthlyCustomerEntity::class, CustomerVehicleEntity::class, ParkingRecordEntity::class],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun monthlyCustomerDao(): MonthlyCustomerDao
    abstract fun customerVehicleDao(): CustomerVehicleDao
    abstract fun parkingRecordDao(): ParkingRecordDao

    companion object {
        private val CREATE_UNIQUE_INDEXES_CALLBACK = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                ensureMonthlyCustomerUniqueIndexes(db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                ensureMonthlyCustomerUniqueIndexes(db)
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
                    .addCallback(CREATE_UNIQUE_INDEXES_CALLBACK)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

private fun ensureMonthlyCustomerUniqueIndexes(db: SupportSQLiteDatabase) {
    db.execSQL(
        "CREATE UNIQUE INDEX IF NOT EXISTS `index_monthly_customers_phone` ON `monthly_customers` (`phone`) WHERE TRIM(`phone`) != ''"
    )
    db.execSQL(
        "CREATE UNIQUE INDEX IF NOT EXISTS `index_monthly_customers_email` ON `monthly_customers` (`email`) WHERE TRIM(`email`) != ''"
    )
}
