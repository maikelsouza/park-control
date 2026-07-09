package com.parkcontrol.features.parking.domain.usecase

import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.model.ParkingStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class FilterParkedVehiclesUseCaseTest {

    private val useCase = FilterParkedVehiclesUseCase()

    @Test
    fun `returns all records when no filters are provided`() {
        val now = LocalDateTime.of(2026, 7, 9, 10, 0)
        val records = listOf(
            record("A", "ABC1D23", now.minusHours(1), ParkingStatus.ESTACIONADO),
            record("B", "XYZ9Z99", now.minusHours(2), ParkingStatus.FINALIZADO)
        )

        val result = useCase(
            records = records,
            plateQuery = "",
            startDate = null,
            endDate = null
        )

        assertEquals(2, result.size)
        assertEquals(listOf("A", "B"), result.map { it.id })
    }

    @Test
    fun `filters by license plate and date range inclusively`() {
        val records = listOf(
            record(
                id = "A",
                plate = "ABC1D23",
                entry = LocalDateTime.of(2026, 7, 2, 8, 0),
                status = ParkingStatus.ESTACIONADO
            ),
            record(
                id = "B",
                plate = "ABC9X99",
                entry = LocalDateTime.of(2026, 7, 5, 8, 0),
                status = ParkingStatus.ESTACIONADO
            ),
            record(
                id = "C",
                plate = "ZZZ0Z00",
                entry = LocalDateTime.of(2026, 7, 5, 8, 0),
                status = ParkingStatus.ESTACIONADO
            )
        )

        val result = useCase(
            records = records,
            plateQuery = "ABC",
            startDate = LocalDate.of(2026, 7, 2),
            endDate = LocalDate.of(2026, 7, 5)
        )

        assertEquals(listOf("B", "A"), result.map { it.id })
    }

    @Test
    fun `returns empty list when no record matches filters`() {
        val records = listOf(
            record("A", "ABC1D23", LocalDateTime.of(2026, 7, 2, 8, 0), ParkingStatus.ESTACIONADO)
        )

        val result = useCase(
            records = records,
            plateQuery = "ZZZ",
            startDate = LocalDate.of(2026, 7, 3),
            endDate = LocalDate.of(2026, 7, 4)
        )

        assertTrue(result.isEmpty())
    }

    private fun record(
        id: String,
        plate: String,
        entry: LocalDateTime,
        status: ParkingStatus
    ) = ParkingRecord(
        id = id,
        licensePlate = plate,
        entryTime = entry,
        status = status
    )
}


