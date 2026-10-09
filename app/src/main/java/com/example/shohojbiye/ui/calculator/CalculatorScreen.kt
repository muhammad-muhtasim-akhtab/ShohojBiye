package com.example.shohojbiye.ui.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    var income by rememberSaveable { mutableStateOf("") }
    var groomEducation by rememberSaveable { mutableIntStateOf(3) }
    var brideEducation by rememberSaveable { mutableIntStateOf(3) }
    var areaType by rememberSaveable { mutableStateOf("URBAN") }
    
    var showGroomEduMenu by remember { mutableStateOf(false) }
    var showBrideEduMenu by remember { mutableStateOf(false) }
    var showAreaMenu by remember { mutableStateOf(false) }

    val educationLevels = listOf("Primary", "Secondary", "Higher Secondary", "Graduate", "Post Graduate")
    val areaTypes = listOf("URBAN", "RURAL")

    val result by viewModel.calculationResult.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Kabin Calculator", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        
        TextField(
            value = income,
            onValueChange = { income = it },
            label = { Text("Groom Monthly Income (BDT)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Groom Education
        ExposedDropdownMenuBox(
            expanded = showGroomEduMenu,
            onExpandedChange = { showGroomEduMenu = !showGroomEduMenu }
        ) {
            TextField(
                value = educationLevels[groomEducation - 1],
                onValueChange = {},
                readOnly = true,
                label = { Text("Groom Education Level") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGroomEduMenu) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = showGroomEduMenu,
                onDismissRequest = { showGroomEduMenu = false }
            ) {
                educationLevels.forEachIndexed { index, level ->
                    DropdownMenuItem(
                        text = { Text(level) },
                        onClick = {
                            groomEducation = index + 1
                            showGroomEduMenu = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Bride Education
        ExposedDropdownMenuBox(
            expanded = showBrideEduMenu,
            onExpandedChange = { showBrideEduMenu = !showBrideEduMenu }
        ) {
            TextField(
                value = educationLevels[brideEducation - 1],
                onValueChange = {},
                readOnly = true,
                label = { Text("Bride Education Level") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBrideEduMenu) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = showBrideEduMenu,
                onDismissRequest = { showBrideEduMenu = false }
            ) {
                educationLevels.forEachIndexed { index, level ->
                    DropdownMenuItem(
                        text = { Text(level) },
                        onClick = {
                            brideEducation = index + 1
                            showBrideEduMenu = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Area Type
        ExposedDropdownMenuBox(
            expanded = showAreaMenu,
            onExpandedChange = { showAreaMenu = !showAreaMenu }
        ) {
            TextField(
                value = areaType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Area Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showAreaMenu) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = showAreaMenu,
                onDismissRequest = { showAreaMenu = false }
            ) {
                areaTypes.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            areaType = type
                            showAreaMenu = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                val amount = income.toDoubleOrNull() ?: 0.0
                viewModel.calculate(amount, groomEducation, brideEducation, areaType)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate")
        }
        
        result?.let {
            Spacer(modifier = Modifier.height(32.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tier: ${it.tier}", style = MaterialTheme.typography.titleMedium)
                    Text("Estimated Kabin: ${String.format(java.util.Locale.US, "%.2f", it.amount)} BDT", style = MaterialTheme.typography.titleLarge)
                    Text(
                        it.disclaimer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
