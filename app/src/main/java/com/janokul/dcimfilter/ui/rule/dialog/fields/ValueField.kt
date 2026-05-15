package com.janokul.dcimfilter.ui.rule.dialog.fields

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.janokul.dcimfilter.room.rule.types.ConditionValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.BoolValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.LongValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.SpecialValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.StringValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValueField(
    selectedValue: ConditionValue<*>,
    setAppPickerState: (Boolean) -> Unit,
    onSelect: (ConditionValue<*>) -> Unit,
    onCanSave: (Boolean) -> Unit
) {
    var canSave by remember { mutableStateOf(true) }
    LaunchedEffect(canSave) {
        onCanSave(canSave)
    }



    when (selectedValue) {
        is StringValue -> StringValueField(selectedValue, setAppPickerState, onSelect)
        is SpecialValue -> SpecialValueField(selectedValue, canSave, { canSave = it })

        is LongValue -> {
            var text by remember(selectedValue) { mutableStateOf(selectedValue.value.toString()) }

            TextField(
                value = text,
                onValueChange = { input ->
                    text = input
                    input.toLongOrNull()?.let { onSelect(LongValue(it)) }
                },
                isError = text.isNotEmpty() && text.toLongOrNull() == null,
                supportingText = {
                    if (text.isNotEmpty() && text.toLongOrNull() == null) {
                        Text("Must be a valid number")
                    }
                },
                label = { Text("Number Value") },
                singleLine = true
            )
        }
        is BoolValue -> {
            val message = if (selectedValue.value) "will unconditionally move EVERY media within this folder" else "will not have any effect on the media within this folder"

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = canSave,
                    onCheckedChange = { canSave = it }
                )
                Text("I understand that this option $message, regardless of any other conditions present.")
            }
        }

    }
}

@Composable
fun StringValueField(
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
fun SpecialValueField(
    selectedValue: SpecialValue,
    canSave: Boolean,
    onCanSave: (Boolean) -> Unit
) {
    when (selectedValue) {
        is SpecialValue.AllValue -> Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = canSave,
                onCheckedChange = { onCanSave(it) }
            )
            Text("I understand that this option will unconditionally move EVERY media within this folder, regardless of any other conditions present.")
        }

        is SpecialValue.NoneValue -> Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = canSave,
            onCheckedChange = { onCanSave(it) }
        )
        Text("I understand that this option will not have any effect on the media within this folder, regardless of any other conditions present.")
    }
    }
}