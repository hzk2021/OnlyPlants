package com.nyp.sit.aws.project.onlyplants

import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nyp.sit.aws.project.onlyplants.Model.networkService
import com.nyp.sit.aws.project.onlyplants.Model.ttsService
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class TTSActivity : AppCompatActivity() {

    private var mp: MediaPlayer? = null
    private var retrieveState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttsactivity)
        }

    override fun onStop() {

        if (mp?.isPlaying == true) {
            mp?.release()
            mp = null
            retrieveState = false
        }

        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.play_audio_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.playAudioBtn -> prepareAudio()
            R.id.stopAudioBtn -> {
                mp?.release()
                mp = null
                invalidateOptionsMenu()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        runOnUiThread {
            if (mp?.isPlaying == true) {
                menu?.findItem(R.id.playAudioBtn)?.isEnabled = false
                menu?.findItem(R.id.playAudioBtn)?.isVisible = false
                menu?.findItem(R.id.stopAudioBtn)?.isEnabled = true
                menu?.findItem(R.id.stopAudioBtn)?.isVisible = true
            }
            else if (mp?.isPlaying == false && !retrieveState) {
                menu?.findItem(R.id.playAudioBtn)?.isEnabled = true
                menu?.findItem(R.id.playAudioBtn)?.isVisible = true
                menu?.findItem(R.id.stopAudioBtn)?.isEnabled = false
                menu?.findItem(R.id.stopAudioBtn)?.isVisible = false
            }
            else if (retrieveState) {
                menu?.findItem(R.id.playAudioBtn)?.isEnabled = false
                menu?.findItem(R.id.playAudioBtn)?.isVisible = false
                menu?.findItem(R.id.stopAudioBtn)?.isEnabled = false
                menu?.findItem(R.id.stopAudioBtn)?.isVisible = true
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    private fun convertTTS() {
        val text = "A rose is either a woody perennial flowering plant of the genus Rosa (/ˈroʊzə/),[1] in the family Rosaceae (/roʊˈzeɪsiːˌiː/),[1] or the flower it bears. There are over three hundred species and tens of thousands of cultivars.[citation needed] They form a group of plants that can be erect shrubs, climbing, or trailing, with stems that are often armed with sharp prickles.[citation needed] Their flowers vary in size and shape and are usually large and showy, in colours ranging from white through yellows and reds. Most species are native to Asia, with smaller numbers native to Europe, North America, and northwestern Africa.[citation needed] Species, cultivars and hybrids are all widely grown for their beauty and often are fragrant. Roses have acquired cultural significance in many societies. Rose plants range in size from compact, miniature roses, to climbers that can reach seven meters in height.[citation needed] Different species hybridize easily, and this has been used in the development of the wide range of garden roses."
        val flower = "Roses"
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        val singleJobItem = scope.async(Dispatchers.IO) { ttsService().convertTTS(text, flower) }

        scope.launch { singleJobItem.await() }
    }

    private fun prepareAudio() {

        if (!networkService().isOnline(this)) {
            displayToast("No network connection. Unable to retrieve audio")
            return
        }

        if (mp?.isPlaying == true) {
            displayToast("Audio is already playing")
            return
        }


        if (retrieveState) {
            displayToast("Currently retrieving audio")
            return
        }

        retrieveState = true
        invalidateOptionsMenu()
        displayToast("Retrieving audio")
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        val singleJobItem = scope.async(Dispatchers.IO) { ttsService().retrieveAudio("test") }

        scope.launch {
            val decodedBody: ByteArray? = singleJobItem.await()

            if (decodedBody == null) {
                runOnUiThread {
                    displayToast("Failed to retrieve audio")
                    retrieveState = false
                    invalidateOptionsMenu()
                }
            }
            else {
                mp = MediaPlayer()

                withContext(Dispatchers.IO) {
                    // Create temporary file
                    val tempMp3 = File.createTempFile("plantAudio", "mp3", applicationContext.cacheDir)
                    tempMp3.deleteOnExit()

                    val fos = FileOutputStream(tempMp3)
                    fos.write(decodedBody)

                    playAudio(tempMp3)
                }
            }
        }
    }

    private fun playAudio(tempMp3: File) {
        if (mp != null) {
            mp!!.reset()

            val fis = FileInputStream(tempMp3)
            mp!!.setDataSource(fis.fd)

            mp!!.prepareAsync()

            // Play audio when ready
            mp!!.setOnPreparedListener {
                invalidateOptionsMenu()
                retrieveState = false
                invalidateOptionsMenu()
                displayToast("Playing audio now")
                mp!!.start()
            }

            // Display toast when audio finish playing
            mp!!.setOnCompletionListener {
                displayToast("Audio finished playing")
                retrieveState = false
                invalidateOptionsMenu()
            }

            // Display error
            mp!!.setOnErrorListener { _, _, _ ->
                displayToast("Error occurred while playing audio")
                retrieveState = false
                invalidateOptionsMenu()
                true
            }
        }
    }

    private fun displayToast(msg: String) {
        return Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}