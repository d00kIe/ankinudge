package com.teraculus.lingojournalandroid.model

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmResults


/**
 * This class represents a RealmResults wrapped inside a LiveData.
 *
 * Realm always keeps the RealmResults up-to-date whenever a change occurs on any thread.
 * When the RealmResults changes, LiveRealmResults notifies the observer.
 *
 * LiveRealmResults observes the enclosed RealmResults until the RealmResults are invalidated,
 * which occurs when the Realm instance that owns this RealmResults closes.
 *
 * @param <T> the type of the RealmModel
</T> */
class LiveRealmResults<T : RealmModel?> @MainThread constructor(results: RealmResults<T>?) :
    MutableLiveData<List<T>?>() {
    private var results: RealmResults<T>? = null

    // The listener notifies the observers whenever a change occurs.
    // This could be expanded to also return the change set in a pair.
    private val listener =
        OrderedRealmCollectionChangeListener<RealmResults<T>> { results, _ ->
            this@LiveRealmResults.setValue(
                results
            )
        }

    /**
     * Starts observing the RealmResults, if it is still valid.
     */
    override fun onActive() {
        super.onActive()
        if (this.results?.isValid == true) { // invalidated results can no longer be observed.
            if (this.results?.isLoaded == true) {
                // we should not notify observers when results aren't ready yet (async query).
                // however, synchronous query should be set explicitly.
                value = results
            }
            this.results?.addChangeListener(listener)
        }
    }

    /**
     * Stops observing the RealmResults.
     */
    override fun onInactive() {
        super.onInactive()
        if (this.results?.isValid == true) {
            this.results?.removeChangeListener(listener)
        }
    }

    /**
     * Wraps the provided managed RealmResults as a LiveData.
     *
     * The provided object should be managed, and should be valid.
     *
     */
    init {
        reset(results)
    }

    fun reset (results : RealmResults<T>?) {
        val hasActiveObservers = this.hasActiveObservers()
        if(hasActiveObservers) {
            if (this.results?.isValid == true) {
                this.results?.removeChangeListener(listener)
            }
        }
        if(results == null) {
            this.results = null
            value = null
        } else {
            require(results.isManaged) { "LiveRealmResults only supports managed RealmModel instances!" }
            require(results.isValid) { "The provided RealmResults is no longer valid because the Realm instance that owns it is closed. It can no longer be observed for changes." }
            this.results = results
            if (results.isLoaded) {
                // we should not notify observers when results aren't ready yet (async query).
                // however, synchronous query should be set explicitly.
                value = results
            }

            if(hasActiveObservers) {
                if (this.results?.isValid == true) {
                    this.results?.addChangeListener(listener)
                }
            }
        }
    }
}