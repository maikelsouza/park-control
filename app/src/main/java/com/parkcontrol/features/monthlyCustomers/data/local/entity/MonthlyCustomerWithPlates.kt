package com.parkcontrol.features.monthlyCustomers.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MonthlyCustomerWithPlates(
    @Embedded
    val customer: MonthlyCustomerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "customerId"
    )
    val plates: List<CustomerPlateEntity>
)

