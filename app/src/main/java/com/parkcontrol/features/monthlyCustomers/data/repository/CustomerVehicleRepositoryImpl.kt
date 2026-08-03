package com.parkcontrol.features.monthlyCustomers.data.repository

import com.parkcontrol.features.monthlyCustomers.data.local.dao.CustomerVehicleDao
import com.parkcontrol.features.monthlyCustomers.data.local.mapper.toDomain
import com.parkcontrol.features.monthlyCustomers.data.local.mapper.toEntity
import com.parkcontrol.features.monthlyCustomers.domain.model.CustomerVehicle
import com.parkcontrol.features.monthlyCustomers.domain.repository.CustomerVehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CustomerVehicleRepositoryImpl(
    private val dao: CustomerVehicleDao
) : CustomerVehicleRepository {

    override fun observeVehiclesByCustomer(customerId: Int): Flow<List<CustomerVehicle>> =
        dao.observeVehiclesByCustomer(customerId).map { list -> list.map { it.toDomain() } }

    override suspend fun getVehicleById(vehicleId: Int): CustomerVehicle? =
        dao.getVehicleById(vehicleId)?.toDomain()

    override suspend fun addVehicle(vehicle: CustomerVehicle): Int =
        dao.insertVehicle(vehicle.toEntity()).toInt()

    override suspend fun updateVehicle(vehicle: CustomerVehicle) =
        dao.updateVehicle(vehicle.toEntity())

    override suspend fun deleteVehicle(vehicleId: Int) =
        dao.deleteVehicle(vehicleId)

    override suspend fun isPlateUsed(plate: String, excludeVehicleId: Int): Boolean =
        dao.isPlateUsed(plate, excludeVehicleId)
}

