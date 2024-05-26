package com.focalstudio.focalhub.view.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focalstudio.focalhub.data.DisplayRuleRepository
import com.focalstudio.focalhub.data.model.DisplayRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RulesManagerViewModel(application: Application, private val ruleRepository: DisplayRuleRepository) : AndroidViewModel(application) {

    private val _rules = MutableStateFlow<List<DisplayRule>>(emptyList())
    val rules: StateFlow<List<DisplayRule>> get() = _rules

    init {
        loadRules()
    }

    private fun loadRules() {
        viewModelScope.launch {
            _rules.value = ruleRepository.getRules() // Assuming getRules() returns a list of rules
        }
    }
}
