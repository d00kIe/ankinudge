package com.teraculus.lingojournalandroid.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*

class TransformedLiveData<Source, Output>(
    private val scope: CoroutineScope ,
    private val source: LiveData<Source>,
    private val transformation: (Source?) -> Output?)
    : LiveData<Output>() {
    private var job: Job? = null

    private val observer = Observer<Source> { source ->
        job?.cancel()
        job = scope.launch {
            transformation(source)?.let { transformed ->
                // Could have used postValue instead, but using the UI context I can guarantee that
                // a canceled job will never emit values.
                //withContext(Contacts.Intents.UI) {
                //  value = transformed
                //}

                postValue(transformed)
            }
        }
    }

    override fun onActive() {
        source.observeForever(observer)
    }

    override fun onInactive() {
        job?.cancel()
        source.removeObserver(observer)
    }
}

fun <Source, Output> LiveData<Source>.transform(
    scope: CoroutineScope = GlobalScope,
    transformation: (Source?) -> Output?) = TransformedLiveData(scope, this, transformation)