package dev.pinkroom.marketsight.presentation.home_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import dev.pinkroom.marketsight.domain.model.assets.AssetFilter
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import dev.pinkroom.marketsight.presentation.home_screen.components.FilterAssets
import dev.pinkroom.marketsight.presentation.home_screen.components.SearchInput

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    searchInput: String?,
    placeHolder: Int,
    filters: List<AssetFilter>,
    onEvent: (HomeEvent) -> Unit,
){
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    fun closeKeyboardAndClearFocus() {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    LazyColumn(
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
    ) {
        stickyHeader {
            SearchInput(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .padding(horizontal = dimens.horizontalPadding),
                value = searchInput ?: "",
                placeHolder = stringResource(id = placeHolder),
                isLoading = isLoading,
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
                    onEvent(HomeEvent.ChangeAssetFilter(assetSelected = it))
                },
            )
        }
        item {

        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
)
@Composable
fun HomeScreenPreview(){
    HomeScreen(
        isLoading = true,
        searchInput = null,
        placeHolder = R.string.place_holder_stock,
        filters = assetFilters,
        onEvent = {},
    )
}