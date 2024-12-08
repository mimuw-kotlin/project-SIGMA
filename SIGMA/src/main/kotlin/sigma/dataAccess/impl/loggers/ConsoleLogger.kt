package sigma.dataAccess.impl.loggers

import sigma.dataAccess.model.loggers.ILogger

class ConsoleLogger : ILogger {
    override fun info(message: String) {
        debug(message)
    }

    override fun warn(message: String) {
        debug(message)
    }

    override fun error(message: String) {
        debug(message)
    }

    override fun debug(message: String) {
        println(message)
    }
}