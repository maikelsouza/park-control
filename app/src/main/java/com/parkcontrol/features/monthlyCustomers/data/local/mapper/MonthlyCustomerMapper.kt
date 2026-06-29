package com.parkcontrol.features.monthlyCustomers.data.local.mapper

import com.parkcontrol.features.monthlyCustomers.data.local.entity.CustomerPlateEntity
import com.parkcontrol.features.monthlyCustomers.data.local.entity.MonthlyCustomerEntity
import com.parkcontrol.features.monthlyCustomers.data.local.entity.MonthlyCustomerWithPlates
import com.parkcontrol.features.monthlyCustomers.domain.model.MonthlyCustomer

fun MonthlyCustomerWithPlates.toDomain(): MonthlyCustomer {
    val orderedPlates = plates
        .sortedWith(compareByDescending<CustomerPlateEntity> { it.isPrimary }.thenBy { it.id })
        .map { it.plate }

    return MonthlyCustomer(
        id = customer.id,
        name = customer.name,
        phone = customer.phone,
        monthlyFeeCents = customer.monthlyFeeCents,
        dueDay = customer.dueDay,
        plates = orderedPlates,
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
        monthlyFeeCents = monthlyFeeCents,
        dueDay = dueDay,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun MonthlyCustomer.toPlateEntities(customerId: Int): List<CustomerPlateEntity> {
    val now = System.currentTimeMillis()
    return plates.mapIndexed { index, plate ->
        CustomerPlateEntity(
            customerId = customerId,
            plate = plate,
            isPrimary = index == 0,
            createdAt = now
        )
    }
}

fun List<MonthlyCustomerWithPlates>.toDomain(): List<MonthlyCustomer> {
    return this.map { it.toDomain() }
}

