package com.parkcontrol.core.domain.usecase

import com.parkcontrol.core.domain.model.ParkingLotInfo
import com.parkcontrol.core.domain.repository.ParkingLotInfoRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving the current parking lot registration data.
 */
class GetParkingLotInfoUseCase(
    private val repository: ParkingLotInfoRepository
) {
    operator fun invoke(): Flow<ParkingLotInfo> {
        return repository.observeParkingLotInfo()
    }
}

