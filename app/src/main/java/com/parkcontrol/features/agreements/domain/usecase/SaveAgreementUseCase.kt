package com.parkcontrol.features.agreements.domain.usecase

import com.parkcontrol.features.agreements.domain.model.Agreement
import com.parkcontrol.features.agreements.domain.repository.AgreementRepository

class SaveAgreementUseCase(
    private val repository: AgreementRepository
) {
    suspend operator fun invoke(agreement: Agreement): Int {
        return repository.addAgreement(agreement)
    }
}

