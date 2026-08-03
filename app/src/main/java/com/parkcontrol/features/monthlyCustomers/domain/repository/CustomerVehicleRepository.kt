package com.parkcontrol.features.monthlyCustomers.domain.repository

import com.parkcontrol.features.monthlyCustomers.domain.model.CustomerVehicle
import kotlinx.coroutines.flow.Flow

interface CustomerVehicleRepository {
    fun observeVehiclesByCustomer(customerId: Int): Flow<List<CustomerVehicle>>
    suspend fun getVehicleById(vehicleId: Int): CustomerVehicle?
    suspend fun addVehicle(vehicle: CustomerVehicle): Int
    suspend fun updateVehicle(vehicle: CustomerVehicle)
    suspend fun deleteVehicle(vehicleId: Int)
    suspend fun isPlateUsed(plate: String, excludeVehicleId: Int = 0): Boolean
}

