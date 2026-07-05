package com.parkcontrol.features.monthlyCustomers.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_customers")
data class MonthlyCustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phone: String,
    val isMonthly: Boolean = true,
    val monthlyFeeCents: Int? = null,
    val dueDay: Int? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)

