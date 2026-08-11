package com.parkcontrol.features.agreements.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.parkcontrol.features.agreements.domain.usecase.GetActiveAgreementsUseCase
import com.parkcontrol.features.agreements.domain.usecase.InactivateAgreementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActiveAgreementsViewModel(
    application: Application,
    private val getActiveAgreementsUseCase: GetActiveAgreementsUseCase,
    private val inactivateAgreementUseCase: InactivateAgreementUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AgreementsListUiState())
    val uiState: StateFlow<AgreementsListUiState> = _uiState

    init {
        loadAgreements()
    }

    private fun loadAgreements() {
        viewModelScope.launch {
            getActiveAgreementsUseCase().collect { agreements ->
                _uiState.value = _uiState.value.copy(agreements = agreements)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun inactivateAgreement(agreementId: Int) {
        viewModelScope.launch {
            try {
                inactivateAgreementUseCase(agreementId)
                _uiState.value = _uiState.value.copy(successMessage = "Convênio inativado com sucesso")
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Erro ao inativar convênio")
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
}

