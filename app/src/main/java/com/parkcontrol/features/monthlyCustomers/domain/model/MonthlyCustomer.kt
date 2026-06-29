package com.parkcontrol.features.monthlyCustomers.domain.model

data class MonthlyCustomer(
    val id: Int = 0,
    val name: String,
    val phone: String,
    val monthlyFeeCents: Int,
    val dueDay: Int,
    val plates: List<String>,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

