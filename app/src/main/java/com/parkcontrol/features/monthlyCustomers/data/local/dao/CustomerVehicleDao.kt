package com.parkcontrol.features.monthlyCustomers.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.parkcontrol.features.monthlyCustomers.data.local.entity.CustomerVehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerVehicleDao {

    @Query("SELECT * FROM customer_vehicles WHERE customerId = :customerId ORDER BY isPrimary DESC, id ASC")
    fun observeVehiclesByCustomer(customerId: Int): Flow<List<CustomerVehicleEntity>>

    @Query("SELECT * FROM customer_vehicles WHERE id = :vehicleId LIMIT 1")
    suspend fun getVehicleById(vehicleId: Int): CustomerVehicleEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertVehicle(vehicle: CustomerVehicleEntity): Long

    @Update
    suspend fun updateVehicle(vehicle: CustomerVehicleEntity)

    @Query("DELETE FROM customer_vehicles WHERE id = :vehicleId")
    suspend fun deleteVehicle(vehicleId: Int)

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM customer_vehicles
            WHERE UPPER(TRIM(plate)) = UPPER(TRIM(:plate))
            AND id != :excludeId
            LIMIT 1
        )
        """
    )
    suspend fun isPlateUsed(plate: String, excludeId: Int): Boolean
}

