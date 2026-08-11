package com.parkcontrol.features.parking.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parkcontrol.core.di.CoreDependencies
import com.parkcontrol.features.parking.domain.usecase.CalculateParkingPriceUseCase

class ParkingViewModelFactory(
    private val application: Application,
    private val calculateParkingPriceUseCase: CalculateParkingPriceUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ParkingViewModel::class.java) -> {
                ParkingViewModel(
                    application,
                    CoreDependencies.createGetActiveAgreementsUseCase(application),
                    calculateParkingPriceUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

