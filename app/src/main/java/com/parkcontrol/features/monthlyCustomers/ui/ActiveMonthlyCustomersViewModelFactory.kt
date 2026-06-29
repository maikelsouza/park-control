package com.parkcontrol.features.monthlyCustomers.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.parkcontrol.core.di.CoreDependencies
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetMonthlyCustomerByIdUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetActiveMonthlyCustomersUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.InactivateMonthlyCustomerUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.SaveMonthlyCustomerUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.UpdateMonthlyCustomerUseCase

class ActiveMonthlyCustomersViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ActiveMonthlyCustomersViewModel::class.java) -> {
                val repository = CoreDependencies.getMonthlyCustomerRepository(application)

                val getActiveMonthlyCustomersUseCase = GetActiveMonthlyCustomersUseCase(repository)
                val saveMonthlyCustomerUseCase = SaveMonthlyCustomerUseCase(repository)
                val getMonthlyCustomerByIdUseCase = GetMonthlyCustomerByIdUseCase(repository)
                val updateMonthlyCustomerUseCase = UpdateMonthlyCustomerUseCase(repository)
                val inactivateMonthlyCustomerUseCase = InactivateMonthlyCustomerUseCase(repository)

                ActiveMonthlyCustomersViewModel(
                    application,
                    getActiveMonthlyCustomersUseCase,
                    saveMonthlyCustomerUseCase,
                    getMonthlyCustomerByIdUseCase,
                    updateMonthlyCustomerUseCase,
                    inactivateMonthlyCustomerUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}


