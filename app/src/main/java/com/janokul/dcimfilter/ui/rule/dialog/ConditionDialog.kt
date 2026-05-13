package com.janokul.dcimfilter.ui.rule.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.janokul.dcimfilter.room.rule.Condition
import com.janokul.dcimfilter.room.rule.types.ConditionAttribute
import com.janokul.dcimfilter.room.rule.types.ConditionValue.StringValue
import com.janokul.dcimfilter.ui.rule.dialog.fields.AppPicker
import com.janokul.dcimfilter.ui.rule.dialog.fields.AttributeField
import com.janokul.dcimfilter.ui.rule.dialog.fields.OpField
import com.janokul.dcimfilter.ui.rule.dialog.fields.ValueField

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
    var acceptEnabled by remember { mutableStateOf(true) }

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
            onCanSave = { acceptEnabled = it }
        )

        AcceptOrDismiss(
            onDismiss = dismissModal,
            acceptEnabled = acceptEnabled
        ) {
            saveCondition(selectedCondition)
            dismissModal()
        }
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

