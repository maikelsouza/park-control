package com.parkcontrol.features.monthlyCustomers.domain.model

data class MonthlyCustomer(
    val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val isMonthly: Boolean = true,
    val monthlyFeeCents: Int? = null,
    val dueDay: Int? = null,
    val vehicles: List<CustomerVehicle> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
