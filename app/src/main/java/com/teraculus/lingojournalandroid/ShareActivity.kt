package com.teraculus.lingojournalandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import com.teraculus.lingojournalandroid.data.Repository
import com.teraculus.lingojournalandroid.ui.LingoTheme
import com.teraculus.lingojournalandroid.ui.goals.GoalRow
import com.teraculus.lingojournalandroid.utils.ApplyTextStyle
import com.teraculus.lingojournalandroid.utils.LocalSysUiController
import com.teraculus.lingojournalandroid.utils.SystemUiController
import com.teraculus.lingojournalandroid.utils.shareImage

private const val KEY_ARG_SHARE_ACTIVITY_ID = "KEY_ARG_EDITOR_ACTIVITY_ID"
private const val KEY_ARG_SHARE_GOAL_ACTIVITY_ID = "KEY_ARG_EDITOR_FROM_GOAL_ACTIVITY_ID"

fun launchShareActivity(context: Context, activityId: String? = null, goalId: String? = null) {
    context.startActivity(createShareActivityIntent(context, activityId, goalId))
}

// Used when user creates a new activity and we want to get a result pack to show an ad
fun launchShareActivity(resultLauncher: ActivityResultLauncher<Intent>, context: Activity) {
    resultLauncher.launch(createShareActivityIntent(context, null, null))
}

fun createShareActivityIntent(context: Context, activityId: String?, goalId: String?): Intent {
    val intent = Intent(context, ShareActivity::class.java)
    intent.putExtra(KEY_ARG_SHARE_ACTIVITY_ID, activityId)
    intent.putExtra(KEY_ARG_SHARE_GOAL_ACTIVITY_ID, goalId)
    return intent
}

data class ShareActivityArg(
    val activityId: String?,
    val goalId: String?
)

class ShareActivity : AppCompatActivity() {
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = getShareArgs(intent)

        PickerProvider.getPickerProvider().fragmentManagerProvider = { supportFragmentManager }

        var composeView: ComposeView? = null

        if (args.goalId != null) {
            val goalLiveData = Repository.getRepository().goals.get(args.goalId)
            composeView = ComposeView(this).apply {
                setContent {
                    LingoTheme {
                        Column() {
                            val goal by goalLiveData.observeAsState()
                            goal?.let { GoalRow(it, modifier = Modifier.padding(0.dp), noButtons = true) }
                            Logo()
                        }
                    }
                }
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        } else if (args.activityId != null) {
            composeView = ComposeView(this).apply {
                setContent {
                    LingoTheme {
                        Card() {
                            Text(text = "Activity")
                        }
                    }
                }
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

        setContent {
            val systemUiController = remember { SystemUiController(window) }
            CompositionLocalProvider(LocalSysUiController provides systemUiController) {
                LingoTheme {
                    ShareContent(composeView)
                }
            }
        }
    }

    @Composable
    private fun ShareContent(composeView: ComposeView?) {
        val scrollState = rememberScrollState()
        val context = LocalContext.current as Activity
        var bitmap: Bitmap? by remember { mutableStateOf(null) }
        var widthSpecs: Int? by remember { mutableStateOf(null) }
        var heightSpecs: Int? by remember { mutableStateOf(null) }
        Scaffold(
            topBar = {
                val elevation =
                    if (MaterialTheme.colors.isLight && (scrollState.value > 0)) AppBarDefaults.TopAppBarElevation else 0.dp
                TopAppBar(
                    title = { Text(text = "Share") },
                    backgroundColor = MaterialTheme.colors.background,
                    elevation = elevation,
                    navigationIcon = {
                        IconButton(onClick = { onBackPressed() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                )
            },
            bottomBar = {
                Button(
                    onClick = {
                        composeView?.let {
                            if (widthSpecs != null && heightSpecs != null) {
                                with(composeView) {
                                    measure(widthSpecs!!, heightSpecs!!)
                                    layout(
                                        0, 0,
                                        measuredWidth,
                                        measuredHeight
                                    )
                                }
                                if (composeView.isLaidOut) {
                                    shareImage(
                                        context,
                                        composeView.drawToBitmap()
                                    )
                                }
                            }
                        }
                    },
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Share")
                }
            }
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.Center
            ) {

                composeView?.let {
                    BoxWithConstraints(contentAlignment = Alignment.Center) {

                        AndroidView(factory = { composeView }) {
                        }

                        val widthPx =
                            with(LocalDensity.current) { maxWidth.toPx().toInt() }
                        val heightPx =
                            with(LocalDensity.current) { maxHeight.toPx().toInt() }

                        SideEffect {
                            if (bitmap == null) {
                                widthSpecs = View.MeasureSpec.makeMeasureSpec(
                                    widthPx,
                                    View.MeasureSpec.AT_MOST
                                )
                                heightSpecs = View.MeasureSpec.makeMeasureSpec(
                                    heightPx,
                                    View.MeasureSpec.AT_MOST
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onBackPressed() {
        if (isTaskRoot) {
            val parentIntent = Intent(this, MainActivity::class.java)
            startActivity(parentIntent)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun getShareArgs(intent: Intent): ShareActivityArg {
        val id = intent.getStringExtra(KEY_ARG_SHARE_ACTIVITY_ID)
        val goalId = intent.getStringExtra(KEY_ARG_SHARE_GOAL_ACTIVITY_ID)
        return ShareActivityArg(id, goalId)
    }

    @Composable
    private fun Logo(modifier: Modifier = Modifier.padding(end = 8.dp, top = 8.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(id = R.drawable.ic_welcome_icon),
                contentDescription = null,
                modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(4.dp))
            ApplyTextStyle(textStyle = MaterialTheme.typography.caption, contentAlpha = ContentAlpha.medium) {
                Text(text = "Lingo Journal for Android", fontWeight = FontWeight.Bold)
            }
        }
    }
}