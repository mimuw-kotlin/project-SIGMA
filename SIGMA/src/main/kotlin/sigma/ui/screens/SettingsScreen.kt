package sigma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import sigma.businessLogic.impl.managers.ResolutionsManager
import sigma.dataAccess.impl.data.Resolution
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class SettingsScreen(private val manager: ResolutionsManager) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Settings(
            onBackClick = {
                navigator.pop()
            }
        )
    }

    @Composable
    private fun Settings(onBackClick: () -> Unit) {
        val resolutions = remember { mutableStateListOf(*manager.getResolutions().toTypedArray()) }
        var draggedItemIndex by remember { mutableStateOf<Int?>(null) }

        MaterialTheme {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
                SettingsHeader(
                    modifier = Modifier
                        .background(MaterialTheme.colors.primary)
                        .fillMaxWidth()
                        .padding(16.dp),
                    onBackClick = onBackClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Resolutions", style = MaterialTheme.typography.h5)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(resolutions) { index, resolution ->
                        ResolutionItem(
                            resolution = resolution,
                            onDrag = { draggedItemIndex = index },
                            onDrop = { targetIndex ->
                                if (draggedItemIndex != null && draggedItemIndex != targetIndex) {
                                    manager.moveResolution(draggedItemIndex!!, targetIndex)
                                    resolutions.add(targetIndex, resolutions.removeAt(draggedItemIndex!!))
                                }
                                draggedItemIndex = null
                            },
                            onDelete = {
                                manager.removeResolution(resolution.name)
                                resolutions.removeAt(index)
                            },
                            onModify = { /* Implement modify logic */ }
                        )
                    }
                    item {
                        IconButton(onClick = { /* Implement add logic */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Resolution")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Timeline path", style = MaterialTheme.typography.h5)
                // Draft implementation for Timeline path settings

                Spacer(modifier = Modifier.height(16.dp))

                Text("Completion status weights", style = MaterialTheme.typography.h5)
                // Draft implementation for Completion status weights settings

                Spacer(modifier = Modifier.height(16.dp))

                Text("Day colors", style = MaterialTheme.typography.h5)
                // Draft implementation for Day colors settings
            }
        }
    }


    @Composable
    fun ResolutionItem(
        resolution: Resolution,
        onDrag: () -> Unit,
        onDrop: (Int) -> Unit,
        onDelete: () -> Unit,
        onModify: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(MaterialTheme.colors.surface)
                .pointerInput(Unit) {
//                    detectDragGestures(
//                        onDragStart = { onDrag() },
//                        onDragEnd = { onDrop() }
//                    )
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(resolution.name, style = MaterialTheme.typography.body1)
                Row {
                    IconButton(onClick = onModify) {
                        Icon(Icons.Default.Edit, contentDescription = "Modify")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }

    @Composable
    private fun SettingsHeader(
        modifier: Modifier = Modifier,
        onBackClick: () -> Unit
    ) {
        Box(
            modifier = modifier
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            Text(
                text = "Settings",
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}