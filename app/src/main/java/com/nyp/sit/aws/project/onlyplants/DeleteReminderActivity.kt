package com.nyp.sit.aws.project.onlyplants

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.nyp.sit.aws.project.onlyplants.Model.reminderService
import kotlinx.android.synthetic.main.activity_delete_reminder.*
import kotlinx.coroutines.*

class DeleteReminderActivity : AppCompatActivity() {

    private lateinit var reminderDisplayList: Array<String>
    private lateinit var reminderNameList: Array<String>
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var mState: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_reminder)

        val intent = intent
        reminderDisplayList = intent.getStringArrayExtra("reminderDisplayList") as Array<String>
        reminderNameList = intent.getStringArrayExtra("reminderNameList") as Array<String>

        refreshRuleLVDisplay()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cancel_delete_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.cancelBtn -> startActivity(Intent(this, ListRemindersActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        menu?.findItem(R.id.deleteBtn)?.isEnabled = mState

        return super.onPrepareOptionsMenu(menu)
    }

    private fun refreshRuleLVDisplay() {
        // Check if any rules were created from this device
        if (reminderDisplayList.isEmpty()) {
            println("Reminder List is Null")
            ruleLV.visibility = View.GONE
            noRulesTV.setText(R.string.no_reminders_set)
            noRulesTV.visibility = View.VISIBLE
        }
        else {
            println("Reminder List is not Null")

            val listView = findViewById<ListView>(R.id.ruleLV)
            arrayAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                reminderDisplayList)
            listView.adapter = arrayAdapter

            listView.setOnItemClickListener { _, _, position, _ ->
                if (position < reminderNameList.size) {
                    // Delete eventbridge rule
                    deleteEvent(reminderNameList[position])
                }
            }

            noRulesTV.visibility = View.GONE
            noRulesTV.text = ""
            ruleLV.visibility = View.VISIBLE
        }
    }

    private fun deleteEvent(ruleName: String) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure you want to delete this item?")

        builder.setPositiveButton("Yes") { dialog, _ ->

            // Disable options menu
            mState = false
            invalidateOptionsMenu()

            loadOverlay()

            // Delete eventbridge rule
            val scope = CoroutineScope(Job() + Dispatchers.IO)
            val singleJobItem = scope.async(Dispatchers.IO) {
                reminderService().deleteReminderRule(ruleName)
            }

            dialog.cancel()

            runBlocking {
                scope.launch {
                    singleJobItem.await()
                }
                singleJobItem.join()
                displayToast("Reminder successfully deleted")

                val intent = Intent(applicationContext, ListRemindersActivity::class.java)
                startActivity(intent)
            }
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }

        val alert = builder.create()
        alert.show()
    }

    private fun loadOverlay() {
        runOnUiThread {
            loadOverlay.setBackgroundColor(Color.GRAY)
            loadOverlay.background.alpha = 200
        }
    }

    private fun displayToast(msg: String) {
        return Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}