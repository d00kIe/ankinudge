package com.teraculus.lingojournalandroid.ui.goals

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.data.getLanguageDisplayName
import com.teraculus.lingojournalandroid.model.ActivityGoal
import com.teraculus.lingojournalandroid.model.ActivityType
import com.teraculus.lingojournalandroid.ui.components.*
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import io.realm.RealmResults
import org.bson.types.ObjectId
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class GoalsViewModel(val repository: Repository = Repository.getRepository()) : ViewModel() {
    private val goals = repository.getActivityGoals()
    val frozen = Transformations.map(goals) { (it as RealmResults<ActivityGoal>).freeze().sortedByDescending { g -> g.id.timestamp } }
    val preferences = repository.getUserPreferences()
    val types = repository.getTypes()
    val groupedTypes = Transformations.map(types) { it.orEmpty().groupBy { it1 -> it1.category } }
    val lastAddedId = MutableLiveData<ObjectId>(null)

    fun addNewActivityGoal(type: ActivityType) {
        val language = preferences.value?.languages?.firstOrNull() ?: "en"
        val weekDays = arrayOf(1,2,3,4,5,6,7)
        val goal = ActivityGoal(text = "", language = language, activityType = type, date = LocalDate.now(), reminder = null, weekDays = weekDays)
        lastAddedId.value = goal.id
        repository.addActivityGoal(goal)
    }

    fun addActivityType(it: ActivityType) {
        repository.addActivityType(it)
    }
}

class GoalItemViewModel(private val frozenGoal: ActivityGoal,
                        private val expand: Boolean,
                        owner: LifecycleOwner,
                        val repository: Repository = Repository.getRepository()) : ViewModel() {
    private val goal = Repository.getRepository().getActivityGoal(frozenGoal.id.toString())
    val snapshot =
        MutableLiveData<ActivityGoal>(if (goal.value?.isValid == true) goal.value!!.freeze<ActivityGoal>() else null)
    val preferences = repository.getUserPreferences()
    val types = repository.getTypes()
    val groupedTypes = Transformations.map(types) { it.orEmpty().groupBy { it1 -> it1.category } }
    var expanded = MutableLiveData(expand)
    val goalWeekDaysString = Transformations.map(snapshot) {
        it?.weekDays?.sorted()?.map { d -> DayOfWeek.of(d) }
            ?.joinToString(truncated = ",") { d -> d.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
    }

    init {
        goal.observe(
            owner,
            Observer {
                snapshot.value = if (it?.isValid == true) it.freeze() else null
            }
        )
    }

    fun setExpanded(value: Boolean) {
        expanded.value = value
    }

    fun onLanguageChange(value: String) {
        repository.updateActivityGoal(frozenGoal.id) {goal ->
            goal.language = value
        }
    }

    fun onTypeChange(value: ActivityType) {
        repository.updateActivityGoal(frozenGoal.id) { goal ->
            goal.activityType = value
        }
    }

    fun addActivityType(it: ActivityType) {
        repository.addActivityType(it)
    }

    fun onActiveChange(value: Boolean) {
        repository.updateActivityGoal(frozenGoal.id) { goal ->
            goal.active = value
        }
    }

    fun removeGoal() {
        Handler(Looper.getMainLooper()).postDelayed({
            repository.removeActivityGoal(frozenGoal.id)
        }, 500)
    }

    fun toggleWeekDay(day: Int) {
        repository.updateActivityGoal(frozenGoal.id) { goal ->
            if(goal.weekDays.contains(day)) {
                goal.weekDays.removeIf() { it == day }
            } else {
                goal.weekDays.add(day)
            }
        }
    }
}

class GoalItemViewModelFactory(
    private val rawGoal: ActivityGoal,
    private val expanded: Boolean,
    private val owner: LifecycleOwner,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalItemViewModel(rawGoal, expanded, owner) as T
        }

        throw IllegalArgumentException("Unknown view model class")
    }
}

@Composable
fun GoalsActivityContent(
    model: GoalsViewModel = viewModel("goalsViewModel"),
    onDismiss: () -> Unit,
) {
    val scrollState = rememberLazyListState()
    val goals by model.frozen.observeAsState()
    val typeGroups = model.groupedTypes.observeAsState()
    val lastAddedId by model.lastAddedId.observeAsState()
    var showActivityTypeDialog by rememberSaveable { mutableStateOf(false) }

    if(showActivityTypeDialog) {
        ActivityTypeSelectDialog(
            groups = typeGroups,
            onItemClick = {
                model.addNewActivityGoal(it)
                showActivityTypeDialog = false
            },
            onAddTypeClick = {
                model.addActivityType(it)
            },
            onDismissRequest = { showActivityTypeDialog = false })
    }

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
            ExtendedFloatingActionButton(text = { Text(text = "New Goal") }, onClick = { showActivityTypeDialog = true }, icon = {
                Icon(Icons.Filled.Add, contentDescription = null)
            })
        }
    )
    {
        LazyColumn(state = scrollState) {
            items(goals.orEmpty()) { goal ->
                key(goal.id) {
                    GoalRow(goal, expanded = goal.id == lastAddedId)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun GoalRow(
    rawGoal: ActivityGoal,
    expanded: Boolean,
    model: GoalItemViewModel = viewModel("goalRow${rawGoal.id}",
        GoalItemViewModelFactory(rawGoal, expanded, LocalLifecycleOwner.current)),
) {
    val goal by model.snapshot.observeAsState()
    val preferences by model.preferences.observeAsState()
    val typeGroups = model.groupedTypes.observeAsState()
    val goalWeekDaysString by model.goalWeekDaysString.observeAsState()
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showActivityTypeDialog by rememberSaveable { mutableStateOf(false) }
    val expanded by model.expanded.observeAsState()
    var deleted by remember { mutableStateOf(false) }

    if(showLanguageDialog) {
        LanguageSelectDialog(
            onItemClick = {
                model.onLanguageChange(it.code)
                showLanguageDialog = false
            },
            onDismissRequest = { showLanguageDialog = false },
            preferences = preferences)
    }

    if(showActivityTypeDialog) {
        ActivityTypeSelectDialog(
            groups = typeGroups,
            onItemClick = {
                model.onTypeChange(it)
                showActivityTypeDialog = false
            },
            onAddTypeClick =  {
                model.addActivityType(it)
            },
            onDismissRequest = { showActivityTypeDialog = false })
    }
    goal?.let {

        AnimatedVisibility(goal != null && !deleted, exit = shrinkVertically() + fadeOut()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(onClick = { model.setExpanded(!expanded!!) }),
                elevation = 2.dp)
            {
                Column {
                    ListItem(
                        icon = {
                            ActivityRowIcon(goal!!.activityType?.category?.icon,
                                goal!!.activityType?.category?.color)
                        },
                        text = {
                            Text(goal!!.activityType?.name.toString(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                        },
                        secondaryText = {
                            Text(getLanguageDisplayName(goal!!.language),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                        },
                        trailing = {
                            Switch(checked = goal!!.active,
                                onCheckedChange = { model.onActiveChange(it) })
                        }
                    )

                    AnimatedVisibility(visible = !expanded!!) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), Arrangement.SpaceBetween) {
                            Text(goalWeekDaysString.orEmpty(), style = MaterialTheme.typography.body2)
                            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null)
                        }
                    }

                    AnimatedVisibility(visible = expanded!!) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            DropDownTextField(label = { Text("Activity") },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = "${goal!!.activityType?.category?.title} : ${goal!!.activityType?.name}",
                                onClick = { showActivityTypeDialog = true })

                            Spacer(modifier = Modifier.size(8.dp))
                            DropDownTextField(label = { Text("Language") },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                value = getLanguageDisplayName(goal!!.language),
                                onClick = { showLanguageDialog = true })
                            Spacer(modifier = Modifier.size(16.dp))
                            ApplyTextStyle(textStyle = MaterialTheme.typography.caption, contentAlpha = ContentAlpha.medium) {
                                Text("Week days")
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                            WeekDaysSelector(weekDays = goal!!.weekDays.toIntArray()) {
                                model.toggleWeekDay(it)
                            }
                            Spacer(modifier = Modifier.size(16.dp))
                            Divider()
                            Spacer(modifier = Modifier.size(8.dp))
                            Row(modifier = Modifier
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { model.removeGoal(); deleted = true }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error)) {
                                    Icon(Icons.Filled.DeleteForever, contentDescription = null)
                                    Text(text = "Delete")
                                }
                                Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeekDaysSelector(weekDays: IntArray, onSelect: (Int) -> Unit) {
    val dayLetters = remember {
        IntRange(1, 7).map { DayOfWeek.of(it).toString().substring(0,1) }
    }

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        for (d in 1 until 8) {
            ToggleButton(onClick = { onSelect(d) }, selected = weekDays.contains(d), highlighted = true, round = true) {
                Text(dayLetters[d-1])
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun FeedGoalRow(
    rawGoal: ActivityGoal,
    model: GoalItemViewModel = viewModel("goalRow${rawGoal.id}",
        GoalItemViewModelFactory(rawGoal, false, LocalLifecycleOwner.current)),
) {
    val goal by model.snapshot.observeAsState()
    var done by remember { mutableStateOf(false) }

    goal?.let {
        AnimatedVisibility(goal != null && !done, exit = shrinkVertically() + fadeOut()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            {
                Column {
                    ListItem(
                        modifier = Modifier.clickable(onClick = { done = true }),
                        icon = {
                            Icon(Icons.Rounded.RadioButtonUnchecked, modifier = Modifier.size(42.dp), tint = MaterialTheme.colors.onSurface.copy(ContentAlpha.disabled), contentDescription = null)
                        },
                        text = {
                            Text("${goal!!.activityType?.category?.title} : ${goal!!.activityType?.name}",
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