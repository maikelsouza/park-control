package com.parkcontrol.core.domain.model

/**
 * Domain model representing the parking lot registration data.
 * This is a singleton entity (a single parking lot per app installation):
 * it can be created/edited, but never deleted or deactivated.
 */
data class ParkingLotInfo(
    val name: String = "",
    val phone: String = "",
    val street: String = "",
    val number: String = "",
    val complement: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = ""
)

