package com.parkcontrol.features.monthlyCustomers.domain.usecase

import com.parkcontrol.features.monthlyCustomers.domain.model.CustomerVehicle
import com.parkcontrol.features.monthlyCustomers.domain.repository.CustomerVehicleRepository

class GetVehicleByIdUseCase(private val repository: CustomerVehicleRepository) {
    suspend operator fun invoke(vehicleId: Int): CustomerVehicle? =
        repository.getVehicleById(vehicleId)
}

