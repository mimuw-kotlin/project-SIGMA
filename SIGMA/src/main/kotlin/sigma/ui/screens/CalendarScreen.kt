package sigma.ui.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import sigma.businessLogic.impl.managers.ResolutionsManager
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

class CalendarScreen(private val manager: ResolutionsManager) : Screen {
    private val DATE_CELL_HEIGHT = 100.dp
    private val DAY_NAME_CELL_HEIGHT = 50.dp

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Calendar(
            onDateSelected = {
                navigator.push(DayScreen(manager, LocalDate.ofInstant(it.toInstant(), ZoneId.systemDefault())))
            },
            onBackClick = {
                navigator.pop()
            }
        )
    }

    @Preview
    @Composable
    fun Calendar(
        onDateSelected: (Date) -> Unit,
        onBackClick: () -> Unit,
        modifier: Modifier = Modifier,
        initDate: Date = Date(),
        minYear: Int = manager.getStartDate().year,
        maxYear: Int = LocalDate.now().year
    ) {
        val calendar = GregorianCalendar().apply { time = initDate }
        val selectedYear = remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
        val selectedMonth = remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
        val selectedDay = remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

        MaterialTheme {
            Card(
                elevation = 8.dp,
                modifier = modifier
            ) {
                Column {
                    CurrentDateHeader(
                        year = selectedYear.value,
                        month = selectedMonth.value,
                        day = selectedDay.value,
                        modifier = Modifier
                            .background(MaterialTheme.colors.primary)
                            .fillMaxWidth()
                            .padding(16.dp),
                        onBackClick = onBackClick
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedYear.value > minYear || selectedMonth.value > Calendar.JANUARY) {
                            IconButton(onClick = {
                                if (selectedMonth.value == Calendar.JANUARY) {
                                    selectedMonth.value = Calendar.DECEMBER
                                    selectedYear.value -= 1
                                } else {
                                    selectedMonth.value -= 1
                                }
                            }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        if (selectedYear.value < maxYear || selectedMonth.value < Calendar.DECEMBER) {
                            IconButton(onClick = {
                                if (selectedMonth.value == Calendar.DECEMBER) {
                                    selectedMonth.value = Calendar.JANUARY
                                    selectedYear.value += 1
                                } else {
                                    selectedMonth.value += 1
                                }
                            }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        DateSelector(
                            month = selectedMonth.value,
                            year = selectedYear.value,
                            minYear = minYear,
                            maxYear = maxYear,
                            onMonthSelected = { selectedMonth.value = it },
                            onYearSelected = { selectedYear.value = it },
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        )

                        Header(days = createDaysFirstLetter(), modifier = Modifier.fillMaxWidth())
                        Divider(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colors.primary))
                        CalendarTable(
                            selectedYear = selectedYear.value,
                            selectedMonth = selectedMonth.value,
                            selectedDay = selectedDay.value,
                            onDaySelected = { selectedDay.value = it }
                        )
                    }
                    TextButton(
                        onClick = {
                            onDateSelected(
                                GregorianCalendar(
                                    selectedYear.value,
                                    selectedMonth.value,
                                    selectedDay.value
                                ).time
                            )
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = MaterialTheme.colors.onPrimary
                        )
                    ) {
                        Text(
                            text = "View",
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CalendarTable(
        selectedYear: Int,
        selectedMonth: Int,
        selectedDay: Int,
        onDaySelected: (day: Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        var dayCounter = 1
        val maxDay = GregorianCalendar(selectedYear, selectedMonth, 1).daysCount()

        if (selectedDay > maxDay) {
            throw IllegalStateException("Day must be lower than $maxDay")
        }

        val startDay = GregorianCalendar(selectedYear, selectedMonth, 1)
            .apply { firstDayOfWeek = Calendar.SUNDAY }.get(Calendar.DAY_OF_WEEK)

        for (column in 1..6) {
            Row(modifier = modifier) {
                for (cell in 1..7) {
                    val beforeStartDayOnColumnOne = cell < startDay && column == 1

                    if (dayCounter > maxDay || beforeStartDayOnColumnOne) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        Day(
                            day = dayCounter,
                            month = selectedMonth,
                            year = selectedYear,
                            selected = (selectedDay == dayCounter),
                            onDaySelected = { onDaySelected(it) },
                            modifier = Modifier
                                .weight(1f)
                                .height(DATE_CELL_HEIGHT)
                                .padding(3.dp)
                                .clip(RoundedCornerShape(10))
                        )
                        dayCounter++
                    }
                }
            }
        }
    }

    @Composable
    private fun DateSelector(
        month: Int,
        year: Int,
        minYear: Int,
        maxYear: Int,
        onMonthSelected: (month: Int) -> Unit,
        onYearSelected: (year: Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.CenterStart
        ) {
            MonthSelector(
                month = month,
                onValueChange = { onMonthSelected(it) },
            )

            YearSelector(
                year = year,
                onValueChange = { onYearSelected(it) },
                minYear = minYear,
                maxYear = maxYear,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }

    @Composable
    private fun CurrentDateHeader(
        year: Int,
        month: Int,
        day: Int,
        modifier: Modifier = Modifier,
        onBackClick: () -> Unit
    ) {
        Box(
            modifier = modifier
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            Text(
                text = SimpleDateFormat("EEEE, dd MMMM yyyy").format(
                    GregorianCalendar(
                        year,
                        month,
                        day
                    ).time
                ),
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    @Composable
    private fun YearSelector(
        year: Int,
        onValueChange: (Int) -> Unit,
        minYear: Int,
        maxYear: Int,
        modifier: Modifier = Modifier
    ) {
        val expanded = remember { mutableStateOf(false) }

        Row(
            modifier = modifier.clickable { expanded.value = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = year.toString())
            Spacer(Modifier.width(4.dp))
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                for (y in minYear..maxYear) {
                    DropdownMenuItem(onClick = {
                        onValueChange(y)
                        expanded.value = false
                    }) {
                        Text(text = y.toString())
                    }
                }
            }
        }
    }

    @Composable
    private fun MonthSelector(
        month: Int,
        onValueChange: (Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val expanded = remember { mutableStateOf(false) }

        Row(
            modifier = modifier.clickable { expanded.value = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = getMonthName(month).uppercase())
            Spacer(Modifier.width(4.dp))
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                for (m in Calendar.JANUARY..Calendar.DECEMBER) {
                    DropdownMenuItem(onClick = {
                        onValueChange(m)
                        expanded.value = false
                    }) {
                        Text(text = getMonthName(m).uppercase())
                    }
                }
            }
        }
    }

    @Composable
    private fun Header(
        days: List<String>,
        modifier: Modifier = Modifier
    ) {
        Row(modifier = modifier) {
            for (dayName in days) {
                DayName(day = dayName, modifier = Modifier.weight(1f).height(DAY_NAME_CELL_HEIGHT))
            }
        }
    }

    @Composable
    private fun Day(
        day: Int,
        month: Int,
        year: Int,
        selected: Boolean,
        onDaySelected: (Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .background(
                    if (selected) {
                        MaterialTheme.colors.primary
                    } else {
//                        MaterialTheme.colors.onPrimary
                        manager.getDayColor(LocalDate.of(year, month + 1, day)) // +1 because month is 0-based
                    }
                )
                .clickable { onDaySelected(day) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.toString(),
                color = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary
            )
        }
    }

    @Composable
    private fun DayName(
        day: String,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day,
                color = MaterialTheme.colors.primary
            )
        }
    }

    private fun getMonthName(month: Int, locale: Locale = Locale.getDefault()): String {
        val cal = Calendar.getInstance(locale)
        cal.set(Calendar.MONTH, month)
        return SimpleDateFormat("MMMM", locale).format(cal.time)
    }

    private fun createDaysFirstLetter(locale: Locale = Locale.getDefault()): List<String> { // TODO: enable starting not from Sunday
        val symbols = DateFormatSymbols(locale)
        return symbols.shortWeekdays.filter { it.isNotEmpty() }.map { it.first().uppercase() }
    }

    private fun GregorianCalendar.daysCount(): Int { // TODO: improve this
        return when (get(Calendar.MONTH)) {
            Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
            Calendar.FEBRUARY -> if (isLeapYear(get(Calendar.YEAR))) 29 else 28
            Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
            else -> 0
        }
    }
}