package com.teraculus.lingojournalandroid.model

import com.teraculus.lingojournalandroid.BuildConfig
import org.bson.types.ObjectId
import java.time.LocalDate
import kotlin.random.Random

fun activityTypeData(): List<ActivityType> {
    return  listOf(
        ActivityType(ActivityCategory.READING, "Book"),
        ActivityType(ActivityCategory.READING, "Newspaper"),
        ActivityType(ActivityCategory.READING, "Homework"),
        ActivityType(ActivityCategory.WRITING, "Email"),
        ActivityType(ActivityCategory.WRITING, "Diary"),
        ActivityType(ActivityCategory.WRITING, "Homework"),
        ActivityType(ActivityCategory.LISTENING, "Audio book"),
        ActivityType(ActivityCategory.LISTENING, "Music"),
        ActivityType(ActivityCategory.LISTENING, "Radio"),
        ActivityType(ActivityCategory.LISTENING, "YouTube"),
        ActivityType(ActivityCategory.LISTENING, "TV"),
        ActivityType(ActivityCategory.LISTENING, "Movie"),
        ActivityType(ActivityCategory.SPEAKING, "With Friends"),
        ActivityType(ActivityCategory.SPEAKING, "Ghosting"),
        ActivityType(ActivityCategory.LEARNING, "Grammar"),
        ActivityType(ActivityCategory.LEARNING, "Vocabulary"),
        ActivityType(ActivityCategory.OTHER, "Preparation"),
        ActivityType(ActivityCategory.OTHER, "Certificate"),
        ActivityType(ActivityCategory.OTHER, "Nothing, being lazy."),
    )
}

fun activityData(activityTypes: List<ActivityType>): List<Activity> {

    var aLotOfActivities = mutableListOf<Activity>()

//    if(BuildConfig.DEBUG) {
//        generateRandomData(activityTypes, aLotOfActivities)
//    }

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
        "Ghosting a movie"
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
        val activity = Activity(title, text, lang, type, conf, moti, day, id = ObjectId())
        aLotOfActivities.add(activity)
        day = day.minusDays(Random.nextLong(0, 3))
    }
}