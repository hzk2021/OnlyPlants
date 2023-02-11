package com.nyp.sit.aws.project.onlyplants.View.LanguageTranslate

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
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

        populateSpinners()

        lta_B_Translate.setOnClickListener {

            GlobalScope.launch {
                val fromLang = lta_FromLang.selectedItem.toString()
                val toLang = lta_ToLang.selectedItem.toString()
                val text = lta_ET_EditText.text.toString()

                val result = LanguageTranslateService().GetTranslatedText(fromLang, toLang, text)

                runOnUiThread{
                    lta_ET_EditText.setText(result.toString())
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

}