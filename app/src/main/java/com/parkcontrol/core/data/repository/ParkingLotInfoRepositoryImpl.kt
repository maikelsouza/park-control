package com.parkcontrol.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.parkcontrol.core.datastore.settingsPreferencesDataStore
import com.parkcontrol.core.datastore.settingsPreferencesFlow
import com.parkcontrol.core.domain.model.ParkingLotInfo
import com.parkcontrol.core.domain.repository.ParkingLotInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of ParkingLotInfoRepository using DataStore.
 * Bridges domain layer with local persistence (Preferences DataStore).
 * There is a single parking lot registration record for the app.
 */
class ParkingLotInfoRepositoryImpl(
    private val context: Context
) : ParkingLotInfoRepository {

    override fun observeParkingLotInfo(): Flow<ParkingLotInfo> {
        return context.settingsPreferencesFlow().map { prefs ->
            ParkingLotInfo(
                name = prefs[ParkingLotInfoDataStoreKeys.NAME].orEmpty(),
                phone = prefs[ParkingLotInfoDataStoreKeys.PHONE].orEmpty(),
                street = prefs[ParkingLotInfoDataStoreKeys.STREET].orEmpty(),
                number = prefs[ParkingLotInfoDataStoreKeys.NUMBER].orEmpty(),
                complement = prefs[ParkingLotInfoDataStoreKeys.COMPLEMENT].orEmpty(),
                neighborhood = prefs[ParkingLotInfoDataStoreKeys.NEIGHBORHOOD].orEmpty(),
                city = prefs[ParkingLotInfoDataStoreKeys.CITY].orEmpty(),
                state = prefs[ParkingLotInfoDataStoreKeys.STATE].orEmpty(),
                zipCode = prefs[ParkingLotInfoDataStoreKeys.ZIP_CODE].orEmpty()
            )
        }
    }

    override suspend fun saveParkingLotInfo(info: ParkingLotInfo) {
        context.settingsPreferencesDataStore.edit { prefs ->
            prefs[ParkingLotInfoDataStoreKeys.NAME] = info.name
            prefs[ParkingLotInfoDataStoreKeys.PHONE] = info.phone
            prefs[ParkingLotInfoDataStoreKeys.STREET] = info.street
            prefs[ParkingLotInfoDataStoreKeys.NUMBER] = info.number
            prefs[ParkingLotInfoDataStoreKeys.COMPLEMENT] = info.complement
            prefs[ParkingLotInfoDataStoreKeys.NEIGHBORHOOD] = info.neighborhood
            prefs[ParkingLotInfoDataStoreKeys.CITY] = info.city
            prefs[ParkingLotInfoDataStoreKeys.STATE] = info.state
            prefs[ParkingLotInfoDataStoreKeys.ZIP_CODE] = info.zipCode
        }
    }
}

/**
 * DataStore keys for the parking lot registration data.
 */
object ParkingLotInfoDataStoreKeys {
    val NAME = stringPreferencesKey("parking_lot_name")
    val PHONE = stringPreferencesKey("parking_lot_phone")
    val STREET = stringPreferencesKey("parking_lot_street")
    val NUMBER = stringPreferencesKey("parking_lot_number")
    val COMPLEMENT = stringPreferencesKey("parking_lot_complement")
    val NEIGHBORHOOD = stringPreferencesKey("parking_lot_neighborhood")
    val CITY = stringPreferencesKey("parking_lot_city")
    val STATE = stringPreferencesKey("parking_lot_state")
    val ZIP_CODE = stringPreferencesKey("parking_lot_zip_code")
}

