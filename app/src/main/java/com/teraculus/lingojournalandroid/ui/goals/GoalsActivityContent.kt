package com.teraculus.lingojournalandroid.ui.goals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.ActivityGoal
import com.teraculus.lingojournalandroid.utils.toActivityTypeTitle
import io.realm.RealmResults
import org.bson.types.ObjectId
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

class GoalsViewModel(val repository: Repository = Repository.getRepository()) : ViewModel() {
    private val goals = repository.getActivityGoals()
    val frozen = Transformations.map(goals) {
        (it as RealmResults<ActivityGoal>).freeze().sortedByDescending { g -> g.id.timestamp }
    }
    val preferences = repository.getUserPreferences()
    val types = repository.getTypes()
    val lastAddedId = MutableLiveData<ObjectId>(null)
}

class GoalItemViewModel(
    private val frozenGoal: ActivityGoal,
    owner: LifecycleOwner,
    val repository: Repository = Repository.getRepository(),
) : ViewModel() {
    private val goal = Repository.getRepository().getActivityGoal(frozenGoal.id.toString())
    val snapshot =
        MutableLiveData<ActivityGoal>(if (goal.value?.isValid == true) goal.value!!.freeze<ActivityGoal>() else null)
    val goalWeekDaysString = Transformations.map(snapshot) {
        it?.weekDays?.sorted()?.map { d -> DayOfWeek.of(d) }
            ?.joinToString(truncated = ",") { d ->
                d.getDisplayName(TextStyle.SHORT,
                    Locale.getDefault())
            }
    }

    init {
        goal.observe(
            owner,
            Observer {
                snapshot.value = if (it?.isValid == true) it.freeze() else null
            }
        )
    }
}

class GoalItemViewModelFactory(
    private val rawGoal: ActivityGoal,
    private val owner: LifecycleOwner,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalItemViewModel(rawGoal, owner) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}

@Composable
fun GoalsActivityContent(
    model: GoalsViewModel = viewModel("goalsViewModel"),
    onDismiss: () -> Unit,
    onAddNewGoal: () -> Unit,
) {
    val scrollState = rememberLazyListState()
    val goals by model.frozen.observeAsState()
//    val lastAddedId by model.lastAddedId.observeAsState()

    Scaffold(
        topBar = {
            val elevation =
                if (MaterialTheme.colors.isLight && (scrollState.firstVisibleItemScrollOffset > 0 || scrollState.firstVisibleItemIndex > 0)) AppBarDefaults.TopAppBarElevation else 0.dp
            TopAppBar(
                title = { Text(text = "Goals") },
                backgroundColor = MaterialTheme.colors.background,
                elevation = elevation,
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(text = { Text(text = "New Goal") },
                onClick = { onAddNewGoal() },
                icon = {
                    Icon(Icons.Filled.Add, contentDescription = null)
                })
        }
    )
    {
        if (goals.isNullOrEmpty()) {
            WelcomingScreen()
        } else {
            LazyColumn(state = scrollState) {
                items(goals.orEmpty()) { goal ->
                    key(goal.id) {
                        FeedGoalRow(goal, onClick = {  })
                    }
                }
            }
        }
    }
}

//@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
//@Composable
//fun GoalRow(
//    rawGoal: ActivityGoal,
//    expand: Boolean,
//    model: GoalItemViewModel = viewModel("goalRow${rawGoal.id}",
//        GoalItemViewModelFactory(rawGoal, expand, LocalLifecycleOwner.current)),
//) {
//    val goal by model.snapshot.observeAsState()
//    val preferences by model.preferences.observeAsState()
//    val typeGroups = model.groupedTypes.observeAsState()
//    val goalWeekDaysString by model.goalWeekDaysString.observeAsState()
//    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
//    var showActivityTypeDialog by rememberSaveable { mutableStateOf(false) }
//    val expanded by model.expanded.observeAsState()
//    var deleted by remember { mutableStateOf(false) }
//
//    if (showLanguageDialog) {
//        LanguageSelectDialog(
//            onItemClick = {
//                model.onLanguageChange(it.code)
//                showLanguageDialog = false
//            },
//            onDismissRequest = { showLanguageDialog = false },
//            preferences = preferences)
//    }
//
//    if (showActivityTypeDialog) {
//        ActivityTypeSelectDialog(
//            groups = typeGroups,
//            onItemClick = {
//                model.onTypeChange(it)
//                showActivityTypeDialog = false
//            },
//            onAddTypeClick = {
//                model.addActivityType(it)
//            },
//            onDismissRequest = { showActivityTypeDialog = false })
//    }
//    goal?.let {
//
//        AnimatedVisibility(goal != null && !deleted, exit = shrinkVertically() + fadeOut()) {
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 8.dp)
//                    .clickable(onClick = { model.setExpanded(!expanded!!) }),
//                elevation = 2.dp)
//            {
//                Column {
//                    ListItem(
//                        icon = {
//                            ActivityRowIcon(goal!!.activityType?.category?.icon,
//                                goal!!.activityType?.category?.color)
//                        },
//                        text = {
//                            Text(goal!!.activityType?.name.toString(),
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis)
//                        },
//                        secondaryText = {
//                            Text(getLanguageDisplayName(goal!!.language),
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis)
//                        },
//                        trailing = {
//                            Switch(checked = goal!!.active,
//                                onCheckedChange = { model.onActiveChange(it) })
//                        }
//                    )
//
//                    AnimatedVisibility(visible = !expanded!!) {
//                        Row(modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp), Arrangement.SpaceBetween) {
//                            Text(goalWeekDaysString.orEmpty(),
//                                style = MaterialTheme.typography.body2)
//                            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null)
//                        }
//                    }
//
//                    AnimatedVisibility(visible = expanded!!) {
//                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
//                            DropDownTextField(label = { Text("Activity") },
//                                modifier = Modifier
//                                    .fillMaxWidth(),
//                                value = toActivityTypeTitle(goal?.activityType),
//                                onClick = { showActivityTypeDialog = true })
//
//                            Spacer(modifier = Modifier.size(8.dp))
//                            DropDownTextField(label = { Text("Language") },
//                                modifier = Modifier
//                                    .fillMaxWidth(),
//                                value = getLanguageDisplayName(goal!!.language),
//                                onClick = { showLanguageDialog = true })
//                            Spacer(modifier = Modifier.size(16.dp))
//                            ApplyTextStyle(textStyle = MaterialTheme.typography.caption,
//                                contentAlpha = ContentAlpha.medium) {
//                                Text("Week days")
//                            }
//                            Spacer(modifier = Modifier.size(8.dp))
//                            WeekDaysSelector(weekDays = goal!!.weekDays.toIntArray()) {
//                                model.toggleWeekDay(it)
//                            }
//                            Spacer(modifier = Modifier.size(16.dp))
//                            Divider()
//                            Spacer(modifier = Modifier.size(8.dp))
//                            Row(modifier = Modifier
//                                .fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically) {
//                                TextButton(onClick = { model.removeGoal(); deleted = true },
//                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onSurface)) {
//                                    Text(text = "Delete")
//                                }
//                                Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = null)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun WeekDaysSelector(weekDays: IntArray, onSelect: (Int) -> Unit) {
//    val dayLetters = remember {
//        IntRange(1, 7).map { DayOfWeek.of(it).toString().substring(0, 1) }
//    }
//
//    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
//        for (d in 1 until 8) {
//            ToggleButton(onClick = { onSelect(d) },
//                selected = weekDays.contains(d),
//                highlighted = true,
//                round = true) {
//                Text(dayLetters[d - 1])
//            }
//        }
//    }
//}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun FeedGoalRow(
    rawGoal: ActivityGoal,
    model: GoalItemViewModel = viewModel("goalRow${rawGoal.id}",
        GoalItemViewModelFactory(rawGoal, LocalLifecycleOwner.current)),
    onClick: (goalId: String) -> Unit,
) {
    val goal by model.snapshot.observeAsState()
    goal?.let {
        val cardColor = goal?.activityType?.category?.color?.let { it1 -> Color(it1) }

        AnimatedVisibility(goal != null , exit = shrinkVertically() + fadeOut()) {
            Card(
                backgroundColor = cardColor ?: MaterialTheme.colors.surface,
                contentColor = if(cardColor != null) Color.White else MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            {
                Column {
                    ListItem(
                        modifier = Modifier.clickable(onClick = { onClick(goal?.id.toString()) }),
                        icon = {
                            Icon(Icons.Rounded.RadioButtonUnchecked,
                                modifier = Modifier.size(42.dp),
                                tint = if(cardColor != null) Color.White else MaterialTheme.colors.onSurface,
                                contentDescription = null)
                        },
                        text = {
                            Text(toActivityTypeTitle(goal?.activityType),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                        },
                        secondaryText = {
                            Text(getLanguageDisplayName(goal!!.language),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomingScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 128.dp),
        contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)) {
            Text(text = "Set daily goals!",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Daily goals help you stay motivated and practice every day. Click on the green button to create your first goal!",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
        }
    }
}