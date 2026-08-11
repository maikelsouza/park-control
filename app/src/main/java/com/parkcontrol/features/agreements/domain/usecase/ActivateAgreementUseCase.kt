package com.parkcontrol.features.agreements.domain.usecase

import com.parkcontrol.features.agreements.domain.repository.AgreementRepository

class ActivateAgreementUseCase(
    private val repository: AgreementRepository
) {
    suspend operator fun invoke(agreementId: Int) {
        repository.activateAgreement(agreementId)
    }
}

