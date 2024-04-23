package com.sreejithsnair.sbank.view

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.sreejithsnair.sbank.R
import com.sreejithsnair.sbank.databinding.ActivityMainBinding
import com.sreejithsnair.sbank.viewmodel.BankRegistrationViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bankRegistrationViewModel: BankRegistrationViewModel

    var isPanValid = false
    var isBirthDateValid = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing ViewModel
        bankRegistrationViewModel = ViewModelProvider(this)
            .get(BankRegistrationViewModel::class.java)

        // Disabling the NEXT button
        binding.btnNext.isEnabled = bankRegistrationViewModel.getPanValidationStatus()

        // Check validation of PAN number on moving of focus from the PAN number edittext.
        binding.edtPanNumber.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                validatePanNumber(hasFocus)
            }

        binding.edtYear.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                validateBirthDate(hasFocus)
            }

        binding.btnNext.setOnClickListener{
            showToast("Details submitted successfully.")
        }

        binding.tvNoPanLink.setOnClickListener {
            showExitConfirmationDialog()
        }

    }

    private fun Context.showToast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun validatePanNumber(hasFocus: Boolean){
        if (!hasFocus) {
            val panNumber = binding.edtPanNumber.text.toString()
            bankRegistrationViewModel.validatePAN(panNumber)
            isPanValid = bankRegistrationViewModel.getPanValidationStatus()
            if(!isPanValid){
                binding.edtLayoutPanNumber.error = "Invalid PAN number."
            }

            if(isPanValid){
                binding.btnNext.isEnabled = true
            }

        } else {
            binding.edtLayoutPanNumber.error = null
            if(isPanValid){
                binding.btnNext.isEnabled = true
            }
        }
    }

    private fun validateBirthDate(hasFocus: Boolean){
        if(!hasFocus){
            val day = binding.edtDate.text.toString()
            val month = binding.edtMonth.text.toString()
            val year = binding.edtYear.text.toString()

            var birthDateValidationStatus = bankRegistrationViewModel.validateDateOfBirth(day, month, year)

        }
    }

    private fun showExitConfirmationDialog() {


        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Confirm Exit")
        alertDialogBuilder.setMessage(getString(R.string.no_pan_exit_conformation))
        alertDialogBuilder.setPositiveButton(getString(R.string.alert_dialog_positive)) { dialog, which ->
            finish()
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.alert_dialog_negative)) { dialog, which ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


}
