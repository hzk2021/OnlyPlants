package com.nyp.sit.aws.project.onlyplants

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nyp.sit.aws.project.onlyplants.Model.sttService
import kotlinx.android.synthetic.main.activity_sttactivity.*
import java.io.File

class STTActivity : AppCompatActivity() {

    private val RECORD_REQUEST_CODE = 101
    private val STORAGE_REQUEST_CODE = 102

    private var STT: sttService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sttactivity)

        STT = sttService(applicationContext, applicationContext.cacheDir)

        micBtn.setOnClickListener {
            val dialog = STTDialogFragment()

            dialog.show(supportFragmentManager, "sttDialogFragment")
        }

        audioSetup()
    }

    fun audioSetup() {

        micBtn.isEnabled = STT?.hasMicrophone() ?: false

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

}