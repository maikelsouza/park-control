package com.parkcontrol.features.agreements.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agreements")
data class AgreementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val contactName: String,
    val phone: String,
    val email: String,
    val street: String,
    val number: String,
    val complement: String,
    val city: String,
    val neighborhood: String,
    val state: String,
    val zipCode: String,
    val discountCents: Int,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)

