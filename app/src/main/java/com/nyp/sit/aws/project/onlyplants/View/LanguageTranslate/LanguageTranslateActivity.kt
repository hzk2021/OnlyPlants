package com.nyp.sit.aws.project.onlyplants.View.LanguageTranslate

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nyp.sit.aws.project.onlyplants.MainActivity
import com.nyp.sit.aws.project.onlyplants.Model.LanguageTranslate.LanguageTranslateService
import com.nyp.sit.aws.project.onlyplants.R
import kotlinx.android.synthetic.main.activity_language_translate.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class LanguageTranslateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_translate)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        populateSpinners()

        lta_B_Translate.setOnClickListener {

            GlobalScope.launch {
                val fromLang = lta_FromLang.selectedItem.toString()
                val toLang = lta_ToLang.selectedItem.toString()
                val text = lta_ET_EditText.text.toString()

                // Check if input is empty
                if (text.isBlank()) {
                    displayToast("Search cannot be empty")
                }
                else {
                    val result = LanguageTranslateService().GetTranslatedText(fromLang, toLang, text)

                    runOnUiThread{
                        lta_ET_EditText.setText(result.toString())
                    }
                }



            }
        }
    }

    fun populateSpinners(){
        val spinnerFromLang = findViewById<Spinner>(R.id.lta_FromLang)
        val itemsFromLang = arrayOf("en", "fr", "zh")
        val fromAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemsFromLang)

        spinnerFromLang.adapter = fromAdapter

        val spinnerToLang = findViewById<Spinner>(R.id.lta_ToLang)
        val itemsToLang = arrayOf("en", "fr", "zh")
        val toAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itemsToLang)

        spinnerToLang.adapter = toAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.sttBtn -> transcribe()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun displayToast(msg: String) {
        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        return
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
                lta_ET_EditText.setText(result!![0])
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stt_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}