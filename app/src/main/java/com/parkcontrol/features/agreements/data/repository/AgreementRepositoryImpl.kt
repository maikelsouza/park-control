package com.parkcontrol.features.agreements.data.repository

import com.parkcontrol.features.agreements.data.local.dao.AgreementDao
import com.parkcontrol.features.agreements.data.local.mapper.toDomain
import com.parkcontrol.features.agreements.data.local.mapper.toEntity
import com.parkcontrol.features.agreements.domain.model.Agreement
import com.parkcontrol.features.agreements.domain.repository.AgreementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AgreementRepositoryImpl(
    private val dao: AgreementDao
) : AgreementRepository {

    override fun observeActiveAgreements(): Flow<List<Agreement>> {
        return dao.observeActiveAgreements().map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeInactiveAgreements(): Flow<List<Agreement>> {
        return dao.observeInactiveAgreements().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getAgreementById(id: Int): Agreement? {
        return dao.getAgreementById(id)?.toDomain()
    }

    override suspend fun addAgreement(agreement: Agreement): Int {
        return dao.insertAgreement(agreement.copy(id = 0).toEntity()).toInt()
    }

    override suspend fun updateAgreement(agreement: Agreement) {
        dao.updateAgreement(agreement.toEntity())
    }

    override suspend fun inactivateAgreement(id: Int) {
        dao.inactivateAgreement(agreementId = id, updatedAt = System.currentTimeMillis())
    }

    override suspend fun activateAgreement(id: Int) {
        dao.activateAgreement(agreementId = id, updatedAt = System.currentTimeMillis())
    }
}

