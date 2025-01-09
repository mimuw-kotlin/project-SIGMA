package sigma.dataAccess.impl.data

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    var resolutions: MutableList<Resolution>,
    val timelinePath: String,
    val completionStatusWeights: CompletionStatusWeights,
    val dayColors: DayColors
) {
    companion object {
        fun getDefault(): Configuration {
            return Configuration(
                mutableListOf(),
                "timeline.csv",
                CompletionStatusWeights(1.0, 0.5, 0.0, 0.0),
                DayColors("#00FF00FF", "#FF0000FF", "#FFFFFFFF", "#0000FFFF", "#000000FF"))
                // green, red, white, blue, black
        }
    }

    fun update(resolutions: MutableList<Resolution>) {
        this.resolutions = resolutions
    }
}

@Serializable
data class CompletionStatusWeights(
    val completed: Double,
    val partial: Double,
    val uncompleted: Double,
    val unknown: Double
)

@Serializable
data class DayColors(
    val success: String,
    val failure: String,
    val empty: String,
    val future: String,
    val past: String
)
