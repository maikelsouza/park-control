package com.parkcontrol.features.monthlyCustomers.domain.usecase

import com.parkcontrol.features.monthlyCustomers.domain.model.CustomerVehicle
import com.parkcontrol.features.monthlyCustomers.domain.repository.CustomerVehicleRepository
import kotlinx.coroutines.flow.Flow

class GetVehiclesByCustomerUseCase(private val repository: CustomerVehicleRepository) {
    operator fun invoke(customerId: Int): Flow<List<CustomerVehicle>> =
        repository.observeVehiclesByCustomer(customerId)
}

