package com.janokul.dcimfilter.ui.rule.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.janokul.dcimfilter.room.rule.Rule

/**
 * Ui to allow user to configure rules for media whithin the fromPath
 */
@Composable
fun RuleSelection(rules: List<Rule>) {
    Card {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Rule Selection", style = MaterialTheme.typography.titleLarge)
            Text(
                "Each rule can be customised such that a file is only eligible for filtering if it all rules entered below evaluate to true for a file.",
                style = MaterialTheme.typography.bodyMedium
            )
            Column(
                Modifier.Companion.verticalScroll(rememberScrollState())
            ) {

            }

        }
    }
}