package com.parkcontrol.features.agreements.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.parkcontrol.core.di.CoreDependencies
import com.parkcontrol.features.agreements.domain.usecase.GetActiveAgreementsUseCase
import com.parkcontrol.features.agreements.domain.usecase.InactivateAgreementUseCase

class ActiveAgreementsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ActiveAgreementsViewModel::class.java) -> {
                val repository = CoreDependencies.getAgreementRepository(application)
                ActiveAgreementsViewModel(
                    application = application,
                    getActiveAgreementsUseCase = GetActiveAgreementsUseCase(repository),
                    inactivateAgreementUseCase = InactivateAgreementUseCase(repository)
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

