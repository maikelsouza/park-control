package com.parkcontrol.features.monthlyCustomers.domain.usecase

import com.parkcontrol.features.monthlyCustomers.domain.model.CustomerVehicle
import com.parkcontrol.features.monthlyCustomers.domain.repository.CustomerVehicleRepository

class SaveVehicleUseCase(private val repository: CustomerVehicleRepository) {
    suspend operator fun invoke(vehicle: CustomerVehicle): Int =
        repository.addVehicle(vehicle)
}

