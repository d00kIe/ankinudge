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
        Activity("Dummy title", text, activityTypeData()[0], 45, 50),
        Activity("Dummy title", text, activityTypeData()[1], 25, 10),
        Activity("Dummy title", text, activityTypeData()[2], 5, 90),
        Activity("Dummy title", text, activityTypeData()[3], 25, 51),
        Activity("Dummy title", text, activityTypeData()[4], 45, 53),
        Activity("Dummy title", text, activityTypeData()[5], 45, 100),
        Activity("Dummy title", text, activityTypeData()[6], 35, 2),
        Activity("Dummy title", text, activityTypeData()[7], 25, 60),
        Activity("Dummy title", text, activityTypeData()[8], 15, 30),
        Activity("Dummy title", text, activityTypeData()[9], 65, 58),
    )
}