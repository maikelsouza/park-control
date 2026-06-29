package com.parkcontrol.core.di

import android.content.Context
import com.parkcontrol.core.data.database.AppDatabase
import com.parkcontrol.core.data.repository.ParkingConfigRepositoryImpl
import com.parkcontrol.core.domain.repository.ParkingConfigRepository
import com.parkcontrol.core.domain.usecase.GetParkingConfigUseCase
import com.parkcontrol.core.domain.usecase.SaveParkingConfigUseCase
import com.parkcontrol.features.monthlyCustomers.data.repository.MonthlyCustomerRepositoryImpl
import com.parkcontrol.features.monthlyCustomers.domain.repository.MonthlyCustomerRepository

/**
 * Factory for creating core dependencies.
 * Provides repository and use case instances for features to use.
 * Ensures single instance of repository across the app (simple singleton pattern).
 */
object CoreDependencies {

    private var parkingConfigRepository: ParkingConfigRepository? = null
    private var monthlyCustomerRepository: MonthlyCustomerRepository? = null
    private var appDatabase: AppDatabase? = null

    /**
     * Get or create the AppDatabase instance.
     */
    private fun getAppDatabase(context: Context): AppDatabase {
        if (appDatabase == null) {
            appDatabase = AppDatabase.getInstance(context)
        }
        return appDatabase!!
    }

    /**
     * Get or create the parking configuration repository instance.
     */
    fun getParkingConfigRepository(context: Context): ParkingConfigRepository {
        if (parkingConfigRepository == null) {
            parkingConfigRepository = ParkingConfigRepositoryImpl(context)
        }
        return parkingConfigRepository!!
    }

    fun getMonthlyCustomerRepository(context: Context): MonthlyCustomerRepository {
        if (monthlyCustomerRepository == null) {
            val database = getAppDatabase(context)
            val dao = database.monthlyCustomerDao()
            monthlyCustomerRepository = MonthlyCustomerRepositoryImpl(dao)
        }
        return monthlyCustomerRepository!!
    }

    /**
     * Create a new instance of GetParkingConfigUseCase.
     */
    fun createGetParkingConfigUseCase(context: Context): GetParkingConfigUseCase {
        return GetParkingConfigUseCase(getParkingConfigRepository(context))
    }

    /**
     * Create a new instance of SaveParkingConfigUseCase.
     */
    fun createSaveParkingConfigUseCase(context: Context): SaveParkingConfigUseCase {
        return SaveParkingConfigUseCase(getParkingConfigRepository(context))
    }
}

