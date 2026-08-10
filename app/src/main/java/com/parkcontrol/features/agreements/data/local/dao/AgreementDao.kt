package com.parkcontrol.features.agreements.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.parkcontrol.features.agreements.data.local.entity.AgreementEntity

@Dao
interface AgreementDao {

    @Query("SELECT * FROM agreements WHERE id = :id LIMIT 1")
    suspend fun getAgreementById(id: Int): AgreementEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAgreement(agreement: AgreementEntity): Long

    @Update
    suspend fun updateAgreement(agreement: AgreementEntity)
}

