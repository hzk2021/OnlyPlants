package com.nyp.sit.aws.project.onlyplants

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.nyp.sit.aws.project.onlyplants.Model.ReminderRule
import com.nyp.sit.aws.project.onlyplants.Model.reminderService
import kotlinx.android.synthetic.main.activity_list_reminders.*
import kotlinx.coroutines.*

class ListRemindersActivity : AppCompatActivity(){

    private var reminderList: Array<ReminderRule>? = null
    private var reminderDisplayList: Array<String> = emptyArray()
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var reminderNameList: Array<String> = emptyArray()
    private var mState: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_reminders)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set intent to go to ReminderFormActivity
        addReminderBtn.setOnClickListener {
            val intent = Intent(this, ReminderFormActivity::class.java)
            startActivity(intent)
        }

        // Retrieve rules from EventBridge
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("token", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result

                Log.d("token on success", token)

                // Get reminder rules created from this device
                val scope = CoroutineScope(Job() + Dispatchers.IO)
                val singleJobItem =
                    scope.async(Dispatchers.IO) { reminderService().getReminderRules(token) }
                scope.launch {
                    val resp = singleJobItem.await()
                    Log.d("retrieve rules", "retrieve complete")
                    reminderList = resp

                    if (reminderList.isNullOrEmpty()) {
                        reminderList = null
                    }

                    try {
                        runOnUiThread {
                            refreshRuleLVDisplay()
                        }
                    } catch (e: Exception) {
                        Log.d("responseMsg", "code failed")
                        e.printStackTrace()
                    }

                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.deleteBtn -> {
                val intent = Intent(this, DeleteReminderActivity::class.java)
                intent.putExtra("reminderDisplayList", reminderDisplayList)
                intent.putExtra("reminderNameList", reminderNameList)
                startActivity(intent)
            }
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        menu?.findItem(R.id.deleteBtn)?.isEnabled = mState

        return super.onPrepareOptionsMenu(menu)
    }

    private fun refreshRuleLVDisplay() {
        // Check if any rules were created from this device
        if (reminderList.isNullOrEmpty()) {
            println("Reminder List is Null")
            ruleLV.visibility = View.GONE
            noRulesTV.setText(R.string.no_reminders_set)
            noRulesTV.visibility = View.VISIBLE

            // Disable delete button; no reminders to delete
            mState = false
            invalidateOptionsMenu()
        }
        else {
            println("Reminder List is not Null")

            for (rule in reminderList!!) {
                val listStr: String = convertCronToString(rule)
                reminderDisplayList += listStr
                reminderNameList += rule.Name
            }

            val listView = findViewById<ListView>(R.id.ruleLV)
            arrayAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                reminderDisplayList)
            listView.adapter = arrayAdapter

            noRulesTV.visibility = View.GONE
            noRulesTV.text = ""
            ruleLV.visibility = View.VISIBLE

            // Display delete button
            mState = true
            invalidateOptionsMenu()
        }
    }

    // Function to convert cronExp into String
    private fun convertCronToString(rule: ReminderRule): String {

        val cronExp = rule.ScheduleExpression
            .replace("cron(", "")
            .replace(")", "")
        val cronExpArray = cronExp.split(" ")

        if (cronExpArray.size != 6) {
            return "problem"
        }

        val minute = cronExpArray[0]
        val hour = cronExpArray[1].toInt()
        val timeValArray = convertTimeDisplay(hour)
        val timeStr = "${timeValArray[0]}:$minute ${timeValArray[1]}"

        val dayOfWeek = cronExpArray[4]

        val fullStr: String

        if (dayOfWeek == "*") {
            fullStr = "$timeStr (EVERYDAY)"
        }
        else {
            fullStr = "$timeStr (EVERY $dayOfWeek)"
        }

        return fullStr
    }

    // Function to convert time from 24 hours to 12 hours
    private fun convertTimeDisplay(selectedHour: Int): Array<Any> {

        val displayHour: Int?
        val displayTime: String

        if (selectedHour == 0) {
            displayHour = 12
            displayTime = "AM"
        }
        else if (selectedHour < 12) {
            displayHour = selectedHour
            displayTime = "AM"
        }
        else if (selectedHour == 12) {
            displayHour = selectedHour
            displayTime = "PM"
        }
        else {
            displayHour = selectedHour - 12
            displayTime = "PM"
        }

        return arrayOf(displayHour,displayTime)
    }

}