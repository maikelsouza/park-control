package com.parkcontrol.features.monthlyCustomers.domain.usecase

import com.parkcontrol.features.monthlyCustomers.domain.repository.CustomerVehicleRepository

class DeleteVehicleUseCase(private val repository: CustomerVehicleRepository) {
    suspend operator fun invoke(vehicleId: Int) =
        repository.deleteVehicle(vehicleId)
}

