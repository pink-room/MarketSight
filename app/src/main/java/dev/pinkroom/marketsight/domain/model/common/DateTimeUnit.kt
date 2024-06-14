package dev.pinkroom.marketsight.domain.model.common

sealed interface DateTimeUnit {
    data object Hour: DateTimeUnit
    data object Day: DateTimeUnit
    data object Month: DateTimeUnit
    data object Year: DateTimeUnit
    data object All: DateTimeUnit
}