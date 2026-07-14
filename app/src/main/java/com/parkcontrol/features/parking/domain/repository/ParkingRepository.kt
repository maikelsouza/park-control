package com.parkcontrol.features.parking.domain.repository

import com.parkcontrol.features.parking.domain.model.ParkingRecord
import kotlinx.coroutines.flow.Flow

interface ParkingRepository {
    fun observeParkingRecords(): Flow<List<ParkingRecord>>

    suspend fun addParkingRecord(record: ParkingRecord)

    suspend fun updateParkingRecord(record: ParkingRecord)

    suspend fun hasActiveParking(licensePlate: String): Boolean
}

