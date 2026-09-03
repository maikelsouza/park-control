package com.parkcontrol.features.monthlyCustomers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "customer_vehicles",
    foreignKeys = [
        ForeignKey(
            entity = MonthlyCustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["customerId"]),
        Index(value = ["plate"])
    ]
)
data class CustomerVehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val customerId: Int,
    val brand: String,
    val model: String,
    val color: String,
    val plate: String,
    val plateType: String,       // PlateType.name
    val category: String,        // VehicleCategory.name
    val parkingSpot: String?,
    val isPrimary: Boolean = false,
    val createdAt: Long
)

