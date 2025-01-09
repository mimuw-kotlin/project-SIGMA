package sigma.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import sigma.businessLogic.impl.managers.ResolutionsManager
import sigma.dataAccess.impl.loggers.ConsoleLogger
import sigma.dataAccess.impl.parsers.CsvTimelineParser
import sigma.dataAccess.impl.parsers.JsonConfigurationParser
import sigma.dataAccess.model.loggers.ILogger
import sigma.dataAccess.model.parsers.IConfigurationParser
import sigma.dataAccess.model.parsers.ITimelineParser

class StartScreen(private val manager: ResolutionsManager) : Screen {
    @Composable
    override fun Content() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("SIGMA", fontSize = 64.sp, style = MaterialTheme.typography.caption)
        }

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            try {
                manager.tryInit()
                navigator.push(HomeScreen(manager))
            } catch (e: Exception) {
                navigator.push(InitialScreen(manager))
            }
        }
    }
}
