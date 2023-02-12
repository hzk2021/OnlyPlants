package com.nyp.sit.aws.project.onlyplants.View.PlantInformation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.nyp.sit.aws.project.onlyplants.MainActivity
import com.nyp.sit.aws.project.onlyplants.R
import kotlinx.android.synthetic.main.activity_plant_search.*

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
        }

        return super.onOptionsItemSelected(item)
    }
}