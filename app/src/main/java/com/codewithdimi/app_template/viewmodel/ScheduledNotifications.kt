package com.codewithdimi.app_template.viewmodel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.codewithdimi.app_template.MainActivity
import com.codewithdimi.app_template.R
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random.Default.nextInt

const val TAG_OUTPUT = "AppTemplateReminderWorkTag"
const val UNIQUE_WORK_NAME = "AppTemplateReminderWork"
const val CHANNEL_ID = "AppTemplateReminderChannelID"
const val DATA_TITLE_ID = "notification_data_title_id"
const val DATA_TEXT_ID = "notification_data_text_id"
const val DATA_HOUR_ID = "notification_data_hour_id"
const val DATA_MINUTE_ID = "notification_data_minute_id"

fun scheduleNotification(ctx: Context, hour: Int, minute: Int, title: String = "How was your day?", text: String = "Track your language learning activities.", reschedule: Boolean = false) {
    val currentDate = Calendar.getInstance()
    val dueDate = Calendar.getInstance()
    dueDate.set(Calendar.HOUR_OF_DAY, hour)
    dueDate.set(Calendar.MINUTE, minute)
    dueDate.set(Calendar.SECOND, 0)
    if (dueDate.before(currentDate) || reschedule) {
        dueDate.add(Calendar.HOUR_OF_DAY, 24)
    }

    val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
    val data = Data.Builder()
    data.putString(DATA_TITLE_ID, title)
    data.putString(DATA_TEXT_ID, text)
    data.putInt(DATA_HOUR_ID, hour)
    data.putInt(DATA_MINUTE_ID, minute)
    val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyWorker>()
//        .setConstraints(constraints)
        .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
        .addTag(TAG_OUTPUT)
        .setInputData(data.build())
        .build()

    WorkManager
        .getInstance(ctx)
        .enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, dailyWorkRequest)
}

fun cancelScheduledNotification(ctx: Context) {
    WorkManager
        .getInstance(ctx)
        .cancelUniqueWork(UNIQUE_WORK_NAME)
}

class DailyWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val title = inputData.getString(DATA_TITLE_ID)
        val text = inputData.getString(DATA_TEXT_ID)
        if (title.isNullOrEmpty()
            || text.isNullOrEmpty()
            || !inputData.hasKeyWithValueOfType<Int>(DATA_HOUR_ID)
            || !inputData.hasKeyWithValueOfType<Int>(DATA_MINUTE_ID))
            return Result.failure()

        val hour = inputData.getInt(DATA_HOUR_ID, 20)
        val minute = inputData.getInt(DATA_MINUTE_ID, 0)

        // reschedule for tomorrow
        scheduleNotification(applicationContext, hour, minute, title, text, true)

        // show the notification
        createNotificationChannel(applicationContext)
        notify(applicationContext, title, text)

        return Result.success()
    }
}

private fun createNotificationChannel(ctx: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = ctx.getString(R.string.channel_name)
        val descriptionText = ctx.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun notify(ctx: Context, title: String, content: String) {
    // Create an explicit intent for an Activity in your app
    val intent = Intent(ctx, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0)

    var builder = NotificationCompat.Builder(ctx, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_stat_name)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        // Set the intent that will fire when the user taps the notification
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(ctx)) {
        // notificationId is a unique int for each notification that you must define
        notify(nextInt(), builder.build())
    }
}