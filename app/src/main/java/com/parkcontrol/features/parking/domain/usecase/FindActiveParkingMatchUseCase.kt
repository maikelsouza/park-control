package com.parkcontrol.features.parking.domain.usecase

import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.model.ParkingStatus

/**
 * Regra de negócio que encontra, entre os registros em aberto (ESTACIONADO),
 * aquele cuja placa (ou telefone, quando digitado por completo) corresponda
 * exatamente ao que foi informado.
 *
 * Usado para selecionar automaticamente o registro de um veículo já
 * estacionado assim que o usuário termina de digitar a placa/telefone,
 * sem precisar clicar manualmente numa sugestão — permitindo, por exemplo,
 * dar saída ou reemitir o QR-code do ticket digital.
 */
class FindActiveParkingMatchUseCase {

    operator fun invoke(
        records: List<ParkingRecord>,
        plateFilter: String,
        phoneFilter: String
    ): ParkingRecord? {
        val normalizedPlate = plateFilter.trim().uppercase()
        val normalizedPhone = phoneFilter.filter(Char::isDigit)

        if (normalizedPlate.isEmpty() && normalizedPhone.isEmpty()) return null

        return records.firstOrNull { record ->
            if (record.status != ParkingStatus.ESTACIONADO) return@firstOrNull false
            when {
                normalizedPlate.isNotEmpty() -> record.licensePlate.uppercase() == normalizedPlate
                // Só considera telefone "completo" (DDD + número) para evitar
                // uma correspondência prematura enquanto o usuário ainda digita.
                normalizedPhone.length >= 10 -> record.phone.filter(Char::isDigit) == normalizedPhone
                else -> false
            }
        }
    }
}

