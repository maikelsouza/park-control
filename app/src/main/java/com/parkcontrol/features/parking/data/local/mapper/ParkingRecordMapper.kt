package com.parkcontrol.features.parking.data.local.mapper

import com.parkcontrol.features.parking.data.local.entity.ParkingRecordEntity
import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.model.ParkingStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private val defaultZoneId: ZoneId = ZoneId.systemDefault()

fun ParkingRecordEntity.toDomain(): ParkingRecord {
    return ParkingRecord(
        id = id,
        customerId = customerId,
        licensePlate = licensePlate,
        phone = phone,
        entryTime = entryTimeMillis.toLocalDateTime(),
        exitTime = exitTimeMillis?.toLocalDateTime(),
        status = ParkingStatus.valueOf(status),
        amountPaid = amountPaid
    )
}

fun ParkingRecord.toEntity(): ParkingRecordEntity {
    val now = System.currentTimeMillis()
    return ParkingRecordEntity(
        id = id,
        customerId = customerId,
        licensePlate = licensePlate.trim().uppercase(),
        phone = phone.trim(),
        entryTimeMillis = entryTime.toEpochMillis(),
        exitTimeMillis = exitTime?.toEpochMillis(),
        status = status.name,
        amountPaid = amountPaid,
        createdAt = now,
        updatedAt = now
    )
}

fun List<ParkingRecordEntity>.toDomain(): List<ParkingRecord> {
    return map { it.toDomain() }
}

private fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this)
        .atZone(defaultZoneId)
        .toLocalDateTime()
}

private fun LocalDateTime.toEpochMillis(): Long {
    return atZone(defaultZoneId)
        .toInstant()
        .toEpochMilli()
}

