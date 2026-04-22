package com.janokul.dcimfilter.ui.rule.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.janokul.dcimfilter.R
import com.janokul.dcimfilter.ui.rule.RuleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(viewModel: RuleViewModel, navController: NavController) {
    val isNew = viewModel.isNew
    val title = if (isNew) "New Rule" else "Rule - ${viewModel.ruleId}"

    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (isNew)
                IconButton(onClick = {
                    viewModel.deleteCurrentRule()
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.secondary_content_description)
                    )
                }
            else
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.secondary_content_description)
                    )
                }
        },
        actions = {
            if (isNew)
                IconButton(onClick = {
                    //todo add part that saves it
                    viewModel.isNew = false
                    navController.popBackStack()
                }) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Create Rule",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            else
                IconButton(onClick = { viewModel.deleteCurrentRule(); navController.popBackStack() }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete Rule",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
        }
    )
}