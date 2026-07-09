package com.parkcontrol.features.parking.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parkcontrol.features.parking.domain.usecase.FilterParkedVehiclesUseCase

class ParkedVehiclesViewModelFactory(
    private val application: Application,
    private val filterParkedVehiclesUseCase: FilterParkedVehiclesUseCase = FilterParkedVehiclesUseCase()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ParkedVehiclesViewModel::class.java) -> {
                ParkedVehiclesViewModel(
                    application = application,
                    filterParkedVehiclesUseCase = filterParkedVehiclesUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

