package com.nyp.sit.aws.project.onlyplants.View.PlantInformation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.nyp.sit.aws.project.onlyplants.MainActivity
import com.nyp.sit.aws.project.onlyplants.R
import kotlinx.android.synthetic.main.activity_plant_search.*
import java.util.*

class PlantSearch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_search)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ps_Button_Search.setOnClickListener {
            val plantName = ps_ET_PlantName.text.toString()

            if (plantName != ""){
                val plantInfoIntent = Intent(this@PlantSearch, PlantInformationActivity::class.java)
                plantInfoIntent.putExtra("plant_image", "".toByteArray())
                plantInfoIntent.putExtra("plant_name", ps_ET_PlantName.text.toString())
                this@PlantSearch.startActivity(plantInfoIntent)
            }
            else {
                Toast.makeText(this@PlantSearch, "Please do not leave blank", Toast.LENGTH_LONG).show()
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.stt_menu, menu)
        return super.onCreateOptionsMenu(menu)
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
                ps_ET_PlantName.setText(result!![0])
            }
        }
    }
}