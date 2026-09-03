package com.example.sieve_of_wisdom.Dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.sieve_of_wisdom.R

class PurchaseDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Transparent window background ensures shape corner radius shows cleanly
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00000000")))
        return inflater.inflate(R.layout.dialog_confirm_purchase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)

        btnCancel.setOnClickListener { dismiss() }
        btnConfirm.setOnClickListener {
            // Handle confirmation logic
            dismiss()
        }
    }
}