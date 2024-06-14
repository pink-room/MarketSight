package dev.pinkroom.marketsight.presentation.home_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.assetFilters
import dev.pinkroom.marketsight.domain.model.assets.Asset
import dev.pinkroom.marketsight.domain.model.assets.AssetFilter
import dev.pinkroom.marketsight.presentation.core.components.PullToRefreshLazyColumn
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.home_screen.components.FilterAssets
import dev.pinkroom.marketsight.presentation.home_screen.components.ListAssets
import dev.pinkroom.marketsight.presentation.home_screen.components.SearchInput

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    searchInput: String?,
    placeHolder: Int,
    filters: List<AssetFilter>,
    assets: List<Asset>,
    isEmptyOnSearch: Boolean,
    hasError: Boolean,
    isRefreshing: Boolean,
    onEvent: (HomeEvent) -> Unit,
    navigateToAssetDetailScreen: (Asset) -> Unit,
){
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun closeKeyboardAndClearFocus() {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    PullToRefreshLazyColumn(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    closeKeyboardAndClearFocus()
                }
            )
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = dimens.contentTopPadding,
            bottom = dimens.contentBottomPadding,
        ),
        onRefresh = { onEvent(HomeEvent.Refresh) },
        isRefreshing = isRefreshing,
        enabledPullToRefresh = {
            !isLoading && !isRefreshing
        }
    ) {
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                    ),
            ) {
                SearchInput(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .padding(horizontal = dimens.horizontalPadding),
                    value = searchInput ?: "",
                    placeHolder = stringResource(id = placeHolder),
                    isLoading = isLoading,
                    isEnabled = !hasError,
                    onChangeInput = {
                        onEvent(HomeEvent.NewSearchInput(value = it))
                    },
                    closeInput = {
                        closeKeyboardAndClearFocus()
                    }
                )
                Spacer(modifier = Modifier.height(dimens.smallPadding))
                FilterAssets(
                    modifier = Modifier
                        .fillMaxWidth(),
                    filters = filters,
                    isLoading = isLoading,
                    onFilterClick = {
                        closeKeyboardAndClearFocus()
                        onEvent(HomeEvent.ChangeAssetFilter(assetSelected = it))
                    },
                )
            }
        }
        ListAssets(
            modifier = Modifier
                .fillMaxSize(),
            assets = assets,
            isLoading = isLoading,
            isEmptyOnSearch = isEmptyOnSearch,
            hasError = hasError,
            onAssetClick = navigateToAssetDetailScreen,
            onRetry = {
                onEvent(HomeEvent.RetryToGetAssetList)
            }
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun HomeScreenPreview(){
    HomeScreen(
        isLoading = false,
        assets = emptyList(),
        searchInput = null,
        placeHolder = R.string.place_holder_stock,
        filters = assetFilters,
        isEmptyOnSearch = false,
        hasError = true,
        isRefreshing = false,
        onEvent = {},
        navigateToAssetDetailScreen = {},
    )
}