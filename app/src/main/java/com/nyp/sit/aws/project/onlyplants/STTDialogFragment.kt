package com.nyp.sit.aws.project.onlyplants

import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nyp.sit.aws.project.onlyplants.Model.sttService
import kotlinx.android.synthetic.main.activity_sttactivity.*
import kotlinx.android.synthetic.main.fragment_dialog_stt.view.*

class STTDialogFragment: DialogFragment() {

    private var mediaRecorder: MediaRecorder? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val rootView: View = inflater.inflate(R.layout.fragment_dialog_stt, container, false)

        mediaRecorder = MediaRecorder()

        val STT = sttService(requireContext(), requireContext().cacheDir)

        STT.recordAudio(requireActivity().micBtn, mediaRecorder)

        rootView.stopBtn.setOnClickListener {
            STT.stopAudio(requireActivity().micBtn, mediaRecorder)
            mediaRecorder = null
            dismiss()
        }

        rootView.searchStrBtn.setOnClickListener {
            STT.stopAudio(requireActivity().micBtn, mediaRecorder)
            mediaRecorder = null
            // trigger lambda
            dismiss()
        }

        return rootView
    }

}

