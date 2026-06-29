package com.parkcontrol.features.monthlyCustomers.domain.usecase

import com.parkcontrol.features.monthlyCustomers.domain.model.MonthlyCustomer
import com.parkcontrol.features.monthlyCustomers.domain.repository.MonthlyCustomerRepository
import kotlinx.coroutines.flow.Flow

class GetInactiveMonthlyCustomersUseCase(
    private val repository: MonthlyCustomerRepository
) {
    operator fun invoke(): Flow<List<MonthlyCustomer>> {
        return repository.observeInactiveMonthlyCustomers()
    }
}

