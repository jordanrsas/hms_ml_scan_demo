package com.dtse.demo.ml.ui.scan

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dtse.demo.ml.R
import com.dtse.demo.ml.ui.asr.AsrFragment
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzer
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions

class ScanFragment : Fragment() {

    private lateinit var scanResultTextView: TextView
    private lateinit var scanButton: Button

    companion object {
        private const val CAMERA_REQUEST = 200
        private const val SCAN_REQUEST = 300
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_scan, container, false)
        scanResultTextView = root.findViewById(R.id.scanResultTextView)
        scanButton = root.findViewById(R.id.scanButton)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scanButton.setOnClickListener {
            onScanRequest()
        }
    }

    private fun onScanRequest() {
        if (checkCameraPermissions()) {
            startScanning()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), CAMERA_REQUEST
            )
        }
    }

    private fun startScanning() {
        val scanOptions: HmsScanAnalyzerOptions = HmsScanAnalyzerOptions
            .Creator()
            .setHmsScanTypes(HmsScan.ALL_SCAN_TYPE)
            .create()
        ScanUtil.startScan(activity, SCAN_REQUEST, scanOptions)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }

        if (requestCode == SCAN_REQUEST) {
            val response = data.getParcelableExtra<HmsScan>(ScanUtil.RESULT)
            scanResultTextView.text = response?.getOriginalValue()
        }
    }

    private fun checkCameraPermissions(): Boolean {
        return context?.let {
            val camera = ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA)
            val storage =
                ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE)
            camera == PackageManager.PERMISSION_GRANTED && storage == PackageManager.PERMISSION_GRANTED
        } ?: false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST) {
            if (checkCameraPermissions()) {
                startScanning()
            } else {
                Toast.makeText(context, R.string.permission_denied_message, Toast.LENGTH_SHORT)
                    .show();
            }
        }
    }
}