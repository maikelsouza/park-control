package com.parkcontrol.features.agreements.domain.usecase

import com.parkcontrol.features.agreements.domain.model.Agreement
import com.parkcontrol.features.agreements.domain.repository.AgreementRepository
import kotlinx.coroutines.flow.Flow

class GetInactiveAgreementsUseCase(
    private val repository: AgreementRepository
) {
    operator fun invoke(): Flow<List<Agreement>> = repository.observeInactiveAgreements()
}

