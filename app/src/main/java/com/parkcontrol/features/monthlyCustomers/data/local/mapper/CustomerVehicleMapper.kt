package com.parkcontrol.features.monthlyCustomers.data.local.mapper

import com.parkcontrol.features.monthlyCustomers.data.local.entity.CustomerVehicleEntity
import com.parkcontrol.features.monthlyCustomers.domain.model.CustomerVehicle
import com.parkcontrol.features.monthlyCustomers.domain.model.PlateType
import com.parkcontrol.features.monthlyCustomers.domain.model.VehicleCategory

fun CustomerVehicleEntity.toDomain(): CustomerVehicle = CustomerVehicle(
    id = id,
    customerId = customerId,
    brand = brand,
    model = model,
    color = color,
    plate = plate,
    plateType = runCatching { PlateType.valueOf(plateType) }.getOrDefault(PlateType.OUTRA),
    category = runCatching { VehicleCategory.valueOf(category) }.getOrDefault(VehicleCategory.OUTRO),
    parkingSpot = parkingSpot,
    isPrimary = isPrimary,
    createdAt = createdAt
)

fun CustomerVehicle.toEntity(): CustomerVehicleEntity = CustomerVehicleEntity(
    id = id,
    customerId = customerId,
    brand = brand,
    model = model,
    color = color,
    plate = plate.trim().uppercase(),
    plateType = plateType.name,
    category = category.name,
    parkingSpot = parkingSpot?.trim()?.takeIf { it.isNotBlank() },
    isPrimary = isPrimary,
    createdAt = createdAt
)

