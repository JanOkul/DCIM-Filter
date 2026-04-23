package com.janokul.dcimfilter.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.janokul.dcimfilter.NavNames
import com.janokul.dcimfilter.room.rule.FilterRule
import com.janokul.dcimfilter.room.rule.FilterRuleDao
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor (
    val filterRuleDao: FilterRuleDao
): ViewModel() {

    val rules = filterRuleDao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createNewRule(navController: NavController) {
        viewModelScope.launch {
            val id = newRuleBlank()
            navController.navigate("${NavNames.RULE.id}/${id}?isNew=true")
        }
    }

    // A blank entry that's created so that the page is navigable by the nav controller
    private suspend fun newRuleBlank(): Long {
        return filterRuleDao.insert(FilterRule.empty())
    }

    // Flips the enabled state of a FilterRule
    fun changeEnabledState(id: Long) {
        viewModelScope.launch {
            val rule = filterRuleDao.getById(id).firstOrNull() ?: return@launch
            filterRuleDao.update(rule.copy(enabled = !rule.enabled))
        }
    }
}