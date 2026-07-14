package com.parkcontrol.features.parking.domain.usecase

import com.parkcontrol.features.parking.domain.repository.ParkingRepository

class CheckVehicleActiveParkingUseCase(
    private val repository: ParkingRepository
) {
    suspend operator fun invoke(licensePlate: String): Boolean {
        return repository.hasActiveParking(licensePlate)
    }
}

