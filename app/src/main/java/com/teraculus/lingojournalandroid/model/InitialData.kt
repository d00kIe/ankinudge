package com.teraculus.lingojournalandroid.model

import java.time.LocalDate

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
    val text = "This is a dummy note. This is a dummy note. This is a dummy note. This is a dummy note."
    return listOf(
        Activity("Dummy title", text, "de", activityTypes[0], 05f, 50f, LocalDate.now().minusDays(1)),
        Activity("Dummy title", text, "en", activityTypes[1], 25f, 0f, LocalDate.now().minusDays(2)),
        Activity("Dummy title", text, "de", activityTypes[2], 0f, 100f, LocalDate.now().minusDays(3)),
        Activity("Dummy title", text, "en", activityTypes[3], 25f, 50f, LocalDate.now().minusDays(3)),
        Activity("Dummy title", text, "de", activityTypes[4], 50f, 50f, LocalDate.now().minusDays(4)),
        Activity("Dummy title", text, "en", activityTypes[5], 50f, 100f, LocalDate.now().minusDays(4)),
        Activity("Dummy title", text, "en", activityTypes[6], 25f, 0f, LocalDate.now().minusDays(4)),
        Activity("Dummy title", text, "de", activityTypes[7], 25f, 50f, LocalDate.now().minusDays(5)),
        Activity("Dummy title", text, "de", activityTypes[8], 25f, 50f, LocalDate.now().minusDays(5)),
        Activity("Dummy title", text, "de", activityTypes[9], 75f, 50f, LocalDate.now().minusDays(6)),
    )
}