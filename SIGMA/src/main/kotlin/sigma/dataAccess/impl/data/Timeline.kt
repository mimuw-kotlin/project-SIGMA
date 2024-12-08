package sigma.dataAccess.impl.data

import java.time.LocalDate

data class Timeline(
    val startDate: LocalDate,
    var days: MutableList<Day> = mutableListOf()
)