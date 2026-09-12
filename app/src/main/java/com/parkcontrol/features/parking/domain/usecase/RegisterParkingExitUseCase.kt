package com.parkcontrol.features.parking.domain.usecase

import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.model.ParkingStatus
import java.time.LocalDateTime

/**
 * Regra de negócio que finaliza um registro de estacionamento: calcula o
 * valor a pagar (usando [CalculateParkingPriceUseCase]), aplica o desconto
 * (convênio ou manual) já registrado na entrada, e retorna o [ParkingRecord]
 * atualizado com status FINALIZADO, hora de saída e valor final.
 */
class RegisterParkingExitUseCase(
    private val calculateParkingPrice: CalculateParkingPriceUseCase = CalculateParkingPriceUseCase()
) {

    operator fun invoke(
        record: ParkingRecord,
        exitTime: LocalDateTime,
        first30MinutesPrice: Double,
        pricePerHour: Double
    ): ParkingRecord {
        val amountPaid = calculateParkingPrice(
            entry = record.entryTime,
            exit = exitTime,
            first30MinutesPrice = first30MinutesPrice,
            pricePerHour = pricePerHour
        )

        val discountAmount = record.discountAmount ?: 0.0
        val finalAmount = (amountPaid - discountAmount).coerceAtLeast(0.0)

        return record.copy(
            exitTime = exitTime,
            status = ParkingStatus.FINALIZADO,
            amountPaid = finalAmount
        )
    }
}

