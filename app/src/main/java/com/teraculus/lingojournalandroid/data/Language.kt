package com.teraculus.lingojournalandroid.data

import java.util.*

val LanguageCodes = listOf("af","ach","ak","am","ar","az","be","bem","bg","bn","br","bs","ca","chr","ckb","co","crs","cs","cy","da","de","ee","el","en","eo","es","et","eu","fa","fi","fo","fr","fy","ga","gaa","gd","gl","gn","gu","ha","haw","hi","hr","ht","hu","hy","ia","id","ig","is","it","iw","ja","jw","ka","kg","kk","km","kn","ko","kri","ku","ky","la","lg","ln","lo","loz","lt","lua","lv","mfe","mg","mi","mk","ml","mn","mr","ms","mt","ne","nl","nn","no","nso","ny","nyn","oc","om","or","pa","pcm","pl","ps","pt","qu","rm","rn","ro","ru","rw","sd","sh","si","sk","sl","sn","so","sq","sr","st","su","sv","sw","ta","te","tg","th","ti","tk","tl","tn","to","tr","tt","tum","tw","ug","uk","ur","uz","vi","wo","xh","yi","yo","zh","zu")

class Language(val name: String, val code: String)

fun getAllLanguages(): List<Language> {
    return LanguageCodes.map { Language(Locale(it).displayLanguage, it) }.sortedBy { it.name }
}

fun getLanguageDisplayName(code: String): String {
    if (code.isEmpty())
        return ""

    return Locale(code).displayLanguage
}