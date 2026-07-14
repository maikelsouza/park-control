package com.parkcontrol.features.parking.data.repository

import com.parkcontrol.features.parking.data.local.dao.ParkingRecordDao
import com.parkcontrol.features.parking.data.local.entity.ParkingRecordEntity
import com.parkcontrol.features.parking.data.local.mapper.toDomain
import com.parkcontrol.features.parking.data.local.mapper.toEntity
import com.parkcontrol.features.parking.domain.model.ParkingRecord
import com.parkcontrol.features.parking.domain.repository.ParkingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParkingRepositoryImpl(
    private val dao: ParkingRecordDao
) : ParkingRepository {

    override fun observeParkingRecords(): Flow<List<ParkingRecord>> {
        return dao.observeParkingRecords().map { entities ->
            entities.toDomain()
        }
    }

    override suspend fun addParkingRecord(record: ParkingRecord) {
        dao.insertParkingRecordEnsuringCustomer(record.toEntity())
    }

    override suspend fun updateParkingRecord(record: ParkingRecord) {
        val existingRecord = dao.getParkingRecordById(record.id)
        val recordEntity = record.toUpdatedEntity(existingRecord)
        dao.updateParkingRecord(recordEntity)
    }

    override suspend fun hasActiveParking(licensePlate: String): Boolean {
        return dao.hasActiveParking(licensePlate)
    }

    private fun ParkingRecord.toUpdatedEntity(existingRecord: ParkingRecordEntity?): ParkingRecordEntity {
        val now = System.currentTimeMillis()
        val baseEntity = toEntity()
        return baseEntity.copy(
            customerId = customerId ?: existingRecord?.customerId,
            createdAt = existingRecord?.createdAt ?: now,
            updatedAt = now
        )
    }
}

