package com.parkcontrol.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.parkcontrol.core.datastore.settingsPreferencesDataStore
import com.parkcontrol.core.datastore.settingsPreferencesFlow
import com.parkcontrol.core.domain.model.ParkingConfig
import com.parkcontrol.core.domain.repository.ParkingConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of ParkingConfigRepository using DataStore.
 * Bridges domain layer with local persistence (Preferences DataStore).
 * Shared across multiple features (Settings, Parking, etc).
 */
class ParkingConfigRepositoryImpl(
    private val context: Context
) : ParkingConfigRepository {

    override fun observeParkingConfig(): Flow<ParkingConfig> {
        return context.settingsPreferencesFlow().map { prefs ->
            val first30 = prefs[ParkingConfigDataStoreKeys.FIRST30_PRICE]
                ?: ParkingConfigDataStoreKeys.DEFAULT_FIRST30_PRICE
            val hourly = prefs[ParkingConfigDataStoreKeys.HOURLY_RATE]
                ?: ParkingConfigDataStoreKeys.DEFAULT_HOURLY_RATE
            val tolerance = prefs[ParkingConfigDataStoreKeys.TOLERANCE_MINUTES]
                ?: ParkingConfigDataStoreKeys.DEFAULT_TOLERANCE_MINUTES

            ParkingConfig(
                first30MinutesPrice = first30,
                pricePerHour = hourly,
                toleranceMinutes = tolerance
            )
        }
    }

    override suspend fun saveParkingConfig(config: ParkingConfig) {
        context.settingsPreferencesDataStore.edit { prefs ->
            prefs[ParkingConfigDataStoreKeys.FIRST30_PRICE] = config.first30MinutesPrice
            prefs[ParkingConfigDataStoreKeys.HOURLY_RATE] = config.pricePerHour
            prefs[ParkingConfigDataStoreKeys.TOLERANCE_MINUTES] = config.toleranceMinutes
        }
    }

    override suspend fun saveFirst30MinutesPrice(price: Double) {
        context.settingsPreferencesDataStore.edit { prefs ->
            prefs[ParkingConfigDataStoreKeys.FIRST30_PRICE] = price
        }
    }

    override suspend fun saveHourlyRate(rate: Double) {
        context.settingsPreferencesDataStore.edit { prefs ->
            prefs[ParkingConfigDataStoreKeys.HOURLY_RATE] = rate
        }
    }

    override suspend fun saveToleranceMinutes(minutes: Int) {
        context.settingsPreferencesDataStore.edit { prefs ->
            prefs[ParkingConfigDataStoreKeys.TOLERANCE_MINUTES] = minutes
        }
    }
}

/**
 * DataStore keys and default values for parking configuration.
 * Centralized constants to avoid duplication across features.
 */
object ParkingConfigDataStoreKeys {
    const val DEFAULT_FIRST30_PRICE = 5.00
    const val DEFAULT_HOURLY_RATE = 7.00
    const val DEFAULT_TOLERANCE_MINUTES = 15

    val FIRST30_PRICE = doublePreferencesKey("first30_price")
    val HOURLY_RATE = doublePreferencesKey("hourly_rate")
    val TOLERANCE_MINUTES = intPreferencesKey("tolerance_minutes")
}


