package com.nyp.sit.aws.project.onlyplants

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.Lifecycle
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.nyp.sit.aws.project.onlyplants.Credentials.learnersLab
import com.nyp.sit.aws.project.onlyplants.Model.PlantIdentifier.PlantService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    val credentials = Credentials.learnersLab

    override fun onCreate(savedInstanceState: Bundle?) : Unit = runBlocking{
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.IO) {
            val dsa = PlantService().GetPlantInformation("OnlyFans;)")
            runOnUiThread{
                test.text = dsa
            }
        }
    }
}