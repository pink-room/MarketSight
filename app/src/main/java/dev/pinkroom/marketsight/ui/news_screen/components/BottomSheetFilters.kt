package dev.pinkroom.marketsight.ui.news_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.DateMomentType
import dev.pinkroom.marketsight.common.SortType
import dev.pinkroom.marketsight.common.toReadableDate
import dev.pinkroom.marketsight.domain.model.common.SubInfoSymbols
import dev.pinkroom.marketsight.ui.core.components.ButtonFilter
import dev.pinkroom.marketsight.ui.core.components.DatePickerComponent
import dev.pinkroom.marketsight.ui.core.components.DefaultSectionFilter
import dev.pinkroom.marketsight.ui.core.theme.dimens
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetFilters(
    modifier: Modifier = Modifier,
    sortFilters: List<SortType>,
    selectedSort: SortType,
    symbols: List<SubInfoSymbols>,
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSortClick: (sort: SortType) -> Unit,
    onSymbolClick: (symbol: SubInfoSymbols) -> Unit,
    changeDate: (dateInMillis: Long?, dateMomentType: DateMomentType) -> Unit,
    startSelectableDates: SelectableDates,
    endSelectableDates: SelectableDates,
    sheetState: SheetState,
){
    AnimatedVisibility(visible = isVisible) {
        ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = onDismiss,
            sheetState = sheetState,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(dimens.smallPadding),
                contentPadding = PaddingValues(
                    bottom = dimens.normalPadding,
                )
            ) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimens.horizontalPadding),
                        text = stringResource(id = R.string.filters_news),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                item {
                    DateRangePickerSection(
                        startDate = startDate,
                        endDate = endDate,
                        changeDate = changeDate,
                        startSelectableDates = startSelectableDates,
                        endSelectableDates = endSelectableDates,
                    )
                }
                item {
                    SortSection(
                        modifier = Modifier
                            .fillMaxWidth(),
                        itemsSort = sortFilters,
                        selectedSort = selectedSort,
                        onSortClick = onSortClick,
                    )
                }
                item {
                    SymbolsSubscribedSection(
                        modifier = Modifier
                            .fillMaxWidth(),
                        symbols = symbols,
                        onSymbolClick = onSymbolClick,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SortSection(
    modifier: Modifier,
    itemsSort: List<SortType>,
    selectedSort: SortType,
    onSortClick: (sort: SortType) -> Unit,
){
    DefaultSectionFilter(
        title = stringResource(id = R.string.filter_sort),
    ){
        FlowRow(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.horizontalPadding, vertical = dimens.xSmallPadding),
            horizontalArrangement = Arrangement.spacedBy(dimens.smallPadding),
        ) {
            itemsSort.forEach { sort ->
                ButtonFilter(
                    isSelected = sort == selectedSort,
                    text = stringResource(id = sort.stringId),
                    onClick = {
                        onSortClick(sort)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SymbolsSubscribedSection(
    modifier: Modifier,
    symbols: List<SubInfoSymbols>,
    onSymbolClick: (symbol: SubInfoSymbols) -> Unit,
){
    DefaultSectionFilter(
        title = stringResource(id = R.string.filter_symbols),
        showBottomDivider = true,
    ){
        FlowRow(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.horizontalPadding, vertical = dimens.xSmallPadding),
            horizontalArrangement = Arrangement.spacedBy(dimens.smallPadding),
        ) {
            symbols.forEach { symbol ->
                ButtonFilter(
                    isSelected = symbol.isSubscribed,
                    text = symbol.name,
                    onClick = {
                        onSymbolClick(symbol)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerSection(
    modifier: Modifier = Modifier,
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    startSelectableDates: SelectableDates,
    endSelectableDates: SelectableDates,
    changeDate: (dateInMillis: Long?, dateMomentType: DateMomentType) -> Unit,
){
    var isToShowStartDatePicker by rememberSaveable {
        mutableStateOf(false)
    }
    var isToShowEndDatePicker by rememberSaveable {
        mutableStateOf(false)
    }

    val startDateText = if (startDate != null) stringResource(id = R.string.start_date) + ": ${startDate.toReadableDate()}"
    else stringResource(id = R.string.start_date)

    val endDateText = if (endDate != null) stringResource(id = R.string.end_date) + ": ${endDate.toReadableDate()}"
    else stringResource(id = R.string.end_date)

    DefaultSectionFilter(
        title = stringResource(id = R.string.date_range),
    ){
        FlowRow(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.horizontalPadding, vertical = dimens.xSmallPadding),
            horizontalArrangement = Arrangement.spacedBy(dimens.smallPadding),
        ) {
            ButtonFilter(
                isSelected = false,
                text = startDateText,
                onClick = {
                    isToShowStartDatePicker = true
                }
            )
            ButtonFilter(
                isSelected = false,
                text = endDateText,
                onClick = {
                    isToShowEndDatePicker = true
                }
            )
        }
    }

    DatePickerComponent(
        initialDate = startDate,
        isToShowDatePicker = isToShowStartDatePicker,
        saveNewDate = {
            changeDate(it,DateMomentType.Start)
        },
        dismissDatePicker = {
            isToShowStartDatePicker = false
        },
        selectableDates = startSelectableDates,
    )
    DatePickerComponent(
        initialDate = endDate,
        isToShowDatePicker = isToShowEndDatePicker,
        saveNewDate = {
            changeDate(it,DateMomentType.End)
        },
        dismissDatePicker = {
            isToShowEndDatePicker = false
        },
        selectableDates = endSelectableDates,
    )
}

