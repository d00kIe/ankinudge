package com.teraculus.lingojournalandroid.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teraculus.lingojournalandroid.model.LiveRealmResults
import com.teraculus.lingojournalandroid.model.Note
import com.teraculus.lingojournalandroid.model.notesData
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmModel
import io.realm.Sort
import io.realm.kotlin.where
import org.bson.types.ObjectId
import java.util.*

fun <T : RealmModel?> MutableLiveData<List<T>?>.trigger() {
    value = value
}

class Repository {
    private var realm: Realm? = null
    private val notes: LiveData<List<Note>?>

    init {
        initializeRealm()

        val notesQuery = realm!!.where<Note>().sort("date", Sort.DESCENDING)
        notes = LiveRealmResults<Note>(notesQuery.findAll())

        if(notes.value?.isEmpty()!!) {
            realm!!.executeTransaction { tr -> tr.insert(notesData()) }
        }
    }

    private fun initializeRealm() {
        val config = RealmConfiguration.Builder()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()
    }

    fun addNote(note: Note) {
        realm!!.executeTransaction { tr -> tr.insert(note) }
    }

    fun removeNote(note: Note) {
        realm!!.executeTransaction {
            note.deleteFromRealm()
        }
    }

    fun getNoteById(id: String): Note? {
        return realm!!.where<Note>().equalTo("id", ObjectId(id)).findFirst()
    }

    fun getNotes(): LiveData<List<Note>?> {
        return notes
    }

    fun updateNote(id: String, title: String, text: String, date: Date ) {
        var note = getNoteById(id)
        note?.let {
            realm!!.executeTransaction {
                note.title = title
                note.text = text
                note.date = date
            }
        }

        (notes as MutableLiveData<List<Note>?>).trigger()
    }

    companion object {
        private  var INSTANCE: Repository? = null

        fun getRepository(): Repository {
            return synchronized(Repository::class) {
                var instance = INSTANCE ?: Repository()
                INSTANCE = instance
                instance
            }
        }
    }
}