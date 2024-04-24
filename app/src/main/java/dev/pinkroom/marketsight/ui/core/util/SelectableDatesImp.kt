package dev.pinkroom.marketsight.ui.core.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import dev.pinkroom.marketsight.common.DateMomentType
import dev.pinkroom.marketsight.common.toEpochMillis
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
class SelectableDatesImp(
    private val limitDate: LocalDate? = null,
    private val dateMomentType: DateMomentType,
): SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val currentTimeInMillis = System.currentTimeMillis()
        val limitInMillis = when(dateMomentType){
            DateMomentType.End -> limitDate?.toEpochMillis(endOfTheDay = true) ?: utcTimeMillis
            DateMomentType.Start -> limitDate?.toEpochMillis(endOfTheDay = true) ?: currentTimeInMillis
        }
        return if (dateMomentType == DateMomentType.Start) utcTimeMillis <= limitInMillis
        else utcTimeMillis in limitInMillis..currentTimeInMillis
    }

    override fun isSelectableYear(year: Int): Boolean {
        val limitYear = limitDate?.year ?: LocalDate.now().year
        return year <= limitYear
    }
}