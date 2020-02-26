package com.example.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.presentation.R
import com.jackpocket.scratchoff.ScratchoffController
import com.osy.util.dpToPx
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_scratch.*
import java.util.concurrent.TimeUnit


class ScratchFragment : Fragment() {
    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scratch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var controller: ScratchoffController? = null
        controller = ScratchoffController(requireContext())
            .setThresholdPercent(0.70)
            .setFadeOnClear(true)
            .setClearOnThresholdReached(true)
            .setTouchRadiusDip(requireContext(), 4.dpToPx())
            .setCompletionCallback {
                Observable.timer(5000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe { controller?.reset() }.let {
                        disposables.add(it)
                    }
            }
            .attach(scratchView, liveCoupon)
    }
}