package com.parkcontrol.features.parking.domain.usecase

import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.repository.ParkingRepository

class SaveParkingRecordUseCase(
    private val repository: ParkingRepository
) {
    suspend operator fun invoke(record: ParkingRecord) {
        repository.addParkingRecord(record)
    }
}

