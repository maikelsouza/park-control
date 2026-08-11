package com.parkcontrol.features.agreements.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.parkcontrol.core.di.CoreDependencies
import com.parkcontrol.features.agreements.domain.usecase.ActivateAgreementUseCase
import com.parkcontrol.features.agreements.domain.usecase.GetInactiveAgreementsUseCase

class InactiveAgreementsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(InactiveAgreementsViewModel::class.java) -> {
                val repository = CoreDependencies.getAgreementRepository(application)
                InactiveAgreementsViewModel(
                    application = application,
                    getInactiveAgreementsUseCase = GetInactiveAgreementsUseCase(repository),
                    activateAgreementUseCase = ActivateAgreementUseCase(repository)
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

