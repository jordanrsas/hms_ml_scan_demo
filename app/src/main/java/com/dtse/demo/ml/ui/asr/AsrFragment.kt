package com.dtse.demo.ml.ui.asr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dtse.demo.ml.R
import com.huawei.hms.mlsdk.asr.MLAsrConstants
import com.huawei.hms.mlsdk.asr.MLAsrListener
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer

class AsrFragment : Fragment(), MLAsrListener {

    companion object {
        private const val RECORD_CODE = 100
        private const val TAG = "ASR"
    }

    private lateinit var recordBtn: Button

    private lateinit var mSpeechRecognizer: MLAsrRecognizer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_asr, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordBtn = view.findViewById<Button>(R.id.recordBtn)
        recordBtn.setOnClickListener {
            initASR()
        }
    }

    private fun initASR() {
        if (checkMicPermission()) {
            startRecording()
        } else {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_CODE)
        }
    }

    private fun startRecording() {
        recordBtn.setOnClickListener(null);
        recordBtn.text = "Grabando"
        mSpeechRecognizer = MLAsrRecognizer.createAsrRecognizer(context)
        mSpeechRecognizer.apply {
            setAsrListener(this@AsrFragment)
            // Set the language that can be recognized to English. If this parameter is not set, English is recognized by default.
            // Example: "zh-CN": Chinese; "en-US": English; "fr-FR": French; "es-ES": Spanish; "de-DE": German; "it-IT": Italian
            val mSpeechRecognizerIntent = Intent(MLAsrConstants.ACTION_HMS_ASR_SPEECH).apply {
                putExtra(
                    MLAsrConstants.LANGUAGE,
                    "es-ES"
                )
                putExtra(
                    MLAsrConstants.FEATURE,
                    MLAsrConstants.FEATURE_WORDFLUX
                )
            }
            startRecognizing(mSpeechRecognizerIntent)
        }
    }

    private fun checkMicPermission(): Boolean {
        return context?.let {
            val mic = ContextCompat.checkSelfPermission(it, Manifest.permission.RECORD_AUDIO)
            mic == PackageManager.PERMISSION_GRANTED
        } ?: false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (checkMicPermission()) {
            startRecording()
        } else {
            Toast.makeText(context, R.string.permission_denied_message, Toast.LENGTH_SHORT).show();
        }
    }

    private fun releaseResources() {
        recordBtn.setText(R.string.record_button)
        recordBtn.setOnLongClickListener { initASR(); true }
        mSpeechRecognizer.apply {
            destroy()
        }
    }

    override fun onStartingOfSpeech() {

    }

    override fun onRecognizingResults(result: Bundle?) {
        val resultTextView = view?.findViewById<TextView>(R.id.result)
        resultTextView?.text = ""
        result?.let {
            for (key: String in it.keySet()) {
                resultTextView?.append(it.getString(key, ""))
            }
        }
    }

    override fun onStartListening() {
        Toast.makeText(context, R.string.talk_now_message, Toast.LENGTH_SHORT).show()
    }

    override fun onState(p0: Int, p1: Bundle?) {

    }

    override fun onError(p0: Int, error: String?) {
        error?.let {
            Log.e(TAG, it)
        }
    }

    override fun onVoiceDataReceived(p0: ByteArray?, p1: Float, p2: Bundle?) {

    }

    override fun onResults(p0: Bundle?) {
        releaseResources()
    }
}