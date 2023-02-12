package com.nyp.sit.aws.project.onlyplants

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.nyp.sit.aws.project.onlyplants.Model.reminderService
import kotlinx.android.synthetic.main.activity_reminder_form.*
import kotlinx.coroutines.*

class ReminderFormActivity : AppCompatActivity() {

    var selectedHour: Int? = null
    var selectedMin: Int? = null
    var selectedDays: String? = null

    // Variables to display time
    private var displayHour: Int? = null
    private var displayMin: String? = null
    private var displayTime: String? = null
    private var displayDays: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_form)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set time and days information
        refreshDisplay()

        // define an array
        val arrayAdapter: ArrayAdapter<*>
        val options = arrayOf(
            resources.getString(R.string.reminderFormOption1),
            resources.getString(R.string.reminderFormOption2)
        )

        // access the listView from xml file
        val listView = findViewById<ListView>(R.id.reminderForm)
        arrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1,
            options)
        listView.adapter = arrayAdapter

        // Set list item to open fragment on click
        listView.setOnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                val timeDialogFragment = ReminderTimeDialogFragment()

                timeDialogFragment.show(supportFragmentManager, "timeDialogFragment")
            }

            if (position == 1) {
                val daysDialogFragment = ReminderDaysDialogFragment()

                daysDialogFragment.show(supportFragmentManager, "daysDialogFragment")
            }
        }

        // Back Button
        mainBackBtn.setOnClickListener {
            val intent = Intent(this, ListRemindersActivity::class.java)
            startActivity(intent)
        }

        // Save Button
        mainSaveBtn.setOnClickListener {
            if (selectedHour == null || selectedMin == null || selectedDays == null) {
                displayToast("No time/day has been selected")
            }
            else {
                mainSaveBtn.isEnabled = false
                mainBackBtn.isEnabled = false
                mainBackBtn.setBackgroundColor(Color.LTGRAY)
                loadOverlay()

                // Function is async, must wait for deviceToken to be
                // retrieved before creating eventbridge rule
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(OnCompleteListener { task ->

                        if (!task.isSuccessful) {
                            Log.w("token", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }

                        val token = task.result

                        // Print device token
                        Log.d("token on success", token)

                        // Create eventbridge rule
                        val scope = CoroutineScope(Job() + Dispatchers.IO)
                        val singleJobItem = scope.async(Dispatchers.IO) { reminderService().createReminderRule(createCronExp(), token) }

                        runBlocking {
                            scope.launch {
                                singleJobItem.await()
                            }
                            singleJobItem.join()
                            displayToast("Reminder successfully created")
                            val intent = Intent(applicationContext, ListRemindersActivity::class.java)
                            startActivity(intent)
                        }
                    })
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

    // Function to convert time from 24 hours to 12 hours
    private fun convertTimeDisplay() {
        if (selectedHour == null) {
            displayTime = "Not selected"
        }
        else {
            if (selectedHour == 0) {
                displayHour = 12
                displayTime = "AM"
            }
            else if (selectedHour!! < 12) {
                displayHour = selectedHour
                displayTime = "AM"
            }
            else if (selectedHour == 12) {
                displayHour = selectedHour
                displayTime = "PM"
            }
            else if (selectedHour!! > 12) {
                displayHour = selectedHour!! - 12
                displayTime = "PM"
            }
        }
    }

    // Function to display minutes
    private fun convertMinutes() {
        if (selectedMin == null) {
            displayTime = "Not selected"
        }
        else {
            if (selectedMin!! < 10) {
                displayMin = "0$selectedMin"
            }
            else {
                displayMin = selectedMin.toString()
            }
        }
    }

    // Function to display days
    private fun listDaysInString() {
        if (selectedDays == null) {
            displayDays = "Not selected"
        }
        else if (selectedDays == "*") {
            displayDays = "Everyday"
        }
        else {
            displayDays = "Every $selectedDays"
        }
    }

    // Function to update the TextViews displaying time and days
    fun refreshDisplay() {
        convertTimeDisplay()
        convertMinutes()
        listDaysInString()

        val timeExp: String?

        if (displayHour == null && displayMin == null) {
            timeExp = displayTime
        }
        else {
            timeExp = "$displayHour:$displayMin $displayTime"
        }


        showTimeTV.text = timeExp
        showDaysTV.text = displayDays
    }

    // Function to create cron expression for Eventbridge rule
    private fun createCronExp(): String {
        return "cron($displayMin $selectedHour ? * $selectedDays *)"
    }

    private fun displayToast(msg: String) {
        return Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun loadOverlay() {
        runOnUiThread {
            loadOverlay.setBackgroundColor(Color.GRAY)
            loadOverlay.background.alpha = 200
        }
    }
}