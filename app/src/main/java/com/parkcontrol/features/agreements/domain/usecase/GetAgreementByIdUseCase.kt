package com.parkcontrol.features.agreements.domain.usecase

import com.parkcontrol.features.agreements.domain.model.Agreement
import com.parkcontrol.features.agreements.domain.repository.AgreementRepository

class GetAgreementByIdUseCase(
    private val repository: AgreementRepository
) {
    suspend operator fun invoke(id: Int): Agreement? {
        return repository.getAgreementById(id)
    }
}

