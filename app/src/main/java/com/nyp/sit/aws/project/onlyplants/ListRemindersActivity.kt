package com.nyp.sit.aws.project.onlyplants

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nyp.sit.aws.project.onlyplants.Model.reminderService

class ListRemindersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_reminders)

        reminderService().getReminders()
    }
}