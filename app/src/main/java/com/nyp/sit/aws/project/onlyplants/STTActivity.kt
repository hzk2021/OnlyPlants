package com.nyp.sit.aws.project.onlyplants

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nyp.sit.aws.project.onlyplants.Model.sttService
import kotlinx.android.synthetic.main.activity_sttactivity.*
import java.io.File
import java.util.*

class STTActivity : AppCompatActivity() {

    private val RECORD_REQUEST_CODE = 101
    private val STORAGE_REQUEST_CODE = 102

    private var STT: sttService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sttactivity)

        STT = sttService(applicationContext, applicationContext.cacheDir)

        micBtn.setOnClickListener {
            transcribe()
//            val dialog = STTDialogFragment()
//
//            dialog.show(supportFragmentManager, "sttDialogFragment")
        }

//        audioSetup()
    }

    fun audioSetup() {

//        micBtn.isEnabled = STT?.hasMicrophone() ?: false

        val tempMp3 = File.createTempFile("searchString", "3gp", applicationContext.cacheDir)
        tempMp3.deleteOnExit()

//        audioFilePath = Environment.getExternalStorageDirectory()
//            .absolutePath + "/myaudio.3gp"

        requestPermission(
            Manifest.permission.RECORD_AUDIO,
            RECORD_REQUEST_CODE)
    }

    private fun requestPermission(permissionType: String, requestCode: Int) {
        val permission = ContextCompat.checkSelfPermission(applicationContext,
            permissionType)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(permissionType), requestCode
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0]
                    != PackageManager.PERMISSION_GRANTED) {

                    micBtn.isEnabled = false

                    Toast.makeText(this,
                        "Record permission required",
                        Toast.LENGTH_LONG).show()
                } else {
                    requestPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        STORAGE_REQUEST_CODE)
                }
                return
            }
            STORAGE_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0]
                    != PackageManager.PERMISSION_GRANTED) {
                    micBtn.isEnabled = false
                    Toast.makeText(this,
                        "External Storage permission required",
                        Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    // Function to convert speech to text
    fun transcribe() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text")

//        val activity: STTActivity = activity as STTActivity

        try
        {
            startActivityForResult(intent, 10)
        }
        catch (ex: Exception)
        {
            Toast.makeText(this,"Your Device Doesn't Support It", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            10 -> if (resultCode == Activity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                searchString.setText(result!![0])
            }
        }
    }
}