package com.parkcontrol.features.agreements.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.parkcontrol.features.agreements.data.local.entity.AgreementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgreementDao {

    @Query("SELECT * FROM agreements WHERE isActive = 1 ORDER BY id DESC")
    fun observeActiveAgreements(): Flow<List<AgreementEntity>>

    @Query("SELECT * FROM agreements WHERE isActive = 0 ORDER BY id DESC")
    fun observeInactiveAgreements(): Flow<List<AgreementEntity>>

    @Query("SELECT * FROM agreements WHERE id = :id LIMIT 1")
    suspend fun getAgreementById(id: Int): AgreementEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAgreement(agreement: AgreementEntity): Long

    @Update
    suspend fun updateAgreement(agreement: AgreementEntity)

    @Query("UPDATE agreements SET isActive = 0, updatedAt = :updatedAt WHERE id = :agreementId")
    suspend fun inactivateAgreement(agreementId: Int, updatedAt: Long)

    @Query("UPDATE agreements SET isActive = 1, updatedAt = :updatedAt WHERE id = :agreementId")
    suspend fun activateAgreement(agreementId: Int, updatedAt: Long)
}

