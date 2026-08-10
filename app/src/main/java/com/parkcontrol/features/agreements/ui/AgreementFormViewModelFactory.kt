package com.parkcontrol.features.agreements.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parkcontrol.core.di.CoreDependencies
import com.parkcontrol.features.agreements.domain.usecase.GetAgreementByIdUseCase
import com.parkcontrol.features.agreements.domain.usecase.SaveAgreementUseCase
import com.parkcontrol.features.agreements.domain.usecase.UpdateAgreementUseCase

class AgreementFormViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AgreementFormViewModel::class.java) -> {
                val repository = CoreDependencies.getAgreementRepository(application)
                AgreementFormViewModel(
                    application = application,
                    getAgreementByIdUseCase = GetAgreementByIdUseCase(repository),
                    saveAgreementUseCase = SaveAgreementUseCase(repository),
                    updateAgreementUseCase = UpdateAgreementUseCase(repository)
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

