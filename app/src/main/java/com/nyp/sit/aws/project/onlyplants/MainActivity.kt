package com.nyp.sit.aws.project.onlyplants

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nyp.sit.aws.project.onlyplants.View.LanguageTranslate.LanguageTranslateActivity
import com.nyp.sit.aws.project.onlyplants.View.LocationActivity
import com.nyp.sit.aws.project.onlyplants.View.PlantIdentifier.PlantIdentifierActivity
import com.nyp.sit.aws.project.onlyplants.View.PlantInformation.PlantInformationActivity
import com.nyp.sit.aws.project.onlyplants.View.PlantInformation.PlantSearch
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        plantMediaCard.setOnClickListener {
            goToActivity(Home())
        }
        plantWikiCard.setOnClickListener {
            goToActivity(WikiApiService())
        }
        mapCard.setOnClickListener {
            goToActivity(LocationActivity())
        }
        plantIdenCard.setOnClickListener {
            goToActivity(PlantIdentifierActivity())
        }
        translateCard.setOnClickListener {
            goToActivity(LanguageTranslateActivity())
        }
        reminderCard.setOnClickListener {
            goToActivity(ListRemindersActivity())
        }
        plantInfoCard.setOnClickListener {
            goToActivity(PlantSearch())
        }
    }

    private fun goToActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
        return
    }
}