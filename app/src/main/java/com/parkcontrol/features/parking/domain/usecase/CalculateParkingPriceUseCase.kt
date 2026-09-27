package com.parkcontrol.features.parking.domain.usecase

import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.ceil

class CalculateParkingPriceUseCase {

    operator fun invoke(
        entry: LocalDateTime,
        exit: LocalDateTime,
        first30MinutesPrice: Double,
        pricePerHour: Double
    ): Double {

        val minutes = Duration
            .between(entry, exit)
            .toMinutes()

        return when {

            minutes <= 30 -> first30MinutesPrice

            else -> {
                val totalHours = ceil(minutes / 60.0)

                totalHours * pricePerHour
            }
        }
    }
}