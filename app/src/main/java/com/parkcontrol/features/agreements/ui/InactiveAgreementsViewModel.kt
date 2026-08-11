package com.parkcontrol.features.agreements.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.parkcontrol.features.agreements.domain.usecase.ActivateAgreementUseCase
import com.parkcontrol.features.agreements.domain.usecase.GetInactiveAgreementsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InactiveAgreementsViewModel(
    application: Application,
    private val getInactiveAgreementsUseCase: GetInactiveAgreementsUseCase,
    private val activateAgreementUseCase: ActivateAgreementUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AgreementsListUiState())
    val uiState: StateFlow<AgreementsListUiState> = _uiState

    init {
        loadAgreements()
    }

    private fun loadAgreements() {
        viewModelScope.launch {
            getInactiveAgreementsUseCase().collect { agreements ->
                _uiState.value = _uiState.value.copy(agreements = agreements)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun activateAgreement(agreementId: Int) {
        viewModelScope.launch {
            try {
                activateAgreementUseCase(agreementId)
                _uiState.value = _uiState.value.copy(successMessage = "Convênio ativado com sucesso")
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Erro ao ativar convênio")
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

