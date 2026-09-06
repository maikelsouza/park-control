package com.parkcontrol.core.domain.repository

import com.parkcontrol.core.domain.model.ParkingLotInfo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for the parking lot registration data.
 * Represents a single record (name, phone, address) that can be
 * created/edited, but never deleted or deactivated.
 */
interface ParkingLotInfoRepository {

    /**
     * Observable stream of the current parking lot registration data.
     */
    fun observeParkingLotInfo(): Flow<ParkingLotInfo>

    /**
     * Save (create or update) the parking lot registration data.
     */
    suspend fun saveParkingLotInfo(info: ParkingLotInfo)
}

