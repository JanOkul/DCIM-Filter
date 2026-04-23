package com.janokul.dcimfilter.ui.rule.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

/**
 * A Card that can be collapsed and expanded
 * @param title A title for the card
 * @param collapsedContent Content that will be shown in the card's collapsed state
 * @param expandedContent Content that will be shown in the card's expanded state
 * @param initiallyCollapsed If the card should initially be collapsed or expanded, default true
 */
@Composable
fun CollapsibleCard(
    title: @Composable () -> Unit,
    collapsedContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit,
    initiallyCollapsed: Boolean = true
) {
    var isCollapsed by remember { mutableStateOf(initiallyCollapsed) }
    val iconRotation by animateFloatAsState(if (isCollapsed) 0f else 180f)

    Card(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth(),
        onClick = { isCollapsed = !isCollapsed }
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ){
                title()
                Icon(
                    Icons.Filled.ExpandMore,
                    modifier = Modifier.rotate(iconRotation),
                    contentDescription = "Icon that indicates if the card is expanded or collapsed."
                )
            }

            if (isCollapsed) {
                collapsedContent()
            } else {
                expandedContent()
            }
        }
    }
}