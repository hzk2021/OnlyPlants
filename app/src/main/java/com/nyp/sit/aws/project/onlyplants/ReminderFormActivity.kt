package com.nyp.sit.aws.project.onlyplants

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class ReminderFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_form)

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
}