package com.parkcontrol.core.domain.repository

import com.parkcontrol.core.domain.model.ParkingConfig
import kotlinx.coroutines.flow.Flow

/**
 * Shared repository interface for parking configuration.
 * Provides contracts for reading and writing parking settings.
 * Implemented by features that persist settings (e.g., Settings feature).
 */
interface ParkingConfigRepository {

    /**
     * Observable stream of current parking configuration.
     * Emits updates whenever configuration changes.
     */
    fun observeParkingConfig(): Flow<ParkingConfig>

    /**
     * Save the complete parking configuration.
     */
    suspend fun saveParkingConfig(config: ParkingConfig)

    /**
     * Save only the first 30 minutes price.
     */
    suspend fun saveFirst30MinutesPrice(price: Double)

    /**
     * Save only the hourly rate.
     */
    suspend fun saveHourlyRate(rate: Double)

    /**
     * Save only the tolerance minutes.
     */
    suspend fun saveToleranceMinutes(minutes: Int)
}

