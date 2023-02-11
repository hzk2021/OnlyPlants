package com.nyp.sit.aws.project.onlyplants

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nyp.sit.aws.project.onlyplants.Model.reminderService
import kotlinx.coroutines.*

open class DeleteReminderFragment : Fragment() {

    private fun deleteEvent(ruleName: String) {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, _ ->

            // Delete eventbridge rule
            val scope = CoroutineScope(Job() + Dispatchers.IO)
            val singleJobItem = scope.async(Dispatchers.IO) {
                reminderService().deleteReminderRule(ruleName)
            }
            scope.launch {
                singleJobItem.await()
            }

            dialog.cancel()
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })
        val alert = builder.create()
        alert.show()

    }

}