package com.parkcontrol.features.agreements.domain.repository

import com.parkcontrol.features.agreements.domain.model.Agreement
import kotlinx.coroutines.flow.Flow

interface AgreementRepository {
    fun observeActiveAgreements(): Flow<List<Agreement>>

    fun observeInactiveAgreements(): Flow<List<Agreement>>

    suspend fun getAgreementById(id: Int): Agreement?

    suspend fun addAgreement(agreement: Agreement): Int

    suspend fun updateAgreement(agreement: Agreement)

    suspend fun inactivateAgreement(id: Int)

    suspend fun activateAgreement(id: Int)
}

