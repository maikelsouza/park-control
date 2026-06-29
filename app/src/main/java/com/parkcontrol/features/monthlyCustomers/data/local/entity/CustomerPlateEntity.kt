package com.parkcontrol.features.monthlyCustomers.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "customer_plates",
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
data class CustomerPlateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val customerId: Int,
    val plate: String,
    val isPrimary: Boolean = false,
    val createdAt: Long
)


