package com.nyp.sit.aws.project.onlyplants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_reminder_days.*
import kotlinx.android.synthetic.main.fragment_dialog_reminder_time.view.*

class ReminderDaysDialogFragment: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_dialog_reminder_days,
            container,
            false)

        rootView.cancelBtn.setOnClickListener {
            dismiss()
        }

        rootView.saveBtn.setOnClickListener {

            // To create DayOfWeek for cron expression
            var selectedDays = ""

            if (sunCB.isChecked){
                selectedDays += sunCB.tag
                selectedDays += ","
            }
            if (monCB.isChecked){
                selectedDays += monCB.tag
                selectedDays += ","
            }
            if (tueCB.isChecked){
                selectedDays += tueCB.tag
                selectedDays += ","
            }
            if (wedCB.isChecked){
                selectedDays += wedCB.tag
                selectedDays += ","
            }
            if (thuCB.isChecked){
                selectedDays += thuCB.tag
                selectedDays += ","
            }
            if (friCB.isChecked){
                selectedDays += friCB.tag
                selectedDays += ","
            }
            if (satCB.isChecked){
                selectedDays += satCB.tag
                selectedDays += ","
            }

            // Remove comma at the end
            selectedDays = selectedDays.dropLast(1)

            // Assign value to variable in activity
            val activity: ReminderFormActivity  = activity as ReminderFormActivity
            activity.selectedDays = selectedDays

            dismiss()
        }

        return rootView
    }

}