package com.nyp.sit.aws.project.onlyplants

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_reminder_form.*
import kotlinx.android.synthetic.main.fragment_dialog_reminder_days.*

class ReminderFormActivity : AppCompatActivity() {

    var selectedHour: Int? = null
    var selectedMin: Int? = null
    var selectedDays: String? = null

    // Variables to display time
    private var displayHour: Int? = null
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
        var listView = findViewById<ListView>(R.id.reminderForm)
        arrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1,
            options)
        listView.adapter = arrayAdapter

        // Set list item to open fragment on click
        listView.setOnItemClickListener { parent, view, position, id ->
            if (position == 0) {
                var timeDialogFragment = ReminderTimeDialogFragment()

                timeDialogFragment.show(supportFragmentManager, "timeDialogFragment")
            }

            if (position == 1) {
                var daysDialogFragment = ReminderDaysDialogFragment()

                daysDialogFragment.show(supportFragmentManager, "daysDialogFragment")
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
        listDaysinString()

        var timeExp: String? = ""

        if (displayHour == null && selectedMin == null) {
            timeExp = displayTime
        }
        else {
            timeExp = "$displayHour:$selectedMin $displayTime"
        }


        showTimeTV.text = timeExp
        showDaysTV.text = displayDays
    }

    // Function to create cron expression for Eventbridge rule
    fun createCronExp(): String {
        return "cron($selectedMin $selectedHour ? * $selectedDays *)"
    }
}