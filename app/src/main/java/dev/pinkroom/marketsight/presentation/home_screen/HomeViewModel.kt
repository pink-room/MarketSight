package dev.pinkroom.marketsight.presentation.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pinkroom.marketsight.common.DispatcherProvider
import dev.pinkroom.marketsight.common.Resource
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.AssetFilter
import dev.pinkroom.marketsight.domain.model.assets.TypeAsset
import dev.pinkroom.marketsight.domain.use_case.assets.GetAllAssets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllAssets: GetAllAssets,
    private val dispatchers: DispatcherProvider,
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private var stocksList: List<Asset> = emptyList()
    private var cryptosList: List<Asset> = emptyList()
    private var selectedFilter = uiState.value.filters.find { it.isSelected } ?: uiState.value.filters.first()

    init {
        getAllAssets()
    }

    fun onEvent(event: HomeEvent) {
        when(event){
            is HomeEvent.NewSearchInput -> changeSearchInput(newInput = event.value)
            is HomeEvent.ChangeAssetFilter -> changeAssetFilter(filterToBeSelected = event.assetSelected)
            HomeEvent.RetryToGetAssetList -> retryToGetAssetList()
        }
    }

    private fun changeSearchInput(newInput: String) {
        val newList = selectedFilter.getAssets().filter { it.name.lowercase().startsWith(newInput.lowercase()) || it.symbol.startsWith(newInput.uppercase()) }
        _uiState.update { it.copy(searchInput = newInput, assets = newList, isEmptyOnSearch = newList.isEmpty()) }
    }

    private fun changeAssetFilter(filterToBeSelected: AssetFilter) {
        val newFilters = uiState.value.filters.map {
            if (it == filterToBeSelected) it.copy(isSelected = true)
            else it.copy(isSelected = false)
        }
        selectedFilter = filterToBeSelected
        val assets = filterToBeSelected.getAssets()
        _uiState.update {
            it.copy(
                filters = newFilters,
                placeHolder = filterToBeSelected.placeHolder,
                searchInput = null,
                isEmptyOnSearch = false,
                hasError = assets.isEmpty(),
                assets = assets,
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getAllAssets() {
        viewModelScope.launch(dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            val allStocksAssetsRequest = async { getAllAssets(typeAsset = TypeAsset.Stock) }
            val allCryptoAssetsRequest = async { getAllAssets(typeAsset = TypeAsset.Crypto) }
            awaitAll(allStocksAssetsRequest, allCryptoAssetsRequest)

            stocksList = extractAssetsFromResult(result = allStocksAssetsRequest.getCompleted())
            cryptosList = extractAssetsFromResult(result = allCryptoAssetsRequest.getCompleted())
            val assets = selectedFilter.getAssets()

            _uiState.update { it.copy(isLoading = false, assets = assets, hasError = assets.isEmpty()) }
        }
    }

    private fun extractAssetsFromResult(result: Resource<List<Asset>>): List<Asset> {
        return when(result){
            is Resource.Success -> result.data
            is Resource.Error -> emptyList()
        }
    }

    private fun AssetFilter.getAssets() = when(typeAsset) {
        TypeAsset.Crypto -> cryptosList
        TypeAsset.Stock -> stocksList
    }

    private fun retryToGetAssetList() {
        viewModelScope.launch(dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            val response = getAllAssets(typeAsset = selectedFilter.typeAsset)
            val assets = extractAssetsFromResult(result = response)

            when(selectedFilter.typeAsset) {
                TypeAsset.Crypto -> cryptosList = assets
                TypeAsset.Stock -> stocksList = assets
            }
            _uiState.update { it.copy(isLoading = false, assets = assets, hasError = assets.isEmpty()) }
        }
    }

}