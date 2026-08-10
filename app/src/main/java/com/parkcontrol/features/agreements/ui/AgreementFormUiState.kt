package com.parkcontrol.features.agreements.ui

import com.parkcontrol.features.agreements.domain.model.Agreement

data class AgreementFormUiState(
    val selectedAgreement: Agreement? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

