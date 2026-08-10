package com.parkcontrol.features.agreements.domain.repository

import com.parkcontrol.features.agreements.domain.model.Agreement

interface AgreementRepository {
    suspend fun getAgreementById(id: Int): Agreement?

    suspend fun addAgreement(agreement: Agreement): Int

    suspend fun updateAgreement(agreement: Agreement)
}

