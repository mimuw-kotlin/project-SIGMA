package sigma.businessLogic.impl.managers

import sigma.dataAccess.impl.data.CompletionStatus
import sigma.dataAccess.impl.data.Resolution
import sigma.dataAccess.impl.data.Timeline
import sigma.dataAccess.impl.managers.ConfigurationManager
import sigma.dataAccess.model.loggers.ILogger
import sigma.dataAccess.model.parsers.IParser

// TODO("Refactor - move some responsibility to other classes???")
class ResolutionsManager(
    private var resolutions: MutableList<Resolution>,
    private var timeline: Timeline,
    private var logger: ILogger,
    private var parser: IParser,
    private var configurationManager: ConfigurationManager
) {
    fun init() : Unit {
        resolutions = parser.readResolutions(configurationManager.getResolutionsPath())
        timeline = parser.readTimeline(configurationManager.getTimelinePath())
    }

    fun close() : Unit {
        parser.writeResolutions(configurationManager.getResolutionsPath(), resolutions)
        parser.writeTimeline(configurationManager.getTimelinePath(), timeline)
    }

    fun addResolution(resolution: Resolution): Unit {
        // validate
        val name = resolution.name
        for (r in resolutions) {
            if (r.name == name) {
                logger.error("Cannot add resolution \"$name\". Resolution with this name already exists.")
                return
            }
        }

        // TODO("refactor")
        for (day in timeline.days) {
            day.add(CompletionStatus.UNKNOWN)
        }

        resolutions.add(resolution)
        logger.debug("Resolution \"$name\" added successfully.")
    }

    fun removeResolution(name: String): Unit {
        val index = resolutions.indexOfFirst { it.name == name }
        if (index == -1) {
            logger.error("Cannot remove resolution \"$name\". Resolution with this name does not exist.")
            return
        }

        // TODO("refactor")
        for (day in timeline.days) {
            day.removeAt(index)
        }

        resolutions.removeAt(index)
        logger.debug("Resolution \"$name\" removed successfully.")
    }

    fun modifyResolution(): Unit {
        TODO("enable changing name, description, and path")
    }

    fun moveResolution(from: Int, to: Int): Unit {
        if (from > resolutions.size || to > resolutions.size || from < 0 || to < 0 || from == to) {
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

        val resolution = resolutions[from]
        resolutions.removeAt(from)
        resolutions.add(to, resolution)
        logger.debug("Resolution moved successfully from position $from to $to.")
    }
}