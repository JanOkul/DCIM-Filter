package com.janokul.dcimfilter.ui.rule.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.janokul.dcimfilter.room.rule.Condition
import com.janokul.dcimfilter.room.rule.types.RuleKeys

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewConditionSheet(dismissModal: (Condition) -> Unit) { //todo make this a general new/edit sheet rather than a new sheet
    //todo add all 3 values here as mutable states


    ModalBottomSheet(
        onDismissRequest = { dismissModal } //todo in here using 3 states construct a Condition and pass it into the callback
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NewKeyField()
            NewOpField()
            NewValueField()
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewKeyField(/* todo add remember mutable state argument in here*/) {
    val keyOptions = RuleKeys.entries.toTypedArray() //todo change this to just options
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(keyOptions[0].value) } //todo in the keys file add a filter nothing and make that default

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {

        TextField(
            modifier = Modifier.menuAnchor(), //todo fix this deprecated call with the version that has arguments
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text("Attribute") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            keyOptions.forEach { selectionOption ->
                // todo Maybe make a display value in rule keys
                DropdownMenuItem(
                    text = { Text(selectionOption.value) },
                    onClick = {
                        selectedOptionText = selectionOption.value
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewOpField(/* todo add remember mutable state argument in here*/) {
    val options = listOf("Option 1", "Option 2", "Option 3", "Option 4")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {

        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text("Operand") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
private fun NewValueField(/* todo add remember mutable state argument in here*/) {
    //todo number or integer field depending on what column is picked, need new attribute in RuleKeys to be able to do this RuleKeys(column, intorstring)
}