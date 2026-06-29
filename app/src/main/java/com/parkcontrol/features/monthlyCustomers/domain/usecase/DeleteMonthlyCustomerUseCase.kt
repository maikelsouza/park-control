package com.parkcontrol.features.monthlyCustomers.domain.usecase

import com.parkcontrol.features.monthlyCustomers.domain.repository.MonthlyCustomerRepository

class InactivateMonthlyCustomerUseCase(
    private val repository: MonthlyCustomerRepository
) {
    suspend operator fun invoke(customerId: Int) {
        repository.inactivateMonthlyCustomer(customerId)
    }
}

