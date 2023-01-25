package com.nyp.sit.aws.project.onlyplants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_reminder_days.*
import kotlinx.android.synthetic.main.fragment_dialog_reminder_days.view.*
import kotlinx.android.synthetic.main.fragment_dialog_reminder_time.view.*
import kotlinx.android.synthetic.main.fragment_dialog_reminder_time.view.cancelBtn
import kotlinx.android.synthetic.main.fragment_dialog_reminder_time.view.saveBtn

class ReminderDaysDialogFragment: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_dialog_reminder_days,
            container,
            false)

        rootView.radioGrp.setOnCheckedChangeListener { radioGroup, i ->
            val daysCB: Array<CheckBox> = arrayOf(sunCB, monCB, tueCB,
                wedCB , thuCB , friCB , satCB)

            enableCheckbox(daysCB)
        }

        rootView.cancelBtn.setOnClickListener {
            dismiss()
        }

        rootView.saveBtn.setOnClickListener {

            val daysCB: Array<CheckBox> = arrayOf(sunCB, monCB, tueCB,
                wedCB , thuCB , friCB , satCB)

            // To create DayOfWeek for cron expression
            var selectedDays = ""

            if (everydayRB.isChecked) {
                selectedDays += "*"
            }

            else if (customRB.isChecked){
                for (day in daysCB){
                    if (day.isChecked){
                        selectedDays += day.tag
                        selectedDays += ","
                    }
                }

                // Remove comma at the end
                selectedDays = selectedDays.dropLast(1)
            }

            // Check if no option was checked/string is empty
            if (selectedDays.isBlank()){
                errorMsg.text = "Please select at least one option"
                errorMsg.visibility = View.VISIBLE
            }

            else {
                removeError()

                // Assign value to variable in activity
                val activity: ReminderFormActivity  = activity as ReminderFormActivity
                activity.selectedDays = selectedDays

                dismiss()
            }
        }

        return rootView
    }

    // Function to enable/disable checkboxes based on radio button selection
    fun enableCheckbox(daysCB: Array<CheckBox>){
        if (customRB.isChecked){
            for (day in daysCB){
                day.isEnabled = true
            }
        }
        else {
            for (day in daysCB){
                day.isEnabled = false
            }
        }
    }

    // Function to remove error message
    fun removeError(){
        if (errorMsg.visibility == View.VISIBLE && errorMsg.text.isNotBlank()){
            errorMsg.text = ""
            errorMsg.visibility = View.GONE
        }
    }

}