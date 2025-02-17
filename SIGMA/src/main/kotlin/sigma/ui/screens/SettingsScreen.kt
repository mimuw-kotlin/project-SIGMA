package sigma.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.popUntil
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import sigma.businessLogic.impl.managers.ResolutionsManager
import sigma.dataAccess.impl.data.Resolution

class SettingsScreen(private val manager: ResolutionsManager) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Settings(
            onBackClick = {
                navigator.popUntil<HomeScreen, Screen>()
            }
        )
    }

    @Composable
    private fun Settings(onBackClick: () -> Unit) {
        val resolutions = remember { mutableStateListOf(*manager.getResolutions().toTypedArray()) }
        var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
        var newResolutionIndex by remember { mutableStateOf(-1) }

        MaterialTheme {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
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
                        var dialog = false
                        if (index == newResolutionIndex) {
                            newResolutionIndex = -1
                            dialog = true
                        }

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
                            onModify = { modifiedResolution ->
                                manager.modifyResolution(resolution.name, modifiedResolution)
                                resolutions[index] = modifiedResolution
                            },
                            dialog = dialog
                        )
                    }
                    item {
                        IconButton(onClick = {
                            val newResolution = Resolution("")
                            manager.addResolution(newResolution)
                            newResolutionIndex = resolutions.size
                            resolutions.add(newResolution)
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Resolution")
                        }
                    }
                }
            }
        }
    }


    @Composable
    private fun ResolutionItem(
        resolution: Resolution,
        onDrag: () -> Unit,
        onDrop: (Int) -> Unit,
        onDelete: () -> Unit,
        onModify: (Resolution) -> Unit,
        dialog: Boolean
    ) {
        var isDialogOpen by remember { mutableStateOf(dialog) }
        var name by remember { mutableStateOf(resolution.name) }
        var description by remember { mutableStateOf(resolution.description ?: "") }
        var image by remember { mutableStateOf(resolution.image ?: "") }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(MaterialTheme.colors.surface)
                .pointerInput(Unit) {
                    // Uncomment to implement drag functionality
                    // detectDragGestures(
                    //     onDragStart = { onDrag() },
                    //     onDragEnd = { onDrop() }
                    // )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(resolution.name, style = MaterialTheme.typography.body1)
                Row {
                    IconButton(onClick = { isDialogOpen = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Modify")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }

        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = { isDialogOpen = false },
                title = { Text("Modify Resolution") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = image,
                            onValueChange = { image = it },
                            label = { Text("Image URL") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        isDialogOpen = false
                        val modifiedResolution = Resolution(name, description, image)
                        onModify(modifiedResolution)
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        isDialogOpen = false
                        if (resolution.name.isEmpty()) {
                            onDelete()
                        }
                    }) {
                        Text("Cancel")
                    }
                }
            )
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
                    imageVector = Icons.Default.Menu,
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