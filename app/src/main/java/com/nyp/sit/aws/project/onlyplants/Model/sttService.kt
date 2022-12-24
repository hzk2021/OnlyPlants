package com.nyp.sit.aws.project.onlyplants.Model

import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.ImageButton
import java.io.File

class sttService(context:Context, audioFilePath: File) {

//    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    private var audioFilePath: String? = null
    private var isRecording = false

    private val context: Context = context

    fun hasMicrophone(): Boolean {
        val pmanager = context.packageManager
        return pmanager.hasSystemFeature(
            PackageManager.FEATURE_MICROPHONE)
    }

    fun recordAudio(micBtn:ImageButton, mediaRecorder: MediaRecorder?) {
        isRecording = true
        micBtn.isEnabled = false

        var newAudioPath: File? = null

        try {

            val dir: File = File(audioFilePath, "searchString")
            if (dir.exists()) {
                for (f in dir.listFiles()!!) {
                    newAudioPath = f
                }
            }

//            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(
                MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecorder?.setOutputFile(newAudioPath)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecorder?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaRecorder?.start()
    }

    fun stopAudio(micBtn: ImageButton, mediaRecorder: MediaRecorder?) {

        if (isRecording) {
            micBtn.isEnabled = false
            mediaRecorder?.stop()
            mediaRecorder?.release()
            isRecording = false
        } else {
            mediaPlayer?.release()
            mediaPlayer = null
            micBtn.isEnabled = true
        }
    }

}