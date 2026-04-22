package com.janokul.dcimfilter.ui.rule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.janokul.dcimfilter.ui.rule.components.AppBar
import com.janokul.dcimfilter.ui.rule.components.PathSelection
import com.janokul.dcimfilter.ui.rule.components.RuleSelection


private const val TAG = "RuleScreen"
@Composable
fun RuleScreen(navController: NavController, viewModel: RuleViewModel = hiltViewModel()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppBar(viewModel, navController) }
    ) { innerPadding ->
        RuleBody(innerPadding)
    }
}

@Composable
fun RuleBody(innerPadding: PaddingValues) {
    Column(
        Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PathSelection()
        RuleSelection(emptyList())
    }
}

