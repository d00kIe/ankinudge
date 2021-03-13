package com.teraculus.lingojournalandroid.model

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
        ActivityType(ActivityCategory.SPEAKING, "With Friends"),
        ActivityType(ActivityCategory.SPEAKING, "Ghosting"),
        ActivityType(ActivityCategory.LEARNING, "Grammar"),
        ActivityType(ActivityCategory.LEARNING, "Vocabulary"),
        ActivityType(ActivityCategory.WATCHING, "YouTube"),
        ActivityType(ActivityCategory.WATCHING, "TV"),
        ActivityType(ActivityCategory.WATCHING, "Movie"),
        ActivityType(ActivityCategory.EXAM, "Preparation"),
        ActivityType(ActivityCategory.EXAM, "Certificate"),
        ActivityType(ActivityCategory.OTHER, "Nothing, being lazy."),
    )
}

fun activityData(activityTypes: List<ActivityType>): List<Activity> {
    val text = "This is a dummy note. This is a dummy note. This is a dummy note. This is a dummy note."
    return listOf(
        Activity("Dummy title", text, "de", activityTypes[0], 05f, 50f),
        Activity("Dummy title", text, "de", activityTypes[1], 25f, 0f),
        Activity("Dummy title", text, "de", activityTypes[2], 0f, 100f),
        Activity("Dummy title", text, "de", activityTypes[3], 25f, 50f),
        Activity("Dummy title", text, "de", activityTypes[4], 50f, 50f),
        Activity("Dummy title", text, "de", activityTypes[5], 50f, 100f),
        Activity("Dummy title", text, "de", activityTypes[6], 25f, 0f),
        Activity("Dummy title", text, "de", activityTypes[7], 25f, 50f),
        Activity("Dummy title", text, "de", activityTypes[8], 25f, 50f),
        Activity("Dummy title", text, "de", activityTypes[9], 75f, 50f),
    )
}