package com.teraculus.lingojournalandroid.model

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmObjectChangeListener


/**
 * This class represents a RealmObject wrapped inside a LiveData.
 *
 * The provided RealmObject must be a managed object that exists in a realm on creation.
 *
 * When the enclosing LifecycleOwner is killed, the listener is automatically unsubscribed.
 *
 * Realm keeps the managed RealmObject up-to-date whenever a change occurs on any thread.
 * When the RealmObject changes, LiveRealmObject notifies the observer.
 *
 * LiveRealmObject observes the object until it is invalidated. You can invalidate the RealmObject by
 * deleting it or by closing the realm that owns it.
 *
 * @param <T> the type of the RealmModel
</T> */
class LiveRealmObject<T : RealmModel?> @MainThread constructor(obj: T?) : MutableLiveData<T>(obj) {

    private var lastHashBeforeGettingInactive: Int = 0
    private val listener =
        RealmObjectChangeListener<T> { obj, objectChangeSet ->
            if (!objectChangeSet!!.isDeleted) {
                updateValue(obj)
            } else { // Because invalidated objects are unsafe to set in LiveData, pass null instead.
                setValue(null)
            }
        }

    /**
     * Starts observing the RealmObject if we have observers and the object is still valid.
     */
    override fun onActive() {
        super.onActive()
        val obj = value
        if (obj != null && RealmObject.isValid(obj)) {
            if(obj.toString().hashCode() != lastHashBeforeGettingInactive) {
                updateValue(obj) // something changed, trigger an update
            }
            RealmObject.addChangeListener(obj, listener)
        }
    }

    /**
     * Stops observing the RealmObject.
     */
    override fun onInactive() {
        super.onInactive()
        val obj = value
        if (obj != null && RealmObject.isValid(obj)) {
            lastHashBeforeGettingInactive = obj.toString().hashCode()
            RealmObject.removeChangeListener(obj, listener)
        }
    }


    fun updateValue(obj: T?) {
        try {
            value = obj
        } catch (e: Exception) {
            postValue(obj)
        }
    }
    //var value : T? = obj
}