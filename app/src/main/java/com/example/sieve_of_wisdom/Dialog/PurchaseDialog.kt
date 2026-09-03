package com.example.sieve_of_wisdom.Dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.sieve_of_wisdom.R

class PurchaseDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.dialog_confirm_purchase,
            container,
            false
        )
    }

    override fun onStart() {
        super.onStart()

        // Make the dialog background transparent
        dialog?.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        // Optional: make the dialog width match most of the screen
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val messageText = view.findViewById<TextView>(R.id.tvMessage)
        val packageTitleText = view.findViewById<TextView>(R.id.tvPackageTitle)

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)

        // Get data passed to the Dialog
        messageText.text = arguments?.getString(ARG_MESSAGE)
        packageTitleText.text = arguments?.getString(ARG_PACKAGE_TITLE)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnConfirm.setOnClickListener {

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                Bundle()
            )

            dismiss()
        }
    }

    companion object {
        const val REQUEST_KEY = "purchase_request"

        private const val ARG_MESSAGE = "arg_message"
        private const val ARG_PACKAGE_TITLE = "arg_package_title"

        fun newInstance(
            message: String,
            packageTitle: String
        ): PurchaseDialog {
            return PurchaseDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_MESSAGE, message)
                    putString(ARG_PACKAGE_TITLE, packageTitle)
                }
            }
        }
    }
}