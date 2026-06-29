package com.parkcontrol.features.monthlyCustomers.domain.usecase

import com.parkcontrol.features.monthlyCustomers.domain.model.MonthlyCustomer
import com.parkcontrol.features.monthlyCustomers.domain.repository.MonthlyCustomerRepository

class GetMonthlyCustomerByIdUseCase(
    private val repository: MonthlyCustomerRepository
) {
    suspend operator fun invoke(id: Int): MonthlyCustomer? {
        return repository.getMonthlyCustomerById(id)
    }
}

