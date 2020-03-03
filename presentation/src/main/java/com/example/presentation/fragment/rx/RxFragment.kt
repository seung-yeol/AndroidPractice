package com.example.presentation.fragment.rx

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.presentation.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_rx.*

class RxFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rx, container, false)
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initView()
        subscribe()
    }

    private var _numObservable = BehaviorSubject.createDefault(0)
    private var numObservable = _numObservable.publish()
    private fun initView() {
        plusButton.setOnClickListener {
            _numObservable.onNext(_numObservable.value!! + 1)
        }

        connectButton.setOnClickListener {
            numObservable.connect()
        }
    }

    private fun subscribe() {
        numObservable.subscribe {
            numberText.text = it.toString()
        }.let {
            compositeDisposable.add(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
