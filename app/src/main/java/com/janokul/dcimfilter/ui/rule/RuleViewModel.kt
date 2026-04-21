package com.janokul.dcimfilter.ui.rule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.janokul.dcimfilter.RULE_ID
import com.janokul.dcimfilter.room.rule.FilterRuleDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RuleViewModel @Inject constructor(
    private val filterRuleDao: FilterRuleDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val ruleId = savedStateHandle.get<Long>(RULE_ID)

    val filterRule = filterRuleDao.getById(ruleId)
}