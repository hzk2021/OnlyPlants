package com.nyp.sit.aws.project.onlyplants

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_wiki_api.*
import kotlinx.coroutines.launch
import com.nyp.sit.aws.project.onlyplants.Model.Call_Wiki
import kotlinx.coroutines.*

class WikiApiService : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wiki_api)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        search_button.setOnClickListener{
            val input = input_et.text.toString()

            // Check for empty string
            if (input.isBlank()) {
                Toast.makeText(this, "Search cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else {
                val scope = CoroutineScope(Job() + Dispatchers.IO)
                val singleJobItem = scope.async(Dispatchers.IO) { Call_Wiki().postWikiSearch(input) }

                scope.launch {
                    val response = singleJobItem.await()
                    runOnUiThread{result_id.text = response.toString()}

                }
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