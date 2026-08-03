package com.parkcontrol.features.monthlyCustomers.data.local.mapper

import com.parkcontrol.features.monthlyCustomers.data.local.entity.CustomerVehicleEntity
import com.parkcontrol.features.monthlyCustomers.data.local.entity.MonthlyCustomerEntity
import com.parkcontrol.features.monthlyCustomers.data.local.entity.MonthlyCustomerWithPlates
import com.parkcontrol.features.monthlyCustomers.domain.model.MonthlyCustomer

fun MonthlyCustomerWithPlates.toDomain(): MonthlyCustomer {
    val orderedVehicles = vehicles
        .sortedWith(compareByDescending<CustomerVehicleEntity> { it.isPrimary }.thenBy { it.id })
        .map { it.toDomain() }

    return MonthlyCustomer(
        id = customer.id,
        name = customer.name,
        phone = customer.phone,
        email = customer.email,
        sexo = customer.sexo,
        isMonthly = customer.isMonthly,
        monthlyFeeCents = customer.monthlyFeeCents,
        dueDay = customer.dueDay,
        vehicles = orderedVehicles,
        isActive = customer.isActive,
        createdAt = customer.createdAt,
        updatedAt = customer.updatedAt
    )
}

fun MonthlyCustomer.toEntity(): MonthlyCustomerEntity {
    return MonthlyCustomerEntity(
        id = id,
        name = name,
        phone = phone,
        email = email,
        sexo = sexo,
        isMonthly = isMonthly,
        monthlyFeeCents = monthlyFeeCents,
        dueDay = dueDay,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<MonthlyCustomerWithPlates>.toDomain(): List<MonthlyCustomer> {
    return this.map { it.toDomain() }
}
