package com.parkcontrol.features.agreements.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.parkcontrol.features.agreements.domain.model.Agreement
import com.parkcontrol.features.agreements.domain.usecase.GetAgreementByIdUseCase
import com.parkcontrol.features.agreements.domain.usecase.SaveAgreementUseCase
import com.parkcontrol.features.agreements.domain.usecase.UpdateAgreementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AgreementFormViewModel(
    application: Application,
    private val getAgreementByIdUseCase: GetAgreementByIdUseCase,
    private val saveAgreementUseCase: SaveAgreementUseCase,
    private val updateAgreementUseCase: UpdateAgreementUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AgreementFormUiState())
    val uiState: StateFlow<AgreementFormUiState> = _uiState

    fun loadAgreementForEdit(agreementId: Int?) {
        if (agreementId == null) {
            _uiState.value = _uiState.value.copy(selectedAgreement = null)
            return
        }

        viewModelScope.launch {
            val agreement = getAgreementByIdUseCase(agreementId)
            _uiState.value = if (agreement == null) {
                _uiState.value.copy(errorMessage = "Convênio não encontrado")
            } else {
                _uiState.value.copy(selectedAgreement = agreement)
            }
        }
    }

    fun saveAgreement(
        agreementId: Int?,
        name: String,
        contactName: String,
        phone: String,
        email: String,
        street: String,
        number: String,
        complement: String,
        city: String,
        neighborhood: String,
        state: String,
        zipCode: String,
        discountValue: String
    ) {
        val normalizedName = name.trim()
        val normalizedContactName = contactName.trim()
        val normalizedPhone = phone.filter(Char::isDigit).take(11)
        val normalizedEmail = email.trim().lowercase()
        val normalizedStreet = street.trim()
        val normalizedNumber = number.trim()
        val normalizedComplement = complement.trim()
        val normalizedCity = city.trim()
        val normalizedNeighborhood = neighborhood.trim()
        val normalizedState = state.trim().uppercase().take(2)
        val normalizedZipCode = zipCode.filter(Char::isDigit).take(8)
        val discountCents = discountValue.filter(Char::isDigit).toIntOrNull()

        if (normalizedName.isBlank() || normalizedContactName.isBlank() || normalizedPhone.isBlank() ||
            normalizedStreet.isBlank() || normalizedCity.isBlank() || normalizedNeighborhood.isBlank() ||
            normalizedState.isBlank() || normalizedZipCode.isBlank() || discountCents == null
        ) {
            _uiState.value = _uiState.value.copy(errorMessage = "Preencha os campos obrigatórios")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true)

                val existing = agreementId?.let { getAgreementByIdUseCase(it) }
                val now = System.currentTimeMillis()

                val agreement = Agreement(
                    id = existing?.id ?: 0,
                    name = normalizedName,
                    contactName = normalizedContactName,
                    phone = normalizedPhone,
                    email = normalizedEmail,
                    street = normalizedStreet,
                    number = normalizedNumber,
                    complement = normalizedComplement,
                    city = normalizedCity,
                    neighborhood = normalizedNeighborhood,
                    state = normalizedState,
                    zipCode = normalizedZipCode,
                    discountCents = discountCents,
                    isActive = existing?.isActive ?: true,
                    createdAt = existing?.createdAt ?: now,
                    updatedAt = now
                )

                if (agreementId == null) {
                    saveAgreementUseCase(agreement)
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = "Convênio salvo com sucesso"
                    )
                } else {
                    updateAgreementUseCase(agreement)
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = "Convênio atualizado com sucesso"
                    )
                }
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Erro ao salvar convênio"
                )
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

