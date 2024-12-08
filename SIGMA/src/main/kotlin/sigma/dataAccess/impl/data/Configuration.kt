package sigma.dataAccess.impl.data

data class Configuration(
    val resolutions: Resolutions,
    val completionStatus: CompletionStatusItem,
    val timeline: TimelineItem
)

data class Resolutions(
    val file: File,
    val list: List<ResolutionItem>
)

data class File(
    val path: String,
    val filename: String
)

data class ResolutionItem(
    val name: String,
    val description: String,
    val image: String
)

data class CompletionStatusItem(
    val weights: Weights
)

data class Weights(
    val completed: Double,
    val partial: Double,
    val uncompleted: Double,
    val unknown: Double
)

data class TimelineItem(
    val file: File
)
