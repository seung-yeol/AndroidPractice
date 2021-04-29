package com.example.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment

class MainNavHostFragment : NavHostFragment() {
    private var fragmentLifecycleCallbacks: FragmentManager.FragmentLifecycleCallbacks? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = FrameLayout(inflater.context)
        layout.id = id


        var fragmentLifecycleCallbacks = this.fragmentLifecycleCallbacks
        if (fragmentLifecycleCallbacks != null) {
            childFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
            this.fragmentLifecycleCallbacks = null
        }
        fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
                super.onFragmentAttached(fm, f, context)
            }
        }
        childFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false)
        this.fragmentLifecycleCallbacks = fragmentLifecycleCallbacks

        return layout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val fragmentLifecycleCallbacks = this.fragmentLifecycleCallbacks
        if (fragmentLifecycleCallbacks != null) {
            childFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
            this.fragmentLifecycleCallbacks = null
        }
    }
}