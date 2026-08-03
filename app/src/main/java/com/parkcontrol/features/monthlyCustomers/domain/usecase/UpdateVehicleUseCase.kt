package com.parkcontrol.features.monthlyCustomers.domain.usecase

import com.parkcontrol.features.monthlyCustomers.domain.model.CustomerVehicle
import com.parkcontrol.features.monthlyCustomers.domain.repository.CustomerVehicleRepository

class UpdateVehicleUseCase(private val repository: CustomerVehicleRepository) {
    suspend operator fun invoke(vehicle: CustomerVehicle) =
        repository.updateVehicle(vehicle)
}

