package com.parkcontrol.features.agreements.data.local.mapper

import com.parkcontrol.features.agreements.data.local.entity.AgreementEntity
import com.parkcontrol.features.agreements.domain.model.Agreement

fun AgreementEntity.toDomain(): Agreement {
    return Agreement(
        id = id,
        name = name,
        contactName = contactName,
        phone = phone,
        email = email,
        street = street,
        number = number,
        complement = complement,
        city = city,
        neighborhood = neighborhood,
        state = state,
        zipCode = zipCode,
        discountCents = discountCents,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Agreement.toEntity(): AgreementEntity {
    return AgreementEntity(
        id = id,
        name = name,
        contactName = contactName,
        phone = phone,
        email = email,
        street = street,
        number = number,
        complement = complement,
        city = city,
        neighborhood = neighborhood,
        state = state,
        zipCode = zipCode,
        discountCents = discountCents,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

