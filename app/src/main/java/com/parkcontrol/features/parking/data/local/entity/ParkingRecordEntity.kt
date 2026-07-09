package com.parkcontrol.features.parking.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.parkcontrol.features.monthlyCustomers.data.local.entity.MonthlyCustomerEntity

@Entity(
    tableName = "parking_records",
    foreignKeys = [
        ForeignKey(
            entity = MonthlyCustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["customerId"]),
        Index(value = ["licensePlate"]),
        Index(value = ["status"]),
        Index(value = ["entryTimeMillis"])
    ]
)
data class ParkingRecordEntity(
    @PrimaryKey
    val id: String,
    val customerId: Int? = null,
    val licensePlate: String,
    val phone: String,
    val entryTimeMillis: Long,
    val exitTimeMillis: Long? = null,
    val status: String,
    val amountPaid: Double? = null,
    val createdAt: Long,
    val updatedAt: Long
)

