package sigma.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import sigma.businessLogic.impl.managers.ResolutionsManager
import sigma.dataAccess.impl.loggers.ConsoleLogger
import sigma.dataAccess.impl.parsers.CsvTimelineParser
import sigma.dataAccess.impl.parsers.JsonConfigurationParser
import sigma.dataAccess.model.loggers.ILogger
import sigma.dataAccess.model.parsers.IConfigurationParser
import sigma.dataAccess.model.parsers.ITimelineParser
import sigma.ui.screens.StartScreen

fun main() = application {
    val logger: ILogger = ConsoleLogger() // TODO("use DI")
    val timelineParser: ITimelineParser = CsvTimelineParser(logger) // TODO("use DI")
    val configurationParser: IConfigurationParser = JsonConfigurationParser(logger)
    val manager = ResolutionsManager(logger, configurationParser, timelineParser)

    Window(onCloseRequest = {
        manager.close()
        exitApplication()
    }) {
        Navigator(StartScreen(manager))
    }
}
