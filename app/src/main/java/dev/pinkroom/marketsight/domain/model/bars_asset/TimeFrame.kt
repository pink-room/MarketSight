package dev.pinkroom.marketsight.domain.model.bars_asset

sealed class TimeFrame(val frameValue: String){
    data class Minutes(val value: Int): TimeFrame(frameValue = "${value}T"){
        init {
            require(value in 1..59) { "Vale need to be between 1 and 59." }
        }
    }
    data class Hour(val value: Int): TimeFrame(frameValue = "${value}H"){
        init {
            require(value in 1..23) { "Value need to be between 1 and 23." }
        }
    }
    data object Day: TimeFrame(frameValue = "1D")
    data object Week: TimeFrame(frameValue = "1W")
    data class Month(val value: Int): TimeFrame(frameValue = "${value}M"){
        init {
            val specificValues = listOf(1,2,3,6,12)
            require(value in specificValues) { "Value need to be one of: ${specificValues.joinToString(separator = ", ")}." }
        }
    }
}
