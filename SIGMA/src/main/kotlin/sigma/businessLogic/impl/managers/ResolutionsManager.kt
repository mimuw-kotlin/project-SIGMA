package sigma.businessLogic.impl.managers

import androidx.compose.ui.graphics.Color
import sigma.dataAccess.impl.data.*
import sigma.dataAccess.model.loggers.ILogger
import sigma.dataAccess.model.parsers.IConfigurationParser
import sigma.dataAccess.model.parsers.ITimelineParser
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// TODO("Refactor - move some responsibility to other classes???")
class ResolutionsManager(
    private val logger: ILogger,
    private val configurationParser: IConfigurationParser,
    private val timelineParser: ITimelineParser
) {
    private var configurationWritePath: String = "appsettings.json"
    private var configurationReadPath: String = configurationWritePath
    private var configuration: Configuration = Configuration.getDefault()
    private var timeline: Timeline = Timeline.getDefault()

    fun tryInit(): Unit {
        configuration = configurationParser.read(configurationReadPath)
        logger.debug("Successfully parsed configuration.")
        timeline = timelineParser.read(configuration.timelinePath, configuration.resolutions.size)
        logger.debug("Successfully parsed timeline.")
    }

    fun close(): Unit {
        configurationParser.write(configurationWritePath, configuration)
        timelineParser.write(configuration.timelinePath, timeline)
    }

    fun setConfigurationReadPath(path: String) {
        configurationReadPath = path
    }

    fun getResolutions(): List<Resolution> {
        return configuration.resolutions
    }

    fun addResolution(resolution: Resolution): Unit {
        // validate
        val name = resolution.name
        for (r in configuration.resolutions) {
            if (r.name == name) {
                logger.warn("Cannot add resolution \"$name\". Resolution with this name already exists.")
                return
            }
        }

        // TODO("refactor")
        for (day in timeline.days) {
            day.add(CompletionStatus.UNKNOWN)
        }

        configuration.resolutions.add(resolution)
        logger.debug("Resolution \"$name\" added successfully.")
    }

    fun removeResolution(name: String): Unit {
        val index = configuration.resolutions.indexOfFirst { it.name == name }
        if (index == -1) {
            logger.error("Cannot remove resolution \"$name\". Resolution with this name does not exist.")
            return
        }

        // TODO("refactor")
        for (day in timeline.days) {
            day.removeAt(index)
            day.updateState()
        }

        configuration.resolutions.removeAt(index)
        logger.debug("Resolution \"$name\" removed successfully.")
    }

    fun modifyResolution(): Unit {
        TODO("enable changing name, description, and path")
    }

    fun moveResolution(from: Int, to: Int): Unit {
        if (from > configuration.resolutions.size || to > configuration.resolutions.size || from < 0 || to < 0 || from == to) {
            logger.error("Cannot move resolution to specified position.")
            logger.debug("Invalid indices: from = $from, to = $to.")
            return
        }

        // TODO("refactor")
        for (day in timeline.days) {
            val status = day[from]
            day.removeAt(from)
            day.add(to, status)
        }

        val resolution = configuration.resolutions[from]
        configuration.resolutions.removeAt(from)
        configuration.resolutions.add(to, resolution)
        logger.debug("Resolution moved successfully from position $from to $to.")
    }

    fun getStartDate(): LocalDate {
        return timeline.startDate
    }

    fun getDayColor(date: LocalDate): Color {
        val diff = ChronoUnit.DAYS.between(timeline.startDate, date)
        val state = getDayState(diff.toInt())
        return when (state) {
            DayState.RECORDED -> lerp(
                colorFromString(configuration.dayColors.success),
                colorFromString(configuration.dayColors.failure),
                getDaySuccessRate(timeline.days[diff.toInt()])
            )

            DayState.PAST -> colorFromString(configuration.dayColors.past)
            DayState.FUTURE -> colorFromString(configuration.dayColors.future)
            DayState.EMPTY -> colorFromString(configuration.dayColors.empty)
        }
    }

    private fun colorFromString(color: String): Color {
        if (color.length != 7 && color.length != 9) {
            val message = "Invalid color string: $color."
            logger.error(message)
            throw IllegalArgumentException(message)
        }

        val red = color.substring(1, 3).toInt(16) / 255.0f
        val green = color.substring(3, 5).toInt(16) / 255.0f
        val blue = color.substring(5, 7).toInt(16) / 255.0f
        if (color.length == 7) {
            return Color(red, green, blue)
        }
        val alpha = color.substring(7, 9).toInt(16) / 255.0f
        return Color(red, green, blue, alpha)
    }

    private fun lerp(start: Color, end: Color, fraction: Float): Color {
        val red = start.red + (end.red - start.red) * fraction
        val green = start.green + (end.green - start.green) * fraction
        val blue = start.blue + (end.blue - start.blue) * fraction
        val alpha = start.alpha + (end.alpha - start.alpha) * fraction

        return Color(red, green, blue, alpha)
    }

    private fun getDayState(diff: Int): DayState {
        return when {
            diff < 0 -> DayState.PAST
            diff >= timeline.days.size -> DayState.FUTURE
            else -> timeline.days[diff].getState()
        }
    }

    private fun getDaySuccessRate(day: Day): Float {
        if (day.getState() != DayState.RECORDED) {
            val message = "Cannot calculate success rate for day $day with state: ${day.getState()}."
            logger.error(message)
            throw IllegalArgumentException(message)
        }

        val statuses = day.getResults()
        val completed = statuses.count { it == CompletionStatus.COMPLETED }
        val partial = statuses.count { it == CompletionStatus.PARTIAL }
        val uncompleted = statuses.count { it == CompletionStatus.UNCOMPLETED }
        val unknown = statuses.count { it == CompletionStatus.UNKNOWN }
        val total = statuses.size
        val weights = configuration.completionStatusWeights
        val success = weights.completed * completed + weights.partial * partial +
                weights.uncompleted * uncompleted + weights.unknown * unknown
        return (success / total).toFloat()
    }
}