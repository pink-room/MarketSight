package dev.pinkroom.marketsight.presentation.core.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import dev.pinkroom.marketsight.R
import dev.pinkroom.marketsight.common.toEpochMillis
import dev.pinkroom.marketsight.presentation.core.theme.BabyBlue
import dev.pinkroom.marketsight.presentation.core.theme.Blue
import dev.pinkroom.marketsight.presentation.core.theme.dimens
import java.time.LocalDate

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    modifier: Modifier = Modifier,
    initialDate: LocalDate? = null,
    isToShowDatePicker: Boolean,
    saveNewDate: (date: Long?) -> Unit,
    dismissDatePicker: () -> Unit,
    selectableDates: SelectableDates,
){
    val locale = LocalConfiguration.current.locales[0]

    val state = DatePickerState(
        locale = locale,
        initialSelectedDateMillis = initialDate?.toEpochMillis(),
        selectableDates = selectableDates,
    )

    val colorCurrentDay = if (selectableDates.isSelectableDate(System.currentTimeMillis()))
        MaterialTheme.colorScheme.onBackground
    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)

    if (isToShowDatePicker)
        DatePickerDialog(
            modifier = modifier,
            onDismissRequest = {
                state.selectedDateMillis = initialDate?.toEpochMillis()
                dismissDatePicker()
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimens.smallPadding),
                ) {
                    AnimatedVisibility(visible = state.selectedDateMillis != null) {
                        Button(
                            onClick = {
                                state.selectedDateMillis = null
                                saveNewDate(null)
                                dismissDatePicker()
                            },
                        ) {
                            Text(text = stringResource(id = R.string.clear_date))
                        }
                    }
                    Button(
                        onClick = {
                            state.selectedDateMillis?.let { saveNewDate(it) }
                            dismissDatePicker()
                        },
                    ) {
                        Text(text = stringResource(id = R.string.select))
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        state.selectedDateMillis = initialDate?.toEpochMillis()
                        dismissDatePicker()
                    },
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
        ) {
            DatePicker(
                state = state,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = BabyBlue.copy(alpha = 0.3f),
                    selectedDayContentColor = Blue,
                    todayContentColor = colorCurrentDay,
                    todayDateBorderColor = MaterialTheme.colorScheme.onBackground,
                    currentYearContentColor = MaterialTheme.colorScheme.onBackground,
                    dateTextFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                        selectionColors = TextSelectionColors(
                            handleColor = MaterialTheme.colorScheme.onBackground,
                            backgroundColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    )
                )
            )
        }
}