package com.parkcontrol.features.parking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.parkcontrol.features.monthlyCustomers.data.local.entity.CustomerPlateEntity
import com.parkcontrol.features.monthlyCustomers.data.local.entity.MonthlyCustomerEntity
import com.parkcontrol.features.parking.data.local.entity.ParkingRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingRecordDao {

    @Query("SELECT * FROM parking_records ORDER BY entryTimeMillis DESC")
    fun observeParkingRecords(): Flow<List<ParkingRecordEntity>>

    @Query("SELECT * FROM parking_records WHERE id = :recordId LIMIT 1")
    suspend fun getParkingRecordById(recordId: String): ParkingRecordEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertParkingRecord(record: ParkingRecordEntity)

    @Update
    suspend fun updateParkingRecord(record: ParkingRecordEntity)

    @Query(
        """
        SELECT mc.id
        FROM monthly_customers mc
        INNER JOIN customer_plates cp ON cp.customerId = mc.id
        WHERE UPPER(TRIM(cp.plate)) = UPPER(TRIM(:plate))
        ORDER BY mc.isActive DESC, cp.isPrimary DESC, mc.id DESC
        LIMIT 1
        """
    )
    suspend fun getCustomerIdByPlate(plate: String): Int?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomer(customer: MonthlyCustomerEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlate(plate: CustomerPlateEntity)

    @Transaction
    suspend fun insertParkingRecordEnsuringCustomer(record: ParkingRecordEntity) {
        val normalizedPlate = record.licensePlate.trim().uppercase()
        val normalizedPhone = record.phone.trim()
        val customerId = getCustomerIdByPlate(normalizedPlate)
            ?: createFallbackCustomer(
                plate = normalizedPlate,
                phone = normalizedPhone
            )

        insertParkingRecord(
            record.copy(
                customerId = customerId,
                licensePlate = normalizedPlate,
                phone = normalizedPhone
            )
         )
     }

     @Query(
         """
         SELECT EXISTS (
             SELECT 1 FROM parking_records
             WHERE UPPER(TRIM(licensePlate)) = UPPER(TRIM(:licensePlate))
             AND status = 'ESTACIONADO'
             LIMIT 1
         )
         """
     )
     suspend fun hasActiveParking(licensePlate: String): Boolean

     private suspend fun createFallbackCustomer(plate: String, phone: String): Int {
        val now = System.currentTimeMillis()
        val customerId = insertCustomer(
            MonthlyCustomerEntity(
                name = "não informado",
                phone = phone,
                isMonthly = false,
                monthlyFeeCents = null,
                dueDay = null,
                isActive = true,
                createdAt = now,
                updatedAt = now
            )
        ).toInt()

        insertPlate(
            CustomerPlateEntity(
                customerId = customerId,
                plate = plate,
                isPrimary = true,
                createdAt = now
            )
        )

        return customerId
    }
}

