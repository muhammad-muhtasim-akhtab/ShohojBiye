package com.example.shohojbiye.ui.registration

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shohojbiye.R
import com.example.shohojbiye.ui.components.PdfViewerDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationFormScreen(
    onRegistrationSuccess: (String) -> Unit,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    var agreedToLegal by rememberSaveable { mutableStateOf(false) }
    
    // Step 1: Parties
    var groomId by rememberSaveable { mutableStateOf("") }
    var brideId by rememberSaveable { mutableStateOf("") }
    
    // Step 2: Witnesses
    var w1Name by rememberSaveable { mutableStateOf("") }
    var w1Nid by rememberSaveable { mutableStateOf("") }
    var w2Name by rememberSaveable { mutableStateOf("") }
    var w2Nid by rememberSaveable { mutableStateOf("") }
    
    // Step 3: Vakil
    var vakilName by rememberSaveable { mutableStateOf("") }
    var vakilNid by rememberSaveable { mutableStateOf("") }

    // Step 4: Mahr & Venue
    var venue by rememberSaveable { mutableStateOf("") }
    var mahrTotal by rememberSaveable { mutableStateOf("") }
    var mahrPrompt by rememberSaveable { mutableStateOf("") }
    var mahrDeferred by rememberSaveable { mutableStateOf("") }
    var mahrProperty by rememberSaveable { mutableStateOf("") }
    
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
    val dateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))

    val registrationState by viewModel.registrationState.collectAsState()
    val calculatedFee by viewModel.kaziFee.collectAsState()

    val isNidValid = { nid: String -> nid.length == 10 || nid.length == 17 }

    val canGoNext = when (currentStep) {
        0 -> agreedToLegal
        1 -> isNidValid(groomId) && isNidValid(brideId)
        2 -> w1Name.isNotBlank() && isNidValid(w1Nid) && 
             w2Name.isNotBlank() && isNidValid(w2Nid)
        3 -> true // Vakil is optional
        4 -> {
            val total = mahrTotal.toDoubleOrNull() ?: 0.0
            val prompt = mahrPrompt.toDoubleOrNull() ?: 0.0
            val deferred = mahrDeferred.toDoubleOrNull() ?: 0.0
            venue.isNotBlank() && total > 0 && total == (prompt + deferred)
        }
        else -> true
    }

    LaunchedEffect(registrationState) {
        if (registrationState is RegistrationViewModel.RegistrationState.Success) {
            onRegistrationSuccess((registrationState as RegistrationViewModel.RegistrationState.Success).regId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(stringResource(R.string.register_marriage))
                        Text(
                            text = stringResource(R.string.step_indicator, currentStep + 1, 6),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    if (currentStep > 0) {
                        IconButton(onClick = { currentStep-- }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep < 5) {
                        Spacer(Modifier)
                        Button(
                            onClick = { currentStep++ },
                            enabled = canGoNext
                        ) {
                            Text("Next")
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.register(
                                    groomId, brideId, selectedDate, venue,
                                    mahrTotal.toDoubleOrNull() ?: 0.0,
                                    mahrPrompt.toDoubleOrNull() ?: 0.0,
                                    mahrDeferred.toDoubleOrNull() ?: 0.0,
                                    mahrProperty,
                                    w1Name, w1Nid, w2Name, w2Nid,
                                    vakilName, vakilNid
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = registrationState !is RegistrationViewModel.RegistrationState.Loading
                        ) {
                            if (registrationState is RegistrationViewModel.RegistrationState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Text("Finalize Registration")
                                Icon(Icons.Default.Check, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            LinearProgressIndicator(
                progress = { (currentStep + 1) / 6f },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            when (currentStep) {
                0 -> DisclaimerStep(agreedToLegal, { agreedToLegal = it })
                1 -> PartiesStep(groomId, { groomId = it }, brideId, { brideId = it })
                2 -> WitnessesStep(w1Name, { w1Name = it }, w1Nid, { w1Nid = it }, w2Name, { w2Name = it }, w2Nid, { w2Nid = it })
                3 -> VakilStep(vakilName, { vakilName = it }, vakilNid, { vakilNid = it })
                4 -> MahrStep(
                    venue, { venue = it },
                    mahrTotal, { 
                        mahrTotal = it
                        viewModel.onMahrChanged(it.toDoubleOrNull() ?: 0.0)
                    },
                    mahrPrompt, { mahrPrompt = it },
                    mahrDeferred, { mahrDeferred = it },
                    mahrProperty, { mahrProperty = it },
                    dateString, { showDatePicker = true }
                )
                5 -> ReviewStep(
                    groomId, brideId, w1Name, w2Name, vakilName, 
                    venue, mahrTotal, calculatedFee
                )
            }
            
            if (registrationState is RegistrationViewModel.RegistrationState.Error) {
                Text(
                    (registrationState as RegistrationViewModel.RegistrationState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DisclaimerStep(agreed: Boolean, onAgreedChange: (Boolean) -> Unit) {
    var showPdfDialog by remember { mutableStateOf(false) }

    Column {
        Text(stringResource(R.string.legal_disclaimer_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text(stringResource(R.string.legal_disclaimer_body), style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { showPdfDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(stringResource(R.string.view_act_pdf))
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = agreed, onCheckedChange = onAgreedChange)
            Text(stringResource(R.string.legal_agreement_checkbox), style = MaterialTheme.typography.bodyMedium)
        }
    }

    if (showPdfDialog) {
        PdfViewerDialog(
            pdfAssetPath = "muslim_marriages_act_1974.pdf",
            onDismissRequest = { showPdfDialog = false }
        )
    }
}

@Composable
fun PartiesStep(gId: String, onGChange: (String) -> Unit, bId: String, onBChange: (String) -> Unit) {
    val isInvalid = { nid: String -> nid.isNotEmpty() && nid.length != 10 && nid.length != 17 }
    Column {
        Text(stringResource(R.string.step_parties), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        TextField(
            value = gId, 
            onValueChange = onGChange, 
            label = { Text(stringResource(R.string.groom_nid)) }, 
            modifier = Modifier.fillMaxWidth(),
            isError = isInvalid(gId),
            supportingText = { if (isInvalid(gId)) Text(stringResource(R.string.error_invalid_nid)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = bId, 
            onValueChange = onBChange, 
            label = { Text(stringResource(R.string.bride_nid)) }, 
            modifier = Modifier.fillMaxWidth(),
            isError = isInvalid(bId),
            supportingText = { if (isInvalid(bId)) Text(stringResource(R.string.error_invalid_nid)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
fun WitnessesStep(w1N: String, onW1N: (String) -> Unit, w1I: String, onW1I: (String) -> Unit, w2N: String, onW2N: (String) -> Unit, w2I: String, onW2I: (String) -> Unit) {
    val isInvalid = { nid: String -> nid.isNotEmpty() && nid.length != 10 && nid.length != 17 }
    Column {
        Text(stringResource(R.string.step_witnesses), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Text("Witness 1", style = MaterialTheme.typography.titleSmall)
        TextField(value = w1N, onValueChange = onW1N, label = { Text(stringResource(R.string.witness_name)) }, modifier = Modifier.fillMaxWidth())
        TextField(
            value = w1I, 
            onValueChange = onW1I, 
            label = { Text(stringResource(R.string.witness_nid)) }, 
            modifier = Modifier.fillMaxWidth(),
            isError = isInvalid(w1I),
            supportingText = { if (isInvalid(w1I)) Text(stringResource(R.string.error_invalid_nid)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(16.dp))
        Text("Witness 2", style = MaterialTheme.typography.titleSmall)
        TextField(value = w2N, onValueChange = onW2N, label = { Text(stringResource(R.string.witness_name)) }, modifier = Modifier.fillMaxWidth())
        TextField(
            value = w2I, 
            onValueChange = onW2I, 
            label = { Text(stringResource(R.string.witness_nid)) }, 
            modifier = Modifier.fillMaxWidth(),
            isError = isInvalid(w2I),
            supportingText = { if (isInvalid(w2I)) Text(stringResource(R.string.error_invalid_nid)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
fun VakilStep(vN: String, onVN: (String) -> Unit, vI: String, onVI: (String) -> Unit) {
    Column {
        Text("Vakil (Representative) - Optional", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        TextField(value = vN, onValueChange = onVN, label = { Text(stringResource(R.string.vakil_name)) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        TextField(
            value = vI, 
            onValueChange = onVI, 
            label = { Text("Vakil NID") }, 
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
fun MahrStep(
    venue: String, onV: (String) -> Unit,
    total: String, onT: (String) -> Unit,
    prompt: String, onP: (String) -> Unit,
    deferred: String, onD: (String) -> Unit,
    property: String, onProp: (String) -> Unit,
    date: String, onDate: () -> Unit
) {
    val isMismatch = (total.toDoubleOrNull() ?: 0.0) != ((prompt.toDoubleOrNull() ?: 0.0) + (deferred.toDoubleOrNull() ?: 0.0))
    
    Column {
        Text(stringResource(R.string.step_mahr), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        OutlinedCard(onClick = onDate, modifier = Modifier.fillMaxWidth()) {
            Text("Date: $date", modifier = Modifier.padding(16.dp))
        }
        Spacer(Modifier.height(8.dp))
        TextField(value = venue, onValueChange = onV, label = { Text("Venue") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        TextField(
            value = total, 
            onValueChange = { 
                onT(it)
                val t = it.toDoubleOrNull() ?: 0.0
                val p = prompt.toDoubleOrNull() ?: 0.0
                if (t >= p) onD((t - p).toString())
            }, 
            label = { Text(stringResource(R.string.mahr_total)) }, 
            modifier = Modifier.fillMaxWidth(),
            isError = isMismatch,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Row(Modifier.fillMaxWidth()) {
            TextField(
                value = prompt, 
                onValueChange = {
                    onP(it)
                    val p = it.toDoubleOrNull() ?: 0.0
                    val t = total.toDoubleOrNull() ?: 0.0
                    if (t >= p) onD((t - p).toString())
                }, 
                label = { Text(stringResource(R.string.mahr_prompt)) }, 
                modifier = Modifier.weight(1f),
                isError = isMismatch,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(Modifier.width(8.dp))
            TextField(
                value = deferred, 
                onValueChange = onD, 
                label = { Text(stringResource(R.string.mahr_deferred)) }, 
                modifier = Modifier.weight(1f),
                isError = isMismatch,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }
        if (isMismatch) {
            Text(
                stringResource(R.string.error_mahr_mismatch),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        TextField(value = property, onValueChange = onProp, label = { Text("Property details (e.g. Gold/Land)") }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ReviewStep(g: String, b: String, w1: String, w2: String, v: String, vnu: String, total: String, fee: Double) {
    Column {
        Text(stringResource(R.string.step_review), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Groom: $g")
                Text("Bride: $b")
                Text("Witnesses: $w1, $w2")
                if (v.isNotBlank()) Text("Vakil: $v")
                Text("Venue: $vnu")
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text("Mahr Total: $total BDT", style = MaterialTheme.typography.titleMedium)
                Text("${stringResource(R.string.kazi_fee)}: $fee BDT", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
