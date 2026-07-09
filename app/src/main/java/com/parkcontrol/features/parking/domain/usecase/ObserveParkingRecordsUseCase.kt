package com.parkcontrol.features.parking.domain.usecase

import com.parkcontrol.features.parking.domain.repository.ParkingRepository

class ObserveParkingRecordsUseCase(
    private val repository: ParkingRepository
) {
    operator fun invoke() = repository.observeParkingRecords()
}

