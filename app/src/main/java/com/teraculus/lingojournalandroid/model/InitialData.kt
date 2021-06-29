package com.teraculus.lingojournalandroid.model

//import com.teraculus.lingojournalandroid.BuildConfig
import com.teraculus.lingojournalandroid.BuildConfig
import org.bson.types.ObjectId
import java.time.LocalDate
import kotlin.random.Random

fun activityTypeData(): List<ActivityType> {
    return  listOf(
        ActivityType(ActivityCategory.READING, "Book", unit = MeasurementUnit.Pages),
        ActivityType(ActivityCategory.READING, "Newspaper",),
        ActivityType(ActivityCategory.READING, "Homework"),
        ActivityType(ActivityCategory.WRITING, "Email"),
        ActivityType(ActivityCategory.WRITING, "Diary"),
        ActivityType(ActivityCategory.WRITING, "Homework"),
        ActivityType(ActivityCategory.LISTENING, "Audio book"),
        ActivityType(ActivityCategory.LISTENING, "YouTube", unit = MeasurementUnit.Videos),
        ActivityType(ActivityCategory.LISTENING, "Movie"),
        ActivityType(ActivityCategory.SPEAKING, "With Friends"),
        ActivityType(ActivityCategory.SPEAKING, "On the phone"),
        ActivityType(ActivityCategory.LEARNING, "Grammar"),
        ActivityType(ActivityCategory.LEARNING, "Vocabulary", unit = MeasurementUnit.Words),
        ActivityType(ActivityCategory.OTHER, "App - Anki", unit = MeasurementUnit.Words),
        ActivityType(ActivityCategory.OTHER, "Exam"),
    )
}

fun activityData(activityTypes: List<ActivityType>): List<Activity> {

    val aLotOfActivities = mutableListOf<Activity>()

    if(BuildConfig.DEBUG) {
        generateRandomData(activityTypes, aLotOfActivities)
    }

    return aLotOfActivities
}

private fun generateRandomData(
    activityTypes: List<ActivityType>,
    aLotOfActivities: MutableList<Activity>,
) {
    val text =
        "This is a dummy note. This is a dummy note. This is a dummy note. This is a dummy note."
    val titles = listOf(
        "My neighbor is annoying",
        "Discovery channel documentary",
        "Podcast",
        "Sherlock Holmes",
        "Chatting with Mia",
        "Interesting article",
        "Book review",
        "Watching a movie"
    )

    val langs = listOf("en", "de", "en", "de", "en", "de", "en", "de", "en", "de", "en", "de")
    val moods = listOf(0f, 25f, 50f, 75f, 100f)

    var day = LocalDate.now()
    for (a in 0 until 2000) {
        val title = titles[Random.nextInt(titles.size - 1)]
        val lang = langs[Random.nextInt(langs.size - 1)]
        val type = activityTypes[Random.nextInt(activityTypes.size - 1)]
        val conf = moods[Random.nextInt(moods.size - 1)]
        val moti = moods[Random.nextInt(moods.size - 1)]
        val activity = Activity(title, text, lang, type,Random.nextInt(1,10).toFloat(), conf, moti,  day, id = ObjectId(), duration = Random.nextInt(5, 120))
        aLotOfActivities.add(activity)
        day = day.minusDays(Random.nextLong(0, 3))
    }
}