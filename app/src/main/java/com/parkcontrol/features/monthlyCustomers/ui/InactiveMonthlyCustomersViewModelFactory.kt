package com.parkcontrol.features.monthlyCustomers.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.parkcontrol.core.di.CoreDependencies
import com.parkcontrol.features.monthlyCustomers.domain.usecase.ActivateMonthlyCustomerUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetInactiveMonthlyCustomersUseCase

class InactiveMonthlyCustomersViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(InactiveMonthlyCustomersViewModel::class.java) -> {
                val repository = CoreDependencies.getMonthlyCustomerRepository(application)
                val getInactiveMonthlyCustomersUseCase = GetInactiveMonthlyCustomersUseCase(repository)
                val activateMonthlyCustomerUseCase = ActivateMonthlyCustomerUseCase(repository)

                InactiveMonthlyCustomersViewModel(
                    application = application,
                    getInactiveMonthlyCustomersUseCase = getInactiveMonthlyCustomersUseCase,
                    activateMonthlyCustomerUseCase = activateMonthlyCustomerUseCase
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

