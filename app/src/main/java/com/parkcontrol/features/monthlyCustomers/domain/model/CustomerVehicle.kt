package com.parkcontrol.features.monthlyCustomers.domain.model

data class CustomerVehicle(
    val id: Int = 0,
    val customerId: Int = 0,
    val brand: String = "",
    val model: String = "",
    val color: String = "",
    val plate: String = "",
    val plateType: PlateType = PlateType.OUTRA,
    val category: VehicleCategory = VehicleCategory.OUTRO,
    val parkingSpot: String? = null,
    val isPrimary: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

