package com.teraculus.lingojournalandroid.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.teraculus.lingojournalandroid.data.Language
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.data.getAllLanguages
import com.teraculus.lingojournalandroid.model.ActivityCategory
import com.teraculus.lingojournalandroid.model.ActivityType
import com.teraculus.lingojournalandroid.model.ThemePreference
import com.teraculus.lingojournalandroid.model.UserPreferences

@Composable
fun SelectDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    title: String,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Card(Modifier.padding(32.dp)) {
            Column {
                Text(text = title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(16.dp))
                Divider()
                content()
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


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ActivityTypeSelectDialog(
    onItemClick: (item: ActivityType) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val types by Repository.getRepository().getTypes().observeAsState()
    val groups = types?.groupBy { it.category }
    SelectDialog(
        onDismissRequest = onDismissRequest,
        title = "Activity type",
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            groups?.forEach { (category, categoryTypes) ->
                stickyHeader {
                    ActivityTypeHeader(category = category)
                }
                items(categoryTypes) { item ->
                    ActivityTypeItem(item, onClick = onItemClick)
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ActivityTypeHeader(category: ActivityCategory?) {
    if (category != null)
        Surface(elevation = 2.dp) {
            ListItem(text = { Text(category.title, style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold) },
                trailing = {
                    Icon(painter = painterResource(id = category.icon),
                        contentDescription = null, Modifier.size(24.dp))
                })
        }
}

@ExperimentalMaterialApi
@Composable
fun ActivityTypeItem(type: ActivityType, onClick: (item: ActivityType) -> Unit) {
    ListItem(text = { Text(type.name) }, modifier = Modifier.clickable { onClick(type) })
}

@ExperimentalMaterialApi
@Composable
fun ThemeSelectDialog(
    onDismissRequest: () -> Unit,
) {
    SelectDialog(
        onDismissRequest = onDismissRequest,
        title = "Select theme",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ListItem(text = { Text("Light") }, modifier = Modifier.clickable { Repository.getRepository().updateThemePreference(ThemePreference.LIGHT); onDismissRequest() })
            ListItem(text = { Text("Dark") }, modifier = Modifier.clickable { Repository.getRepository().updateThemePreference(ThemePreference.DARK); onDismissRequest() })
            ListItem(text = { Text("System") }, modifier = Modifier.clickable { Repository.getRepository().updateThemePreference(ThemePreference.SYSTEM); onDismissRequest() })
        }
    }
}
