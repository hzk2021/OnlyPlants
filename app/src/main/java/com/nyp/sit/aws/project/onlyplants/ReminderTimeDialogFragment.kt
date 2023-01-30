package com.nyp.sit.aws.project.onlyplants

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_reminder_time.*
import kotlinx.android.synthetic.main.fragment_dialog_reminder_time.view.*

class ReminderTimeDialogFragment: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_dialog_reminder_time,
            container,
            false)

        // Assign value to variables in activity
        val activity: ReminderFormActivity  = activity as ReminderFormActivity

        Log.d("test","time saved: " + activity.selectedHour + ":" + activity.selectedMin)

        rootView.cancelBtn.setOnClickListener {
            dismiss()
        }

        rootView.saveBtn.setOnClickListener {
            activity.selectedHour = reminderTime.hour
            activity.selectedMin = reminderTime.minute
            dismiss()
        }

        return rootView
    }

    override fun onDismiss(dialog: DialogInterface) {

        // Set time and days information
        val activity: ReminderFormActivity  = activity as ReminderFormActivity
        activity.refreshDisplay()

        super.onDismiss(dialog)
    }
}