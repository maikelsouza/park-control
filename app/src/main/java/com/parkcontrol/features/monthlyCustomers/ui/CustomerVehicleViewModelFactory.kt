package com.parkcontrol.features.monthlyCustomers.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.parkcontrol.core.di.CoreDependencies
import com.parkcontrol.features.monthlyCustomers.domain.usecase.DeleteVehicleUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetMonthlyCustomerByIdUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetVehicleByIdUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.GetVehiclesByCustomerUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.SaveVehicleUseCase
import com.parkcontrol.features.monthlyCustomers.domain.usecase.UpdateVehicleUseCase

class CustomerVehicleViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CustomerVehicleViewModel::class.java) -> {
                val vehicleRepo = CoreDependencies.getCustomerVehicleRepository(application)
                val customerRepo = CoreDependencies.getMonthlyCustomerRepository(application)

                CustomerVehicleViewModel(
                    application,
                    GetVehiclesByCustomerUseCase(vehicleRepo),
                    GetVehicleByIdUseCase(vehicleRepo),
                    SaveVehicleUseCase(vehicleRepo),
                    UpdateVehicleUseCase(vehicleRepo),
                    DeleteVehicleUseCase(vehicleRepo),
                    GetMonthlyCustomerByIdUseCase(customerRepo)
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

