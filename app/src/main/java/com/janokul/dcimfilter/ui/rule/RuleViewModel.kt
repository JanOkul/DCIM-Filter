package com.janokul.dcimfilter.ui.rule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.janokul.dcimfilter.room.rule.FilterRule
import com.janokul.dcimfilter.room.rule.FilterRuleDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RuleViewModel @Inject constructor(
    private val filterRuleDao: FilterRuleDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val ruleId = checkNotNull(savedStateHandle.get<Long>("ruleId"))
    var isNew = checkNotNull(savedStateHandle.get<Boolean>("isNew"))

    val currentRule = filterRuleDao.getById(ruleId)

    fun updateCurrentRule(rule: FilterRule) {
        viewModelScope.launch {
            filterRuleDao.delete(rule)
        }
    }

    fun deleteCurrentRule(scope: CoroutineScope = viewModelScope) {
        scope.launch {
            val rule = currentRule.firstOrNull() ?: return@launch
            filterRuleDao.delete(rule)
        }
    }

    override fun onCleared() {
        // If app closed etc, will make sure empty entry isnt made.
        if (isNew) {
            deleteCurrentRule(CoroutineScope(Dispatchers.IO + NonCancellable))
        }
        super.onCleared()
    }
}