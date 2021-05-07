package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.History
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.teraculus.lingojournalandroid.data.Language
import com.teraculus.lingojournalandroid.data.getAllLanguages
import com.teraculus.lingojournalandroid.model.ActivityCategory
import com.teraculus.lingojournalandroid.model.ActivityType
import com.teraculus.lingojournalandroid.model.UserPreferences

@Composable
fun SelectDialog(
    onDismissRequest: () -> Unit,
    title: String,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Column {
                Text(text = title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(16.dp))
                content()
            }
        }
    }
}

@Composable
fun InputDialog(
    onConfirm: (value: String) -> Unit,
    onDismissRequest: () -> Unit,
    title: String,
) {
    var value by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismissRequest) {
        Card(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title,
                    style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(value = value, onValueChange = { value = it }, label = { Text("New activity type") })
                Spacer(modifier = Modifier.size(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismissRequest) { Text(text = "Cancel") }
                    TextButton(onClick = {onConfirm(value)}) { Text(text = "Add") }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun LanguageSelectDialog(
    onItemClick: (item: Language) -> Unit,
    onDismissRequest: () -> Unit,
    preferences: UserPreferences?
) {
    val languages by remember {
        mutableStateOf(getAllLanguages().filter { preferences?.languages?.contains(it.code) == false })
    }
    val usedLanguages by remember {
        mutableStateOf(getAllLanguages().filter { preferences?.languages?.contains(it.code) == true })
    }
    SelectDialog(
        onDismissRequest = onDismissRequest,
        title = "Language",
    ) {

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if(usedLanguages.isNotEmpty()) {
                items(usedLanguages) { item ->
                    LanguageItem(item, onClick = onItemClick, isRecent = true)
                }
                item {
                    Divider()
                }
            }
            items(languages) { item ->
                LanguageItem(item, onClick = onItemClick)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun LanguageItem(lang: Language, onClick: (item: Language) -> Unit, isRecent: Boolean = false) {
    ListItem(
        text = { Text(lang.name) },
        modifier = Modifier.clickable { onClick(lang) },
        trailing = {
            if(isRecent)
                Icon(Icons.Rounded.History, contentDescription = null)
        })
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityTypeSelectDialog(
    onItemClick: (item: ActivityType) -> Unit,
    onAddTypeClick: (item: ActivityType) -> Unit,
    onDismissRequest: () -> Unit,
    groups: State<Map<ActivityCategory?, List<ActivityType>>?>,
) {
    SelectDialog(
        onDismissRequest = onDismissRequest,
        title = "Activity type",
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            groups.value.orEmpty().forEach { (category, categoryTypes) ->
                stickyHeader {
                    ActivityTypeHeader(category = category, onAddTypeClick)
                }
                items(categoryTypes) { item ->
                    ActivityTypeItem(item, onClick = onItemClick)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityTypeHeader(category: ActivityCategory?, onAddActivity: (item: ActivityType) -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }
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
                        Surface(elevation = 0.dp,
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = Color(category.color)) {
                            Icon(painter = painterResource(id = category.icon),
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(4.dp),
                                tint = MaterialTheme.colors.onPrimary,
                                contentDescription = null)
                        }
                    },
                    trailing = {
                        IconButton(onClick = { showAddDialog = true }) {
                            Icon(Icons.Rounded.AddCircle, contentDescription = null)
                        }
                    }
                )
                Divider()
            }
        }
    if(showAddDialog)
        InputDialog(
            onConfirm = { onAddActivity(ActivityType(category, it)); showAddDialog = false },
            onDismissRequest = { showAddDialog = false },
            title = "New ${category?.title} activity")
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActivityTypeItem(type: ActivityType, onClick: (item: ActivityType) -> Unit) {
    ListItem(text = { Text(type.name) }, modifier = Modifier.clickable { onClick(type) })
}

@ExperimentalMaterialApi
@Composable
fun RadioSelectDialog(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (text: String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    SelectDialog(
        onDismissRequest = onDismissRequest,
        title = title,
    ) {
        Column(Modifier.selectableGroup()) {
            options.forEach { text ->
                RadioWithText(text, text == selected, onSelect)
            }
        }
    }
}

@Composable
private fun RadioWithText(
    text: String,
    selected: Boolean,
    onSelect: (text: String) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = { onSelect(text) },
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null recommended for accessibility with screenreaders
        )
        Text(
            text = text,
            style = MaterialTheme.typography.body1.merge(),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
