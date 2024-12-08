package sigma.dataAccess.impl.managers

import java.time.LocalDate

class TimeManager {
    companion object {
        fun today(): LocalDate {
            return LocalDate.now()
        }

        fun parse(date: String): LocalDate {
            return LocalDate.parse(date)
        }

        fun toString(date: LocalDate): String {
            return date.toString()
        }
    }
}
