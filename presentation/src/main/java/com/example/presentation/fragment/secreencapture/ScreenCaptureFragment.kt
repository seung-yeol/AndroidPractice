package com.example.presentation.fragment.secreencapture

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.presentation.LifecycleCompositeDisposable
import com.example.presentation.R
import com.example.presentation.addTo
import com.gun0912.tedpermission.TedPermissionResult
import com.tedpark.tedpermission.rx2.TedRx2Permission
import kotlinx.android.synthetic.main.fragment_screencapture.*

class ScreenCaptureFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_screencapture, container, false).rootView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()
    }

    private val viewLifecycleDisposables by lazy { LifecycleCompositeDisposable(this, viewLifecycleOwnerLiveData) }

    private val mpm: MediaProjectionManager by lazy { requireContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager }

    private fun init() {
        screenshot.setOnClickListener {
            TedRx2Permission.with(requireContext())
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setDeniedMessage("권한이 필요합니다.")
                .setDeniedCloseButtonText("닫기")
                .setGotoSettingButtonText("설정")
                .request()
                .filter(TedPermissionResult::isGranted)
                .subscribe { startActivityForResult(mpm.createScreenCaptureIntent(), 123) }
                .addTo(viewLifecycleDisposables)
        }

        /*LocalBroadcastManager.getInstance(requireContext()).registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val result = intent.getBooleanExtra(ScreenCaptureService.IS_SCREEN_CAPTURE_SUCCESS, false)

                Toast.makeText(requireContext(), if (result) "스크린샷을 저장하였습니다." else "스크린샷을 찍지 못하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }, IntentFilter(ScreenCaptureService.SCREEN_CAPTURE_RESULT_ACTION))*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*println("osy onActivityResult requestCode : $requestCode,  resultCode : $resultCode,   data : $data")
        if (requestCode == 123) {
            if (resultCode != Activity.RESULT_OK) {
                println("osy resultCode != RESULT_OK       $resultCode")
            } else {
                println("osy startService")
                requireActivity().startService(ScreenCaptureService.getStartIntent(requireContext(), resultCode, data))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)*/
    }
}