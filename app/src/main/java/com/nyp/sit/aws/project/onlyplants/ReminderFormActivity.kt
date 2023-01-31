package com.nyp.sit.aws.project.onlyplants

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
//        mainBackBtn.setOnClickListener {
//
//        }

        // Save Button
        mainSaveBtn.setOnClickListener {
            if (selectedHour == null || selectedMin == null || selectedDays == null) {
                Toast.makeText(this, "No time/day has been selected", Toast.LENGTH_SHORT).show()
            }
            else {
                createReminderRule()
                Toast.makeText(this, "Creating reminder", Toast.LENGTH_SHORT).show()
            }
        }
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
    private fun listDaysinString() {
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
        listDaysinString()

        var timeExp: String? = ""

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

    // Function to call createReminderRule API
    private fun createReminderRule() {
        val cronExp = createCronExp()

        val scope = CoroutineScope(Job() + Dispatchers.IO)
        val singleJobItem = scope.async(Dispatchers.IO) { reminderService().createReminder(cronExp) }

        scope.launch { singleJobItem.await() }
    }
}