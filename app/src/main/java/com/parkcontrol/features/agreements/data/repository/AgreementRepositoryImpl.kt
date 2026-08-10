package com.parkcontrol.features.agreements.data.repository

import com.parkcontrol.features.agreements.data.local.dao.AgreementDao
import com.parkcontrol.features.agreements.data.local.mapper.toDomain
import com.parkcontrol.features.agreements.data.local.mapper.toEntity
import com.parkcontrol.features.agreements.domain.model.Agreement
import com.parkcontrol.features.agreements.domain.repository.AgreementRepository

class AgreementRepositoryImpl(
    private val dao: AgreementDao
) : AgreementRepository {

    override suspend fun getAgreementById(id: Int): Agreement? {
        return dao.getAgreementById(id)?.toDomain()
    }

    override suspend fun addAgreement(agreement: Agreement): Int {
        return dao.insertAgreement(agreement.copy(id = 0).toEntity()).toInt()
    }

    override suspend fun updateAgreement(agreement: Agreement) {
        dao.updateAgreement(agreement.toEntity())
    }
}

