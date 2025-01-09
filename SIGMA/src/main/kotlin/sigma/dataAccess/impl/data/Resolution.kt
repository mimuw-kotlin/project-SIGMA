package sigma.dataAccess.impl.data

import kotlinx.serialization.Serializable

@Serializable
data class Resolution(
    val name: String,
    val description: String? = null,
    val image: String? = null
)