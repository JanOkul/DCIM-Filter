package com.janokul.dcimfilter.ui.rule.dialog.fields

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.janokul.dcimfilter.room.rule.types.ConditionValue
import com.janokul.dcimfilter.room.rule.types.ConditionValue.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValueField(
    selectedValue: ConditionValue<*>,
    setAppPickerState: (Boolean) -> Unit,
    onSelect: (ConditionValue<*>) -> Unit,
    canSave: Boolean,
    onCanSave: (Boolean) -> Unit
) {
    LaunchedEffect(selectedValue) {
        onCanSave(true)
    }

    when (selectedValue) {
        is StringValue -> StringValueField(selectedValue, setAppPickerState, onSelect)
        is SpecialValue -> SpecialValueField(selectedValue, onSelect)
        is LongValue -> LongValueField(selectedValue, onSelect)
        is BoolValue -> BoolValueField(selectedValue, onSelect)
    }
}

@Composable
fun LongValueField(selectedValue: LongValue, onSelect: (ConditionValue<*>) -> Unit) {
    TextField(
        value = selectedValue.value,
        onValueChange = { value: String -> onSelect(LongValue(value)) },
        isError = selectedValue.value.isEmpty() || selectedValue.value.toLongOrNull() == null,
        supportingText = {
            if (selectedValue.value.isEmpty()) {
                Text("Please enter a number")
            } else if (selectedValue.value.toLongOrNull() == null) {
                Text("Not a valid number (Characters, spaces, too large etc...)")
            }
        },
        label = { Text("Number Value") },
        singleLine = true
    )
}

@Composable
fun BoolValueField(selectedValue: BoolValue, onSelect: (ConditionValue<*>) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = selectedValue.value,
            onCheckedChange = { onSelect(BoolValue(it)) }
        )
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
            isError = selectedStringValue.value.none { !it.isWhitespace() },
            supportingText = {
                if (selectedStringValue.value.none { !it.isWhitespace() }) {
                    Text("Must contain more than one character that isn't a space.")
                }
            },
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
    onSelect: (ConditionValue<*>) -> Unit
) {
    when (selectedValue) {
        is SpecialValue.AllValue -> Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = selectedValue.permitted,
                onCheckedChange = { onSelect(SpecialValue.AllValue(permitted = it))  }
            )
            Text("I understand that this option will unconditionally move EVERY media within this folder, regardless of any other conditions present.")
        }

        is SpecialValue.NoneValue -> Unit
    }
}