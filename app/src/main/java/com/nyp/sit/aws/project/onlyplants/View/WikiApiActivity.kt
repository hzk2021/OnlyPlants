package com.nyp.sit.aws.project.onlyplants

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_wiki_api.*
import kotlinx.coroutines.launch
import com.nyp.sit.aws.project.onlyplants.Model.Call_Wiki
import com.nyp.sit.aws.project.onlyplants.Model.LanguageTranslate.LanguageTranslateService
import com.nyp.sit.aws.project.onlyplants.Model.Plant.PlantService
import com.nyp.sit.aws.project.onlyplants.Model.networkService
import com.nyp.sit.aws.project.onlyplants.Model.ttsService
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class WikiApiService : AppCompatActivity() {

    private var mp: MediaPlayer? = null
    private var retrieveState: Boolean = false
    private var emptySearch: Boolean = true
    private var searchStr: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wiki_api)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        search_button.setOnClickListener{
            val input = input_et.text.toString()

            // Check for empty string
            if (input.isBlank()) {
                Toast.makeText(this, "Search cannot be empty", Toast.LENGTH_SHORT).show()
                emptySearch = true
            }
            else {
                val scope = CoroutineScope(Job() + Dispatchers.IO)
                val singleJobItem = scope.async(Dispatchers.IO) { Call_Wiki().postWikiSearch(input) }

                scope.launch {
                    val response = singleJobItem.await()
                    runOnUiThread{result_id.text = response.toString()}
                    emptySearch = false
                    searchStr = input
                    invalidateOptionsMenu()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.playAudioBtn -> prepareAudio()
            R.id.stopAudioBtn -> {
                mp?.release()
                mp = null
                invalidateOptionsMenu()
                displayToast("Audio stopped")
            }
            R.id.sttBtn -> transcribe()
            R.id.translateBtn -> {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.popup_translate)
                val spinner1 = dialog.findViewById<Spinner>(R.id.pu_FromLang)
                val spinner2 = dialog.findViewById<Spinner>(R.id.pu_ToLang)
                val button = dialog.findViewById<Button>(R.id.pu_Button_Translate)

                val spinnerFromLang = dialog.findViewById<Spinner>(R.id.pu_FromLang)
                val itemsFromLang = arrayOf("en", "fr", "zh")
                val fromAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemsFromLang)

                spinner1.adapter = fromAdapter
                spinner2.adapter = fromAdapter

                dialog.show()

                button.setOnClickListener {

                    GlobalScope.launch {
                        dialog.dismiss()

                        val fromLang = spinner1.selectedItem.toString()
                        val toLang = spinner2.selectedItem.toString()

                        val rootView = findViewById<ViewGroup>(android.R.id.content)

                        PlantService().translateViews(rootView, fromLang, toLang)
                    }

                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {

        if (mp?.isPlaying == true) {
            mp?.release()
            mp = null
            retrieveState = false
            invalidateOptionsMenu()
            displayToast("Audio stopped")
        }

        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.play_audio_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        runOnUiThread {
            if (emptySearch) {
                menu?.findItem(R.id.playAudioBtn)?.isEnabled = false
                menu?.findItem(R.id.playAudioBtn)?.isVisible = true
                menu?.findItem(R.id.stopAudioBtn)?.isEnabled = false
                menu?.findItem(R.id.stopAudioBtn)?.isVisible = false
            }
            else if (mp?.isPlaying == true) {
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
        val singleJobItem = scope.async(Dispatchers.IO) { ttsService().retrieveAudio(searchStr) }

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

    // Speech to test service
    // Function to convert speech to text
    private fun transcribe() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text")

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
                input_et.setText(result!![0])
            }
        }
    }
}