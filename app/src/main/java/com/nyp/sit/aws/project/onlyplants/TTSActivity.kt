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

//        convertTTS()

        playAudioBtn.setOnClickListener {
            prepareAudio()
        }
    }

    fun convertTTS() {
        val text = "A rose is either a woody perennial flowering plant of the genus Rosa (/ˈroʊzə/),[1] in the family Rosaceae (/roʊˈzeɪsiːˌiː/),[1] or the flower it bears. There are over three hundred species and tens of thousands of cultivars.[citation needed] They form a group of plants that can be erect shrubs, climbing, or trailing, with stems that are often armed with sharp prickles.[citation needed] Their flowers vary in size and shape and are usually large and showy, in colours ranging from white through yellows and reds. Most species are native to Asia, with smaller numbers native to Europe, North America, and northwestern Africa.[citation needed] Species, cultivars and hybrids are all widely grown for their beauty and often are fragrant. Roses have acquired cultural significance in many societies. Rose plants range in size from compact, miniature roses, to climbers that can reach seven meters in height.[citation needed] Different species hybridize easily, and this has been used in the development of the wide range of garden roses."
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