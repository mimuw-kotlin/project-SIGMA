package sigma.dataAccess.impl.parsers

import sigma.dataAccess.impl.data.Timeline
import sigma.dataAccess.impl.data.CompletionStatus
import sigma.dataAccess.impl.data.Day
import sigma.dataAccess.impl.data.DayState
import sigma.dataAccess.model.parsers.ITimelineParser
import sigma.dataAccess.model.loggers.ILogger
import java.io.File
import java.time.LocalDate

class CsvTimelineParser(private val logger: ILogger) : ITimelineParser {
    override fun read(path: String, resolutionsCount: Int): Timeline {
        logger.debug("Starting to read timeline from file: $path")
        val file = File(path)
        if (!file.exists()) {
            val message = "Timeline file not found: $path."
            logger.error(message)
            throw IllegalArgumentException(message)
        }

        val lines = file.readLines()
        if (lines.isEmpty()) {
            val message = "Timeline file is empty: $path."
            logger.error(message)
            throw IllegalArgumentException(message)
        }

        val dateResolutionStatuses = lines.map { line ->
            val tokens = line.split(",")
            val date = LocalDate.parse(tokens[0])
            val statuses = tokens.drop(1).map { CompletionStatus.fromString(it) }
            logger.debug("Parsed date: $date, statuses: $statuses")
            date to statuses
        }

        if (dateResolutionStatuses.any { it.second.size != resolutionsCount }) {
            val message = "All days must have the same number of statuses as the first day."
            logger.error(message)
            throw IllegalArgumentException(message)
        }

        val startDate = dateResolutionStatuses.first().first
        val timeline = Timeline(startDate)
        var currentDate = startDate

        for ((date, statuses) in dateResolutionStatuses) {
            while (currentDate.isBefore(date)) {
                timeline.days.add(Day.getEmpty(resolutionsCount))
                logger.debug("Added empty day for date: $currentDate")
                currentDate = currentDate.plusDays(1)
            }
            val day = Day()
            statuses.forEach { day.add(it) }
            day.updateState()
            timeline.days.add(day)
            logger.debug("Added day for date: $date with statuses: $statuses")
            currentDate = currentDate.plusDays(1)
        }

        val today = LocalDate.now()
        while (currentDate.isBefore(today)) {
            timeline.days.add(Day.getEmpty(resolutionsCount))
            logger.debug("Added empty day for date: $currentDate")
            currentDate = currentDate.plusDays(1)
        }

        logger.debug("Finished reading timeline from $path. Total days: ${timeline.days.size}")
        return timeline
    }

    override fun write(path: String, timeline: Timeline) {
        logger.debug("Writing timeline to file: $path")
        val lines = timeline.days.mapIndexedNotNull { index, day ->
            if (day.getState() != DayState.EMPTY) {
                val date = timeline.startDate.plusDays(index.toLong()).toString()
                val statuses = day.getResults().joinToString(",") { it.toString() }
                "$date,$statuses"
            } else {
                null
            }
        }
        File(path).writeText(lines.joinToString("\n"))
        logger.debug("Finished writing timeline tp $path. Total days: ${timeline.days.size}")
    }
}
