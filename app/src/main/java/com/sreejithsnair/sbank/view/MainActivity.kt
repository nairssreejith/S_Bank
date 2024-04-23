package com.sreejithsnair.sbank.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.color.MaterialColors
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

        initialize()

        // Check validation of PAN number on moving of focus from the PAN number edittext.
        panNumberTextInputListener()

        // Check validation of BirthDate with respect to current Date on moving focus from year edittext.
        birthDateTextInputListener()

        // Opens a webpage showcasing the disclaimer using intent when clicked on a substring ("Learn more").
        learnMoreTextViewListener()

        // Next button shows the registration successful message as toast.
        nextButtonListener()

        // No PAN number text acts as a link that makes user confirm the non availability of PAN and exits app.
        noPanNumberTextViewListener()
    }

    private fun initialize(){

        // Initializing ViewModel.
        bankRegistrationViewModel = ViewModelProvider(this)
            .get(BankRegistrationViewModel::class.java)

        // Disabling the NEXT button initially as no validations have been performed.
        binding.btnNext.isEnabled = false
    }

    private fun panNumberTextInputListener(){
        binding.edtPanNumber.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    checkIfPanValid()
                } else {
                    binding.edtLayoutPanNumber.error = null
                }
            }

        binding.edtPanNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 10) {
                    checkIfPanValid()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun birthDateTextInputListener(){
        binding.edtDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 2)
                    checkIfDateValid()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edtMonth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 2)
                    checkIfDateValid()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edtYear.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 4)
                    checkIfDateValid()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun checkIfPanValid(){
        val panNumber = binding.edtPanNumber.text.toString()

        bankRegistrationViewModel.validatePAN(panNumber)
        isPanValid = bankRegistrationViewModel.getPanValidationStatus()

        if(!isPanValid){
            binding.edtLayoutPanNumber.error = "Invalid PAN number."
            // This function executes a coroutine in the view-model and observes a mutable live data to enable NEXT button.
            observeLiveDataFromViewModel()
        } else {
            binding.edtLayoutPanNumber.error = null
            observeLiveDataFromViewModel()
        }
    }

    private fun checkIfDateValid(){
        val day = binding.edtDate.text.toString()
        val month = binding.edtMonth.text.toString()
        val year = binding.edtYear.text.toString()

        val birthDateValidationStatus = bankRegistrationViewModel.validateDateOfBirth(day, month, year)
        isBirthDateValid = bankRegistrationViewModel.getBirthDateValidationStatus()

        if(!isBirthDateValid){
            binding.tvDateErrorMessage.visibility = View.VISIBLE
            binding.tvDateErrorMessage.text = birthDateValidationStatus
            observeLiveDataFromViewModel()
        } else {
            // This function executes a coroutine in the view-model and observes a mutable live data to enable NEXT button.
            observeLiveDataFromViewModel()
            binding.tvDateErrorMessage.visibility = View.INVISIBLE
        }
    }

    private fun learnMoreTextViewListener() {
        val disclaimer = getString(R.string.disclaimer)

        val spannableString = SpannableString(disclaimer)

        val startIndex = disclaimer.indexOf("Learn")
        val endIndex = startIndex + "Learn more.".length

        var url = "https://www.utiitsl.com/disclaimer"
        val clickableSpan: ClickableSpan = object: ClickableSpan(){
            override fun onClick(p0: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.argb(255,98,0,238)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvDisclaimer.text = spannableString
        binding.tvDisclaimer.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun nextButtonListener(){
        binding.btnNext.setOnClickListener{
            showToast("Details submitted successfully.")
            showRegistrationSuccessDialog()
        }
    }

    private fun noPanNumberTextViewListener(){
        binding.tvNoPanLink.setOnClickListener {
            showExitConfirmationDialog()
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

    private fun showRegistrationSuccessDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Registration Successful")
        alertDialogBuilder.setMessage(getString(R.string.registration_success))
        alertDialogBuilder.setPositiveButton(getString(R.string.alert_dialog_positive)) { dialog, which ->
            dialog.dismiss()
            resetApp()
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.alert_dialog_negative)) { dialog, which ->
            finish()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun resetApp() {
        binding.edtPanNumber.text = null
        binding.edtPanNumber.requestFocus()
        binding.edtDate.text = null
        binding.edtMonth.text = null
        binding.edtYear.text = null
    }

    private fun observeLiveDataFromViewModel() {
        bankRegistrationViewModel.checkIfBothPanAndDateAreValidated().observe(this, Observer {
            binding.btnNext.isEnabled = it.isPanNumberValid && it.isBirthDateValid
        })
    }

    private fun Context.showToast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

}
