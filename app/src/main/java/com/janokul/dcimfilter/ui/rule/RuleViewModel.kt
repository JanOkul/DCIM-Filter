package com.janokul.dcimfilter.ui.rule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janokul.dcimfilter.room.rule.Condition
import com.janokul.dcimfilter.room.rule.FilterRule
import com.janokul.dcimfilter.room.rule.FilterRuleDao
import com.janokul.dcimfilter.room.rule.types.RuleKeys
import com.janokul.dcimfilter.room.rule.types.RuleOps
import com.janokul.dcimfilter.room.rule.types.RuleValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RuleViewModel @Inject constructor(
    private val filterRuleDao: FilterRuleDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val ruleId = checkNotNull(savedStateHandle.get<Long>("ruleId"))
    var isNew by mutableStateOf(checkNotNull(savedStateHandle.get<Boolean>("isNew")))
    var isDirty by mutableStateOf(isNew)
    var currentFilterRule by mutableStateOf(FilterRule.empty())
        private set

    init {
        viewModelScope.launch {
            currentFilterRule = filterRuleDao.getById(ruleId).firstOrNull() ?: FilterRule.empty()
        }
    }

    // Updates viewmodel copy of the filter rule
    fun updateCurrentRule(newFilterRule: FilterRule) {
        if (!isDirty) {
            isDirty = true
        }
        currentFilterRule = newFilterRule
    }

    fun addCondition(key: RuleKeys, op: RuleOps, value: RuleValue) {
        val condition = Condition(key, op, value)
        currentFilterRule = currentFilterRule.copy(conditions = currentFilterRule.conditions + condition)
    }

    // Syncs changes to Room
    fun saveCurrentRule() {
        viewModelScope.launch {
            filterRuleDao.update(currentFilterRule)
            isDirty = false
            isNew = false
        }
    }

    fun deleteCurrentRule(scope: CoroutineScope = viewModelScope) {
        scope.launch {
            filterRuleDao.delete(currentFilterRule)
        }
    }

    // If app closed without making a save, this override will make sure empty rule isn't saved.
    override fun onCleared() {
        if (isNew) {
            deleteCurrentRule(CoroutineScope(Dispatchers.IO + NonCancellable))
        }
        super.onCleared()
    }
}