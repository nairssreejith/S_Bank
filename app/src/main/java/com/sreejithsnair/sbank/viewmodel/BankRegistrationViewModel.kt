package com.sreejithsnair.sbank.viewmodel

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel

class BankRegistrationViewModel: ViewModel() {

    private var isPanValid: Boolean = false
    private var isBirthDateValid: Boolean = false
    private lateinit var dateValidationStatus: String

    fun validatePAN(panNumber: String){
        val panPattern = Regex("[A-Z]{5}[0-9]{4}[A-Z]{1}")
        isPanValid =  panPattern.matches(panNumber)
    }

    fun getPanValidationStatus(): Boolean{
        return isPanValid
    }

    fun validateDateOfBirth(day: String, month: String, year: String): String {
        val date_day = day.toIntOrNull()
        val date_month = month.toIntOrNull()
        val date_year = year.toIntOrNull()

        // Fetching present date
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        if (date_day == null || date_month == null || date_year == null) {
            dateValidationStatus = "Please enter valid date"
            isBirthDateValid = false

        } else if (date_year > currentYear || (date_year == currentYear && date_month > currentMonth) ||
                (date_year == currentYear && date_month == currentMonth && date_day > currentDay)) {
            dateValidationStatus = "Date of birth cannot be in the future"
            isBirthDateValid = false
        } else {
            dateValidationStatus = "Valid date of birth"
            isBirthDateValid = true
        }
        return dateValidationStatus
    }

    fun getBirthDateValidationStatus(): Boolean{
        return isBirthDateValid
    }

}