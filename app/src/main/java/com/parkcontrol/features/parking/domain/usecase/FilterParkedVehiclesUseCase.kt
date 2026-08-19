package com.parkcontrol.features.parking.domain.usecase

import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.model.ParkingStatus
import java.time.LocalDate

class FilterParkedVehiclesUseCase {
    operator fun invoke(
        records: List<ParkingRecord>,
        plateQuery: String,
        startDate: LocalDate?,
        endDate: LocalDate?,
        statusFilter: ParkingStatus? = null
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
            .filter { record ->
                statusFilter?.let { record.status == it } ?: true
            }
            .sortedByDescending { it.entryTime }
            .toList()
    }
}


