package com.teraculus.lingojournalandroid.data

import java.util.*

class Language(val name: String, val code: String) {
}

fun getAllLanguages() : List<Language> {
    return Locale.getISOLanguages().map { Language(Locale(it).displayLanguage, it) }
}
