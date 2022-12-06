package com.nyp.sit.aws.project.onlyplants

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nyp.sit.aws.project.onlyplants.Model.ttsService
import kotlinx.android.synthetic.main.activity_ttsactivity.*
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class TTSActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttsactivity)


        playAudioBtn.setOnClickListener {
            prepareAudio()
        }
    }

    fun convertTTS() {
        val text = "Hello World"
        val voice = "Joanna"
        val flower = "Roses"
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        val singleJobItem = scope.async(Dispatchers.IO) { ttsService().ConvertTTS(text, voice, flower) }

        scope.launch { singleJobItem.await() }
    }

    fun prepareAudio() {
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        val singleJobItem = scope.async(Dispatchers.IO) { ttsService().retrieveAudio("Roses") }

        scope.launch {
            val decodedBody = singleJobItem.await()

            val mp = MediaPlayer()

            withContext(Dispatchers.IO) {
                // Create temporary file
                val tempMp3 = File.createTempFile("plantAudio", "mp3", applicationContext.cacheDir)
                tempMp3.deleteOnExit()

                val fos = FileOutputStream(tempMp3)
                fos.write(decodedBody)

                mp.reset()

                val fis = FileInputStream(tempMp3)
                mp.setDataSource(fis.fd)

                mp.prepareAsync()

                // Play audio when ready
                mp.setOnPreparedListener {
                    mp.start()
                }
            }
        }
    }

}