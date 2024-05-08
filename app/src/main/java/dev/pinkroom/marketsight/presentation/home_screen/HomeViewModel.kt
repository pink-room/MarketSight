package dev.pinkroom.marketsight.presentation.home_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pinkroom.marketsight.domain.model.assets.AssetFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when(event){
            is HomeEvent.NewSearchInput -> changeSearchInput(newInput = event.value)
            is HomeEvent.ChangeAssetFilter -> changeAssetFilter(filterToBeSelected = event.assetSelected)
        }
    }

    private fun changeSearchInput(newInput: String) {
        _uiState.update { it.copy(searchInput = newInput) }
        // TODO UPDATE LIST TO SHOW RESULTS RELATED TO INPUT
    }

    private fun changeAssetFilter(filterToBeSelected: AssetFilter) {
        val newFilters = uiState.value.filters.map {
            if (it == filterToBeSelected) it.copy(isSelected = true)
            else it.copy(isSelected = false)
        }
        _uiState.update { it.copy(filters = newFilters, placeHolder = filterToBeSelected.placeHolder) }
    }
}