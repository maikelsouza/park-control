package com.parkcontrol.features.monthlyCustomers.data.repository

import com.parkcontrol.features.monthlyCustomers.data.local.dao.MonthlyCustomerDao
import com.parkcontrol.features.monthlyCustomers.data.local.mapper.toDomain
import com.parkcontrol.features.monthlyCustomers.data.local.mapper.toEntity
import com.parkcontrol.features.monthlyCustomers.domain.model.MonthlyCustomer
import com.parkcontrol.features.monthlyCustomers.domain.repository.MonthlyCustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed implementation using SQLite for monthly customers persistence.
 */
class MonthlyCustomerRepositoryImpl(
    private val dao: MonthlyCustomerDao
) : MonthlyCustomerRepository {

    override fun observeActiveMonthlyCustomers(): Flow<List<MonthlyCustomer>> =
        dao.observeActiveCustomers().map { it.toDomain() }

    override fun observeInactiveMonthlyCustomers(): Flow<List<MonthlyCustomer>> =
        dao.observeInactiveCustomers().map { it.toDomain() }

    override suspend fun getMonthlyCustomerById(id: Int): MonthlyCustomer? =
        dao.getCustomerById(id)?.toDomain()

    override suspend fun addMonthlyCustomer(customer: MonthlyCustomer): Int =
        dao.insertCustomer(customer.copy(id = 0).toEntity()).toInt()

    override suspend fun updateMonthlyCustomer(customer: MonthlyCustomer) =
        dao.updateCustomer(customer.toEntity())

    override suspend fun inactivateMonthlyCustomer(id: Int) =
        dao.inactivateCustomer(customerId = id, updatedAt = System.currentTimeMillis())

    override suspend fun activateMonthlyCustomer(id: Int) =
        dao.activateCustomer(customerId = id, updatedAt = System.currentTimeMillis())
}
