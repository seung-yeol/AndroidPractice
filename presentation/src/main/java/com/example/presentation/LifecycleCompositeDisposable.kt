package com.example.presentation

import androidx.lifecycle.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.disposables.DisposableContainer

fun Disposable.addTo(disposables: DisposableContainer): Disposable = apply { disposables.add(this) }

class LifecycleCompositeDisposable() : Disposable, DisposableContainer {
    private var internalDisposables: CompositeDisposable? = null

    private val lifecycleEventObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                disposeInternalDisposablesIf()
                source.lifecycle.removeObserver(this)
            }
        }
    }


    private fun disposeInternalDisposablesIf() {
        if (internalDisposables?.isDisposed == false) {
            internalDisposables?.dispose()
            internalDisposables = null
        }
    }

    private fun createInternalDisposablesIf() {
        if (internalDisposables == null || internalDisposables?.isDisposed == true) {
            internalDisposables = CompositeDisposable()
        }
    }

    constructor(lifecycleOwner: LifecycleOwner, viewLifecycleOwnerLiveData: LiveData<LifecycleOwner>) : this() {
        viewLifecycleOwnerLiveData.observe(lifecycleOwner, Observer { viewLifecycleOwner ->
            viewLifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)
        })
    }

    constructor(lifecycleOwner: LifecycleOwner) : this() {
        lifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)
    }

    override fun add(d: Disposable): Boolean {
        createInternalDisposablesIf()
        return internalDisposables?.add(d) ?: false
    }

    override fun remove(d: Disposable): Boolean {
        return internalDisposables?.remove(d) ?: false
    }

    override fun delete(d: Disposable): Boolean {
        return internalDisposables?.delete(d) ?: false
    }

    override fun isDisposed(): Boolean {
        return internalDisposables?.isDisposed ?: true
    }

    override fun dispose() {
        internalDisposables?.dispose()
    }
}