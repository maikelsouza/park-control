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
                val extraMinutes = minutes - 30
                val extraHours =
                    ceil(extraMinutes / 60.0)

                first30MinutesPrice +
                        (extraHours * pricePerHour)
            }
        }
    }
}