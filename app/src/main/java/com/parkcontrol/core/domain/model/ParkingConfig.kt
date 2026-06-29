package com.parkcontrol.core.domain.model

/**
 * Configuration model for parking rates shared across features.
 * Used by both Settings feature (for configuration) and Parking feature (for calculation).
 */
data class ParkingConfig(
    val first30MinutesPrice: Double = 5.00,
    val pricePerHour: Double = 7.00,
    val toleranceMinutes: Int = 15
)

