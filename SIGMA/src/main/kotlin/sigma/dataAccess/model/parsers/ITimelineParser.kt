package sigma.dataAccess.model.parsers

import sigma.dataAccess.impl.data.Timeline

interface ITimelineParser {
    fun read(path: String, resolutionsCount: Int) : Timeline
    fun write(path: String, timeline: Timeline) : Unit
}