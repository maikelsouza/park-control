package com.parkcontrol.features.agreements.domain.model

data class Agreement(
    val id: Int = 0,
    val name: String,
    val contactName: String,
    val phone: String,
    val email: String = "",
    val street: String,
    val number: String = "",
    val complement: String = "",
    val city: String,
    val neighborhood: String,
    val state: String,
    val zipCode: String,
    val discountCents: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

