package com.parkcontrol.core.domain.usecase

import com.parkcontrol.core.domain.model.ParkingConfig
import com.parkcontrol.core.domain.repository.ParkingConfigRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving current parking configuration.
 * Shared across features that need to read parking settings (e.g., Parking feature).
 */
class GetParkingConfigUseCase(
    private val repository: ParkingConfigRepository
) {
    operator fun invoke(): Flow<ParkingConfig> {
        return repository.observeParkingConfig()
    }
}

