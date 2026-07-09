package com.parkcontrol.features.parking.domain.usecase

import com.parkcontrol.features.parking.domain.model.ParkingRecord
import java.time.LocalDate

class FilterParkedVehiclesUseCase {
    operator fun invoke(
        records: List<ParkingRecord>,
        plateQuery: String,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): List<ParkingRecord> {
        val normalizedPlateQuery = plateQuery.trim().uppercase()

        return records
            .asSequence()
            .filter { record ->
                normalizedPlateQuery.isBlank() ||
                    record.licensePlate.uppercase().contains(normalizedPlateQuery)
            }
            .filter { record ->
                val entryDate = record.entryTime.toLocalDate()
                val matchesStart = startDate?.let { entryDate >= it } ?: true
                val matchesEnd = endDate?.let { entryDate <= it } ?: true
                matchesStart && matchesEnd
            }
            .sortedByDescending { it.entryTime }
            .toList()
    }
}


