package com.parkcontrol.features.monthlyCustomers.domain.repository

import com.parkcontrol.features.monthlyCustomers.domain.model.MonthlyCustomer
import kotlinx.coroutines.flow.Flow

interface MonthlyCustomerRepository {
    fun observeActiveMonthlyCustomers(): Flow<List<MonthlyCustomer>>

    fun observeInactiveMonthlyCustomers(): Flow<List<MonthlyCustomer>>

    suspend fun getMonthlyCustomerById(id: Int): MonthlyCustomer?

    suspend fun addMonthlyCustomer(customer: MonthlyCustomer)

    suspend fun updateMonthlyCustomer(customer: MonthlyCustomer)

    suspend fun inactivateMonthlyCustomer(id: Int)

    suspend fun activateMonthlyCustomer(id: Int)
}

