package com.nyp.sit.aws.project.onlyplants

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_wiki_api.*
import kotlinx.coroutines.launch
import com.nyp.sit.aws.project.onlyplants.Model.Call_Wiki
import kotlinx.coroutines.*

class WikiApiService : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wiki_api)

//        GlobalScope.launch(Dispatchers.IO){
//
//            val test = Call_Wiki().GetWikiInformation()
//            //print(test)
//            runOnUiThread{
//                result_id.text = test
//            }
//        }

        search_button.setOnClickListener{
            val input = input_et.text.toString()


            val scope = CoroutineScope(Job() + Dispatchers.IO)
            val singleJobItem = scope.async(Dispatchers.IO) { Call_Wiki().postWikiSearch(input) }

            scope.launch {
                val response = singleJobItem.await()
                runOnUiThread{result_id.text = response.toString()}

            }
        }
    }
}