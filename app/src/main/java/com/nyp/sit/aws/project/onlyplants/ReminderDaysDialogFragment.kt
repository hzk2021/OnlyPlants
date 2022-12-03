package com.nyp.sit.aws.project.onlyplants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
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
//            val selectedDays =
        }

        return rootView
    }

}