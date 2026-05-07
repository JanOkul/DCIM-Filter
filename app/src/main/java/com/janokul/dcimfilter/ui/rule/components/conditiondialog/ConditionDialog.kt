package com.janokul.dcimfilter.ui.rule.components.conditiondialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.janokul.dcimfilter.room.rule.Condition
import com.janokul.dcimfilter.room.rule.types.ConditionAttribute
import com.janokul.dcimfilter.room.rule.types.ConditionOp
import com.janokul.dcimfilter.room.rule.types.ConditionValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.BoolValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.LongValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.StringValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionDialog(
    condition: Condition?,
    dismissModal: () -> Unit,
    saveCondition: (Condition) -> Unit,
    autoSetToPath: (String) -> Unit
) {
    var showAppPickerSheet by remember { mutableStateOf(false) }
    var selectedAttribute by remember { mutableStateOf(condition?.attribute ?: ConditionAttribute.FILTER_NONE) }
    var selectedOp by remember(selectedAttribute) { mutableStateOf(condition?.op ?: selectedAttribute.valueType.defaultOp) }
    var selectedValue by remember(selectedAttribute) { mutableStateOf(condition?.value ?: selectedAttribute.valueType) }
    val dialogTitle = if (condition == null) "Create New Condition" else "Edit Condition"

    val dialogContent = @Composable {
        AttributeField(selectedAttribute) { selectedAttribute = it }
        OpField(selectedOp, selectedAttribute.valueType.validOps) { selectedOp = it }
        ValueField(
            selectedValue,
            { showAppPickerSheet = it },
            { selectedValue = it }
        )
        AcceptOrDismiss(dismissModal, selectedAttribute.valueType !is BoolValue) {
            saveCondition(Condition(selectedAttribute, selectedOp, selectedValue))
            dismissModal()
        }
    }

    Dialog(
        onDismissRequest = { dismissModal() }
    ) {
        Card{
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (showAppPickerSheet) {
                    AppPicker(closePicker = { showAppPickerSheet = false }) { packageName, packageLabel ->
                        selectedValue = StringValue.PackageValue(value = packageName)
                        autoSetToPath(packageLabel)
                    }
                } else {
                    Text(dialogTitle, style = MaterialTheme.typography.titleLarge)
                    dialogContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttributeField(selectedAttribute: ConditionAttribute, onSelect: (ConditionAttribute) -> Unit) {
    val options = ConditionAttribute.entries.toTypedArray()
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {

        TextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
            readOnly = true,
            value = selectedAttribute.displayName,
            onValueChange = {},
            label = { Text("Attribute") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption.displayName) },
                    onClick = {
                        onSelect(selectionOption)
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
private fun OpField(
    selectedOp: ConditionOp,
    options: List<ConditionOp>,
    onSelect: (ConditionOp) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    if (options == BoolValue().validOps) {
        return
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {

        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = selectedOp.op,
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
                    text = { Text(selectionOption.op) },
                    onClick = {
                        onSelect(selectionOption)
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
private fun ValueField(
    selectedValue: ConditionValue<*>,
    setAppPickerState: (Boolean) -> Unit,
    onSelect: (ConditionValue<*>) -> Unit) {

    when (selectedValue) {
        is StringValue -> StringValueField(selectedValue, setAppPickerState, onSelect)

        is LongValue -> TextField(
            value = selectedValue.value.toString(),
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { it.toLongOrNull()?.let { num -> onSelect(LongValue(num)) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Number Value") }
        )
        is BoolValue -> Unit
    }
}

@Composable
private fun StringValueField(
    selectedStringValue: StringValue,
    setAppPickerState: (Boolean) -> Unit,
    onSelect: (ConditionValue<*>) -> Unit
) {
    when (selectedStringValue) {
        is StringValue.RawStringValue -> TextField(
            value = selectedStringValue.value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { value: String -> onSelect(StringValue.RawStringValue(value)) },
            label = { Text("Text Value") }
        )

        is StringValue.PackageValue -> {
            TextField(
                value = selectedStringValue.value,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { value: String -> onSelect(StringValue.RawStringValue(value)) },
                label = { Text("Selected Package Name") },
                readOnly = true
            )

            Button(onClick = { setAppPickerState(true) }) {
                Text("Select A Package")
            }
        }
        is StringValue.DateValue -> {}
    }
}

@Composable
private fun AcceptOrDismiss(onDismiss: () -> Unit, acceptEnabled: Boolean, onAccept: () -> Unit) {
    Row{
        TextButton(onClick = onDismiss) {
            Text("Discard", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.weight(1f))

        TextButton(onClick = onAccept, enabled = acceptEnabled) {
            Text("Save", style = MaterialTheme.typography.titleMedium)
        }
    }
}

