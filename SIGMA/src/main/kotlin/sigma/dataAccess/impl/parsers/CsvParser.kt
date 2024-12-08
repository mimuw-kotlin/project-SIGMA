package sigma.dataAccess.impl.parsers

import sigma.dataAccess.impl.data.Resolution
import sigma.dataAccess.impl.data.Timeline
import sigma.dataAccess.impl.data.CompletionStatus
import sigma.dataAccess.impl.data.Day
import sigma.dataAccess.impl.managers.TimeManager
import sigma.dataAccess.model.parsers.IParser
import sigma.dataAccess.model.loggers.ILogger
import java.io.File

class CsvParser(private val logger: ILogger) : IParser {
    override fun readResolutions(path: String): MutableList<Resolution> {
        logger.debug("Starting to read resolutions from file: $path")
        val file = File(path)
        if (!file.exists()) {
            logger.error("Resolutions file not found: $path")
            throw IllegalArgumentException("Resolutions file not found: $path")
        }

        val lines = file.readLines()
        if (lines.size < 3) {
            logger.error("Invalid resolutions file format: $path")
            throw IllegalArgumentException("Invalid resolutions file format.")
        }

        val names = lines[0].split(",")
        val descriptions = lines[1].split(",")
        val images = lines[2].split(",")

        if (names.size != descriptions.size || names.size != images.size) {
            logger.error("Mismatch in counts of names, descriptions, and images.")
            throw IllegalArgumentException("Mismatch in counts of names, descriptions, and images.")
        }

        val resolutions = mutableListOf<Resolution>()
        for (i in names.indices) {
            val name = names[i].takeIf { it.isNotBlank() }
                ?: throw IllegalArgumentException("Resolution name cannot be blank.")
            val description = descriptions[i].takeIf { it.isNotBlank() }
            val image = images[i].takeIf { it.isNotBlank() }
            resolutions.add(Resolution(name, description, image))
            logger.debug("Added resolution: name=$name, description=$description, image=$image")
        }

        logger.debug("Finished reading resolutions. Total resolutions: ${resolutions.size}")
        return resolutions
    }

    override fun readTimeline(path: String): Timeline {
        logger.debug("Starting to read timeline from file: $path")
        val file = File(path)
        if (!file.exists()) {
            logger.error("Timeline file not found: $path")
            throw IllegalArgumentException("Timeline file not found: $path")
        }

        val lines = file.readLines()
        if (lines.isEmpty()) {
            logger.error("Timeline file is empty: $path")
            throw IllegalArgumentException("Timeline file is empty.")
        }

        val dateResolutionStatuses = lines.map { line ->
            val tokens = line.split(",")
            val date = TimeManager.parse(tokens[0])
            val statuses = tokens.drop(1).map { CompletionStatus.fromString(it) }
            logger.debug("Parsed date: $date, statuses: $statuses")
            date to statuses
        }

        val resolutionsCount = dateResolutionStatuses.first().second.size
        if (dateResolutionStatuses.any { it.second.size != resolutionsCount }) {
            logger.error("All days must have the same number of statuses as the first day.")
            throw IllegalArgumentException("All days must have the same number of statuses as the first day.")
        }

        val startDate = dateResolutionStatuses.first().first
        val timeline = Timeline(startDate)
        var currentDate = startDate

        for ((date, statuses) in dateResolutionStatuses) {
            while (currentDate.isBefore(date)) {
                timeline.days.add(Day.createEmptyDay(resolutionsCount))
                logger.debug("Added empty day for date: $currentDate")
                currentDate = currentDate.plusDays(1)
            }
            val day = Day()
            statuses.forEach { day.add(it) }
            timeline.days.add(day)
            logger.debug("Added day for date: $date with statuses: $statuses")
            currentDate = currentDate.plusDays(1)
        }

        val today = TimeManager.today()
        while (currentDate.isBefore(today)) {
            timeline.days.add(Day.createEmptyDay(resolutionsCount))
            logger.debug("Added empty day for date: $currentDate")
            currentDate = currentDate.plusDays(1)
        }

        logger.debug("Finished reading timeline. Total days: ${timeline.days.size}")
        return timeline
    }

    override fun writeResolutions(path: String, resolutions: List<Resolution>) {
        logger.debug("Writing resolutions to file: $path")
        val names = resolutions.joinToString(",") { it.name }
        val descriptions = resolutions.joinToString(",") { it.description ?: "" }
        val images = resolutions.joinToString(",") { it.image ?: "" }
        File(path).writeText("$names\n$descriptions\n$images\n")
        logger.debug("Finished writing resolutions. Total resolutions: ${resolutions.size}")
    }

    override fun writeTimeline(path: String, timeline: Timeline) {
        logger.debug("Writing timeline to file: $path")
        val lines = timeline.days.mapIndexed { index, day ->
            val date = TimeManager.toString(timeline.startDate.plusDays(index.toLong()))
            val statuses = day.getResults().joinToString(",") { it.toString() }
            "$date,$statuses"
        }
        File(path).writeText(lines.joinToString("\n"))
        logger.debug("Finished writing timeline. Total days: ${timeline.days.size}")
    }
}
