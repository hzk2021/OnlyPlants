package com.nyp.sit.aws.project.onlyplants

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nyp.sit.aws.project.onlyplants.Model.Social.SocialMediaService
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.coroutines.*

class Post : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
//        GlobalScope.launch(Dispatchers.IO) {
//            val test=SocialMediaService().GetAllPost()
//            runOnUiThread{
//                Caption.text=test
//            }
//        }

    }
}