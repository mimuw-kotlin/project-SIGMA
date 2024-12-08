package sigma.dataAccess.model.parsers

import sigma.dataAccess.impl.data.Resolution
import sigma.dataAccess.impl.data.Timeline

interface IParser {
    fun readTimeline(path: String) : Timeline
    fun readResolutions(path: String) : MutableList<Resolution>
    fun writeTimeline(path: String, timeline: Timeline) : Unit
    fun writeResolutions(path: String, resolutions: List<Resolution>) : Unit
}