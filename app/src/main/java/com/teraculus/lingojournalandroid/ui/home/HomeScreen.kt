package com.teraculus.lingojournalandroid.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.R
import com.teraculus.lingojournalandroid.ui.components.ActivityRow
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import com.teraculus.lingojournalandroid.utils.toDayString

@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    model: ActivityListViewModel = viewModel("activityListViewModel",
        ActivityListViewModelFactory()),
    onItemClick: (id: String) -> Unit,
) {
    ActivityList(model = model, onItemClick)
}

@ExperimentalMaterialApi
@Composable
fun ActivityList(
    model: ActivityListViewModel,
    onItemClick: (id: String) -> Unit
) {
    val groups by model.grouped.observeAsState()

    LazyColumn(
    ) {
        item {
//            if(MaterialTheme.colors.isLight) {
//                Image(painter = painterResource(id = R.drawable.lightbackground), contentDescription = null)
//            } else {
//                Image(painter = painterResource(id = R.drawable.darkbackground), contentDescription = null)
//            }
            Text("Journal", modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp), style = MaterialTheme.typography.h4)
        }
        if (groups != null && groups.orEmpty().isNotEmpty()) {
            groups.orEmpty().forEach { (date, items) ->
                item {
                    Column {
                        ApplyTextStyle(textStyle = MaterialTheme.typography.caption, contentAlpha = ContentAlpha.medium) {
                            Text(text = toDayString(date), modifier = Modifier.padding(top=24.dp, bottom = 16.dp, start = 16.dp))
                        }
                        Divider()
                    }
                }
                items(items.filter { it.isValid }) { activity ->
                    ActivityRow(activity, onClick = onItemClick)
                }
            }
        } else {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Add some activity, dude!", style = MaterialTheme.typography.h5)
                }
            }
        }
        item {
            Spacer(Modifier.size(200.dp))
        }
    }
}