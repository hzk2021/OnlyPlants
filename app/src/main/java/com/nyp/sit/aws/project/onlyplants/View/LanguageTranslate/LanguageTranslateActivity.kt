package com.nyp.sit.aws.project.onlyplants.View.LanguageTranslate

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
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
        }

        return super.onOptionsItemSelected(item)
    }

    private fun displayToast(msg: String) {
        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        return
    }

}