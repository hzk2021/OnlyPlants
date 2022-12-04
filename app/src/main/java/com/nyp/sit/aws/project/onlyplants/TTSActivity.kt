package com.nyp.sit.aws.project.onlyplants

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import kotlinx.android.synthetic.main.activity_ttsactivity.*
import kotlinx.coroutines.*

class TTSActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttsactivity)

        playAudioBtn.setOnClickListener {
            prepareAudio()
        }

    }

    fun prepareAudio() {
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        var singleJobItem = scope.async(Dispatchers.Main) { TTSFunction() }

        scope.launch { singleJobItem.await() }
    }

}