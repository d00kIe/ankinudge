package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.teraculus.lingojournalandroid.model.ActivityCategory
import com.teraculus.lingojournalandroid.model.ActivityType
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ActivityTypeSelectDialog(
    onItemClick: (item: ActivityType) -> Unit,
    onAddTypeClick: (item: ActivityType) -> Unit,
    onDismissRequest: () -> Unit,
    groups: State<Map<ActivityCategory?, List<ActivityType>>?>,
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var dialogCategory: ActivityCategory? by remember { mutableStateOf(null) }

    if (showAddDialog && dialogCategory != null)
        NewActivityTypeDialog(
            onConfirm = { name, unit ->
                onAddTypeClick(ActivityType(dialogCategory, name, unit = unit))
                showAddDialog = false
                dialogCategory = null
            },
            onDismissRequest = { showAddDialog = false },
            title = "New ${dialogCategory?.title?.toLowerCase(Locale.ROOT)} activity")


    var selectedCategory: ActivityCategory? by remember { mutableStateOf(null) }
    var title = remember(selectedCategory) { if(selectedCategory == null) "Choose category" else null }
    val categories = remember(groups) { groups.value.orEmpty().keys.toList() }
    SelectDialog(
        onDismissRequest = onDismissRequest,
        title = title,
    ) {
        if(selectedCategory == null) {
            LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                items(categories.size) { index ->
                    val category = categories[index]
                    Card(onClick = { selectedCategory = category }, elevation = 2.dp, modifier = Modifier.padding(16.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                            ActivityTypeIcon(category = category, size = 48.dp, onClick = { selectedCategory = category })
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(text = category?.title.toString())
                        }
                    }
                }
            }
        }



        if(selectedCategory != null) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    ActivityTypeHeader(category = selectedCategory) {
                        dialogCategory = it
                        showAddDialog = true
                    }
                }
                items(groups.value.orEmpty()[selectedCategory].orEmpty()) { item ->
                    ActivityTypeItem(item, onClick = onItemClick)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityTypeHeader(
    category: ActivityCategory?,
    onAddTypeClick: (item: ActivityCategory) -> Unit,
) {

    if (category != null)
        Surface {
            Column {
                ListItem(
                    text = {
                        Text(category.title,
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold)
                    },
                    icon = {
                        ActivityTypeIcon(category = category)
                    },
                    trailing = {
                        OutlinedButton(
                            onClick = { onAddTypeClick(category) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onSurface)) {
                            Text("Add new")
                        }
                    }
                )
                Divider()
            }
        }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityTypeItem(type: ActivityType, onClick: (item: ActivityType) -> Unit) {
    ListItem(
        text = { Text(type.name) },
        secondaryText = { Text("Unit: ${type.unit!!.title}") },
        modifier = Modifier.clickable { onClick(type) })
}