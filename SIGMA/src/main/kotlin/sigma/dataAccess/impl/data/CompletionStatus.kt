package sigma.dataAccess.impl.data

enum class CompletionStatus(val code: String) {
    UNKNOWN("0"),
    COMPLETED("1"),
    PARTIAL("2"),
    UNCOMPLETED("3");

    override fun toString(): String {
        return code
    }

    companion object {
        fun fromString(code: String?): CompletionStatus {
            return entries.find { it.code == code } ?: UNKNOWN
        }
    }
}
