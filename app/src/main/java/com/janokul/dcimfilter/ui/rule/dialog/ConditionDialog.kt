package com.janokul.dcimfilter.ui.rule.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.janokul.dcimfilter.room.rule.*
import com.janokul.dcimfilter.room.rule.types.ConditionAttribute
import com.janokul.dcimfilter.room.rule.types.ConditionValue.*

import com.janokul.dcimfilter.ui.rule.dialog.fields.*

/**
 * Creates the selected condition when the select box first loads.
 */
private fun createDefaultCondition(): Condition {
    val attribute = ConditionAttribute.FILTER_NONE
    return Condition(
            attribute,
            attribute.valueType.defaultOp,
            attribute.valueType
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionDialog(
    condition: Condition?,
    dismissModal: () -> Unit,
    saveCondition: (Condition) -> Unit,
    autoSetToPath: (String) -> Unit
) {
    var showAppPickerSheet by remember { mutableStateOf(false) }
    var selectedCondition by remember { mutableStateOf(condition ?: createDefaultCondition()) }

    Dialog(
        onDismissRequest = { dismissModal() }
    ) {
        Card{
            if (showAppPickerSheet) {
                AppPicker(
                    closePicker = { showAppPickerSheet = false },
                    onSelectPackage = { packageName, packageLabel ->
                        selectedCondition = selectedCondition.copy(
                            value = StringValue.PackageValue(value = packageName)
                        )
                        autoSetToPath(packageLabel)
                    }
                )
            } else {
                ConditionForm(
                    selectedCondition = selectedCondition,
                    onConditionChange = { selectedCondition = it },
                    onPickerChange = { showAppPickerSheet = it },
                    saveCondition = saveCondition,
                    dismissModal = dismissModal
                )
            }
        }
    }
}

@Composable
private fun ConditionForm(
    selectedCondition: Condition,
    onConditionChange: (Condition) -> Unit,
    onPickerChange: (Boolean) -> Unit,
    saveCondition: (Condition) -> Unit,
    dismissModal: () -> Unit

) {
    var acceptEnabled = when (val value = selectedCondition.value) {
        is LongValue -> value.value.isNotEmpty() && value.value.toLongOrNull() != null
        is BoolValue -> false
        is StringValue.RawStringValue -> value.value.any { !it.isWhitespace() }
        is StringValue.PackageValue -> value.value.isNotEmpty()
        is StringValue.DateValue -> value.value.isNotEmpty() //todo fill in rest
        is SpecialValue.NoneValue -> false
        is SpecialValue.AllValue -> false
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Edit Condition", style = MaterialTheme.typography.titleLarge)
        AttributeField(
            selectedAttribute = selectedCondition.attribute
        ) {
            onConditionChange(
                selectedCondition.copy(
                    attribute = it,
                    op = it.valueType.defaultOp,
                    value = it.valueType
                )
            )
        }

        OpField(
            selectedOp = selectedCondition.op,
            options = selectedCondition.attribute.valueType.validOps
        ) {
            onConditionChange(
                selectedCondition.copy(op = it)
            )
        }

        ValueField(
            selectedValue = selectedCondition.value,
            setAppPickerState = { onPickerChange(it) },
            onSelect = {
                onConditionChange(
                    selectedCondition.copy(value = it)
                )
            },
            canSave = acceptEnabled,
            onCanSave = { acceptEnabled = it }
        )

        AcceptOrDismiss(
            acceptEnabled = acceptEnabled,
            onDismiss = dismissModal
        ) {
            saveCondition(selectedCondition)
            dismissModal()
        }
    }
}

@Composable
private fun AcceptOrDismiss(acceptEnabled: Boolean, onDismiss: () -> Unit, onAccept: () -> Unit) {
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

