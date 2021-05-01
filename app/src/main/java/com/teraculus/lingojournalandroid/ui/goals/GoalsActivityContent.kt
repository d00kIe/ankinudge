package com.teraculus.lingojournalandroid.ui.goals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.model.ActivityGoal
import com.teraculus.lingojournalandroid.ui.components.ActivityRowIcon
import io.realm.RealmResults

class GoalsViewModel(val repository: Repository = Repository.getRepository()) : ViewModel() {
    val goals = repository.getActivityGoals()
    val frozen = Transformations.map(goals) { (it as RealmResults<ActivityGoal>).freeze() }
}

class GoalItemViewModel(frozenGoal: ActivityGoal, owner: LifecycleOwner) : ViewModel() {
    val goal = Repository.getRepository().getActivity(frozenGoal.id.toString())
    val snapshot =
        MutableLiveData<ActivityGoal>(if (goal.value?.isValid == true) goal.value!!.freeze<ActivityGoal>() else null)

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
    viewModel: GoalsViewModel = viewModel("goalsViewModel"),
    onDismiss: () -> Unit,
    onOpenGoalEditor: (id: String?) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val goals by viewModel.frozen.observeAsState()
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
                },
                actions = {
                    IconButton(onClick = { onOpenGoalEditor(null) }) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                    }
                }
            )
        }
    )
    {
        LazyColumn(state = scrollState) {
            items(goals.orEmpty()) { goal ->
                GoalRow(goal, onClick = { onOpenGoalEditor(it) })
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GoalRow(
    rawGoal: ActivityGoal,
    onClick: (id: String) -> Unit,
    model: GoalItemViewModel = viewModel("activityRow${rawGoal.id}",
        GoalItemViewModelFactory(rawGoal, LocalLifecycleOwner.current)),
) {
    val goal by model.snapshot.observeAsState()
    if (goal != null) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(onClick = { onClick(goal!!.id.toString()) }),
            elevation = 2.dp)
        {
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
            )
        }
    }
}