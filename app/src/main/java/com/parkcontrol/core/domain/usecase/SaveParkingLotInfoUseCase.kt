package com.parkcontrol.core.domain.usecase

import com.parkcontrol.core.domain.model.ParkingLotInfo
import com.parkcontrol.core.domain.repository.ParkingLotInfoRepository

/**
 * Use case for creating/updating the parking lot registration data.
 */
class SaveParkingLotInfoUseCase(
    private val repository: ParkingLotInfoRepository
) {
    suspend operator fun invoke(info: ParkingLotInfo) {
        repository.saveParkingLotInfo(info)
    }
}

