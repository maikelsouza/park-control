package com.parkcontrol.features.parking.domain.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class ParkingRecord(
    val id: String = UUID.randomUUID().toString(),
    val customerId: Int? = null,
    val licensePlate: String,
    val phone: String = "",
    val entryTime: LocalDateTime,
    val exitTime: LocalDateTime? = null,
    val status: ParkingStatus = ParkingStatus.ESTACIONADO,
    val amountPaid: Double? = null
)

fun LocalDateTime.formatToBrazilian(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return this.format(formatter)
}

