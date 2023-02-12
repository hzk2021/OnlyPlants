package com.nyp.sit.aws.project.onlyplants

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) : Unit = runBlocking{
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}