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
    )
}

fun activityData(): List<Activity> {
    val text = "This is a dummy note. This is a dummy note. This is a dummy note. This is a dummy note."
    return listOf(
        Activity("Dummy title", text, "de", activityTypeData()[0], 45f, 50f),
        Activity("Dummy title", text, "de", activityTypeData()[1], 25f, 10f),
        Activity("Dummy title", text, "de", activityTypeData()[2], 5f, 90f),
        Activity("Dummy title", text, "de", activityTypeData()[3], 25f, 51f),
        Activity("Dummy title", text, "de", activityTypeData()[4], 45f, 53f),
        Activity("Dummy title", text, "de", activityTypeData()[5], 45f, 100f),
        Activity("Dummy title", text, "de", activityTypeData()[6], 35f, 2f),
        Activity("Dummy title", text, "de", activityTypeData()[7], 25f, 60f),
        Activity("Dummy title", text, "de", activityTypeData()[8], 15f, 30f),
        Activity("Dummy title", text, "de", activityTypeData()[9], 65f, 58f),
    )
}