package com.parkcontrol.features.monthlyCustomers.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.parkcontrol.features.monthlyCustomers.data.local.entity.CustomerPlateEntity
import com.parkcontrol.features.monthlyCustomers.data.local.entity.MonthlyCustomerEntity
import com.parkcontrol.features.monthlyCustomers.data.local.entity.MonthlyCustomerWithPlates
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyCustomerDao {

    @Transaction
    @Query("SELECT * FROM monthly_customers WHERE isActive = 1 ORDER BY id DESC")
    fun observeActiveCustomers(): Flow<List<MonthlyCustomerWithPlates>>

    @Transaction
    @Query("SELECT * FROM monthly_customers WHERE isActive = 0 ORDER BY id DESC")
    fun observeInactiveCustomers(): Flow<List<MonthlyCustomerWithPlates>>

    @Transaction
    @Query("SELECT * FROM monthly_customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Int): MonthlyCustomerWithPlates?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCustomer(customer: MonthlyCustomerEntity): Long

    @Update
    suspend fun updateCustomer(customer: MonthlyCustomerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlates(plates: List<CustomerPlateEntity>)

    @Query("DELETE FROM customer_plates WHERE customerId = :customerId")
    suspend fun deletePlatesByCustomerId(customerId: Int)

    @Query("UPDATE monthly_customers SET isActive = 0, updatedAt = :updatedAt WHERE id = :customerId")
    suspend fun inactivateCustomer(customerId: Int, updatedAt: Long)

    @Query("UPDATE monthly_customers SET isActive = 1, updatedAt = :updatedAt WHERE id = :customerId")
    suspend fun activateCustomer(customerId: Int, updatedAt: Long)

    @Transaction
    suspend fun replaceCustomerPlates(customerId: Int, plates: List<CustomerPlateEntity>) {
        deletePlatesByCustomerId(customerId)
        if (plates.isNotEmpty()) {
            insertPlates(plates)
        }
    }

    @Query("SELECT COUNT(*) FROM monthly_customers WHERE isActive = 1")
    suspend fun getActiveCustomerCount(): Int
}

