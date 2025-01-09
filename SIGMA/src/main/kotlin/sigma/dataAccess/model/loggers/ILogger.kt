package sigma.dataAccess.model.loggers

interface ILogger {
    fun info(message : String) : Unit
    fun warn(message : String) : Unit
    fun error(message : String) : Unit
    fun debug(message : String) : Unit
}