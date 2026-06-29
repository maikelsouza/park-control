package com.parkcontrol.features.monthlyCustomers.domain.usecase

import com.parkcontrol.features.monthlyCustomers.domain.model.MonthlyCustomer
import com.parkcontrol.features.monthlyCustomers.domain.repository.MonthlyCustomerRepository

class SaveMonthlyCustomerUseCase(
    private val repository: MonthlyCustomerRepository
) {
    suspend operator fun invoke(customer: MonthlyCustomer) {
        repository.addMonthlyCustomer(customer)
    }
}

