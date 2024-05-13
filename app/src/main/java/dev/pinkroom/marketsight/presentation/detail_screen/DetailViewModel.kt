package dev.pinkroom.marketsight.presentation.detail_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.di.DetailScreenArgModule.SymbolId
import dev.pinkroom.marketsight.domain.use_case.assets.GetAssetById
import dev.pinkroom.marketsight.domain.use_case.market.GetBarsAsset
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    @SymbolId private val symbolIdArg: String?,
    private val dispatchers: DispatcherProvider,
    private val getAssetById: GetAssetById,
    private val getBarsAsset: GetBarsAsset,
): ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _action = Channel<DetailAction>()
    val action = _action.receiveAsFlow()

    private lateinit var symbolId: String

    init {
        validateArgs()
    }

    private fun validateArgs() {
        viewModelScope.launch(dispatchers.Default) {
            if (symbolIdArg == null) _action.send(DetailAction.NavigateToHomeEmptyId)
            else {
                symbolId = symbolIdArg
                initData()
            }
        }
    }

    private fun initData() {
        viewModelScope.launch(dispatchers.IO) {
            when(val response = getAssetById(id = symbolId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            statusMainInfo = it.statusMainInfo.copy(isLoading = false, errorMessage = null),
                            asset = response.data
                        )
                    }
                    getHistoricalBarsInfo()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(statusMainInfo = it.statusMainInfo.copy(isLoading = false, errorMessage = R.string.error_on_getting_assets)) }
                }
            }
        }
    }

    private fun getHistoricalBarsInfo() {
        viewModelScope.launch(dispatchers.IO) {
            val asset = uiState.value.asset
            val selectedFilter = uiState.value.selectedFilter
            val response = getBarsAsset(
                symbol = asset.symbol,
                typeAsset = asset.getTypeAsset(),
                startDate = selectedFilter.getStarLocalDateTime(),
                endDate = selectedFilter.getEndLocalDateTime(),
            )
            when(response) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            statusHistoricalBars = it.statusHistoricalBars.copy(isLoading = false, errorMessage = null),
                            bars = response.data,
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(statusHistoricalBars = it.statusHistoricalBars.copy(isLoading = false, errorMessage = R.string.error_on_getting_bars)) }
                }
            }
        }
    }


}