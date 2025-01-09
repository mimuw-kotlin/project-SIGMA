package sigma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import sigma.businessLogic.impl.managers.ResolutionsManager
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class InitialScreen(private val manager: ResolutionsManager) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var importStatus by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to SIGMA!", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                navigator.replace(HomeScreen(manager))
            }) {
                Text("Start New Challenge")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                try {
                    val selectedFile = selectConfigurationFile()
                    if (selectedFile != null) {
                        manager.setConfigurationReadPath(selectedFile.path)
                        manager.tryInit()
                        importStatus = "Successfully imported: ${selectedFile.name}"
                        navigator.replace(HomeScreen(manager))
                    } else {
                        importStatus = "No file selected"
                    }
                } catch (e: Exception) {
                    importStatus = "Error importing file: ${e.message}"
                }
            }) {
                Text("Import Data")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(importStatus, fontSize = 14.sp)
        }
    }

    private fun selectConfigurationFile(): File? {
        val fileDialog = FileDialog(Frame(), "Select configuration in JSON", FileDialog.LOAD).apply {
            isVisible = true
        }

        val filePath = fileDialog.directory?.let { dir ->
            fileDialog.file?.let { file ->
                File(dir, file).absolutePath
            }
        }

        return if (filePath != null) File(filePath) else null
    }
}
