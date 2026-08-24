package com.parkcontrol.features.agreements.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkcontrol.core.navigation.AppDrawerScaffold
import com.parkcontrol.core.navigation.AppRoutes
import com.parkcontrol.core.ui.masks.CurrencyMaskTransformation
import com.parkcontrol.core.ui.masks.PhoneMaskTransformation
import com.parkcontrol.core.ui.masks.ZipCodeMaskTransformation
import com.parkcontrol.core.ui.masks.onlyMoneyDigits
import com.parkcontrol.core.ui.masks.onlyPhoneDigits
import com.parkcontrol.core.ui.masks.onlyZipCodeDigits
import com.parkcontrol.core.utils.looksLikeEmail
import com.parkcontrol.core.utils.sanitizeEmailInput

private val BrazilianStates = listOf(
    "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO",
    "MA", "MG", "MS", "MT", "PA", "PB", "PE", "PI", "PR",
    "RJ", "RN", "RO", "RR", "RS", "SC", "SE", "SP", "TO"
)

@Composable
fun AgreementsScreen(
    onNavigate: (String) -> Unit,
    agreementId: Int? = null,
    onFinish: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    AppDrawerScaffold(
        currentRoute = AppRoutes.AgreementsActive.route,
        onNavigate = onNavigate
    ) { paddingValues ->
        val context = LocalContext.current
        val application = context.applicationContext as android.app.Application
        val factory = AgreementFormViewModelFactory(application)
        val viewModel: AgreementFormViewModel = viewModel(factory = factory)
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        AgreementsFormContent(
            modifier = Modifier.padding(paddingValues),
            agreementId = agreementId,
            snackbarHostState = snackbarHostState,
            onBack = onBack ?: onFinish,
            onSave = { data ->
                viewModel.saveAgreement(
                    agreementId = agreementId,
                    name = data.name,
                    contactName = data.contactName,
                    phone = data.phone,
                    email = data.email,
                    street = data.street,
                    number = data.number,
                    complement = data.complement,
                    city = data.city,
                    neighborhood = data.neighborhood,
                    state = data.state,
                    zipCode = data.zipCode,
                    discountValue = data.discountValue
                )
            },
            onLoadForEdit = {
                viewModel.loadAgreementForEdit(agreementId)
            },
            selectedAgreement = uiState.selectedAgreement,
            onErrorConsumed = {
                viewModel.clearErrorMessage()
            },
            onSuccessConsumed = {
                viewModel.clearSuccessMessage()
                onFinish?.invoke()
            },
            isSaving = uiState.isSaving,
            errorMessage = uiState.errorMessage,
            successMessage = uiState.successMessage
        )
    }
}

private data class AgreementFormData(
    val name: String,
    val contactName: String,
    val phone: String,
    val email: String,
    val street: String,
    val number: String,
    val complement: String,
    val city: String,
    val neighborhood: String,
    val state: String,
    val zipCode: String,
    val discountValue: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgreementsFormContent(
    modifier: Modifier = Modifier,
    agreementId: Int?,
    snackbarHostState: SnackbarHostState,
    onBack: (() -> Unit)? = null,
    onSave: (AgreementFormData) -> Unit,
    onLoadForEdit: () -> Unit,
    selectedAgreement: com.parkcontrol.features.agreements.domain.model.Agreement?,
    onErrorConsumed: () -> Unit,
    onSuccessConsumed: () -> Unit,
    isSaving: Boolean,
    errorMessage: String?,
    successMessage: String?
) {
    val colorScheme = MaterialTheme.colorScheme

    var name by rememberSaveable { mutableStateOf("") }
    var contactName by rememberSaveable { mutableStateOf("") }
    var contactPhone by rememberSaveable { mutableStateOf("") }
    var contactEmail by rememberSaveable { mutableStateOf("") }
    var street by rememberSaveable { mutableStateOf("") }
    var number by rememberSaveable { mutableStateOf("") }
    var complement by rememberSaveable { mutableStateOf("") }
    var neighborhood by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var state by rememberSaveable { mutableStateOf("") }
    var stateExpanded by remember { mutableStateOf(false) }
    var zipCode by rememberSaveable { mutableStateOf("") }
    var discountValue by rememberSaveable { mutableStateOf("") }
    var showValidation by rememberSaveable { mutableStateOf(false) }
    var didPrefill by rememberSaveable(agreementId) { mutableStateOf(false) }
    var emailFieldTouched by rememberSaveable { mutableStateOf(false) }

    val nameError = requiredFieldError(name, showValidation)
    val contactNameError = requiredFieldError(contactName, showValidation)
    val phoneError = requiredFieldError(contactPhone, showValidation)
    val streetError = requiredFieldError(street, showValidation)
    val neighborhoodError = requiredFieldError(neighborhood, showValidation)
    val cityError = requiredFieldError(city, showValidation)
    val stateError = requiredFieldError(state, showValidation)
    val zipCodeError = requiredFieldError(zipCode, showValidation)
    val discountError = requiredFieldError(discountValue, showValidation)

    val isEmailValid = contactEmail.isBlank() || contactEmail.looksLikeEmail()
    val showEmailError = (emailFieldTouched || showValidation) && contactEmail.isNotBlank() && !contactEmail.looksLikeEmail()

    val isFormValid = name.isNotBlank() &&
        contactName.isNotBlank() &&
        contactPhone.isNotBlank() &&
        street.isNotBlank() &&
        neighborhood.isNotBlank() &&
        city.isNotBlank() &&
        state.isNotBlank() &&
        zipCode.isNotBlank() &&
        discountValue.isNotBlank() &&
        isEmailValid

    LaunchedEffect(agreementId) {
        onLoadForEdit()
    }

    LaunchedEffect(selectedAgreement, agreementId) {
        val agreement = selectedAgreement
        if (agreementId != null && agreement != null && !didPrefill) {
            name = agreement.name
            contactName = agreement.contactName
            contactPhone = agreement.phone.onlyPhoneDigits().take(11)
            contactEmail = agreement.email
            street = agreement.street
            number = agreement.number
            complement = agreement.complement
            neighborhood = agreement.neighborhood
            city = agreement.city
            state = agreement.state
            zipCode = agreement.zipCode.onlyZipCodeDigits().take(8)
            discountValue = agreement.discountCents.toString()
            didPrefill = true
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onErrorConsumed()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onSuccessConsumed()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (agreementId == null) "Novo convênio" else "Editar convênio",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )
        }

        Text(
            text = if (agreementId == null) {
                "Cadastre os dados de um novo convênio"
            } else {
                "Atualize os dados do convênio"
            },
            color = colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome do convênio *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            isError = nameError != null,
            supportingText = {
                if (nameError != null) Text(nameError, color = colorScheme.error)
            }
        )

        OutlinedTextField(
            value = contactName,
            onValueChange = { contactName = it },
            label = { Text("Nome do responsável *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            isError = contactNameError != null,
            supportingText = {
                if (contactNameError != null) Text(contactNameError, color = colorScheme.error)
            }
        )

        OutlinedTextField(
            value = contactPhone,
            onValueChange = { typed -> contactPhone = typed.onlyPhoneDigits().take(11) },
            label = { Text("Telefone *") },
            placeholder = { Text("(00) 00000-0000") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            visualTransformation = PhoneMaskTransformation,
            isError = phoneError != null,
            supportingText = {
                if (phoneError != null) Text(phoneError, color = colorScheme.error)
            }
        )

        OutlinedTextField(
            value = contactEmail,
            onValueChange = { typed ->
                emailFieldTouched = true
                contactEmail = typed.sanitizeEmailInput()
            },
            label = { Text("E-mail") },
            placeholder = { Text("ex: nome@dominio.com") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = showEmailError,
            supportingText = {
                if (showEmailError) Text("Informe um e-mail válido", color = colorScheme.error)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false
            )
        )

        OutlinedTextField(
            value = street,
            onValueChange = { street = it },
            label = { Text("Rua *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            isError = streetError != null,
            supportingText = {
                if (streetError != null) Text(streetError, color = colorScheme.error)
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = number,
                onValueChange = { number = it },
                label = { Text("Número") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = complement,
                onValueChange = { complement = it },
                label = { Text("Complemento") },
                placeholder = { Text("Apto, sala, bloco...") },
                modifier = Modifier.weight(2f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
        }

        OutlinedTextField(
            value = neighborhood,
            onValueChange = { neighborhood = it },
            label = { Text("Bairro *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            isError = neighborhoodError != null,
            supportingText = {
                if (neighborhoodError != null) Text(neighborhoodError, color = colorScheme.error)
            }
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Cidade *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            isError = cityError != null,
            supportingText = {
                if (cityError != null) Text(cityError, color = colorScheme.error)
            }
        )

        ExposedDropdownMenuBox(
            expanded = stateExpanded,
            onExpandedChange = { stateExpanded = !stateExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state,
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado *") },
                placeholder = { Text("UF") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
                singleLine = true,
                isError = stateError != null,
                supportingText = {
                    if (stateError != null) Text(stateError, color = colorScheme.error)
                }
            )
            ExposedDropdownMenu(
                expanded = stateExpanded,
                onDismissRequest = { stateExpanded = false }
            ) {
                BrazilianStates.forEach { uf ->
                    DropdownMenuItem(
                        text = { Text(uf) },
                        onClick = {
                            state = uf
                            stateExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = zipCode,
            onValueChange = { typed -> zipCode = typed.onlyZipCodeDigits().take(8) },
            label = { Text("CEP *") },
            placeholder = { Text("00000-000") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = ZipCodeMaskTransformation,
            isError = zipCodeError != null,
            supportingText = {
                if (zipCodeError != null) Text(zipCodeError, color = colorScheme.error)
            }
        )

        OutlinedTextField(
            value = discountValue,
            onValueChange = { typed -> discountValue = typed.onlyMoneyDigits().take(11) },
            label = { Text("Valor de desconto (R$) *") },
            placeholder = { Text("Ex: 5,00") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = CurrencyMaskTransformation,
            isError = discountError != null,
            supportingText = {
                if (discountError != null) Text(discountError, color = colorScheme.error)
            }
        )

        Button(
            onClick = {
                showValidation = true
                emailFieldTouched = true
                if (!isFormValid || isSaving) return@Button

                onSave(
                    AgreementFormData(
                        name = name,
                        contactName = contactName,
                        phone = contactPhone,
                        email = contactEmail,
                        street = street,
                        number = number,
                        complement = complement,
                        city = city,
                        neighborhood = neighborhood,
                        state = state,
                        zipCode = zipCode,
                        discountValue = discountValue
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (agreementId == null) "Salvar" else "Atualizar",
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimary
            )
        }

        SnackbarHost(hostState = snackbarHostState)

        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun requiredFieldError(value: String, showValidation: Boolean): String? {
    return if (showValidation && value.isBlank()) "Campo obrigatório" else null
}







