package com.parkcontrol.features.agreements.ui

import com.parkcontrol.features.agreements.domain.model.Agreement

data class AgreementsListUiState(
    val agreements: List<Agreement> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

