package com.sreejithsnair.sbank.viewmodel

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sreejithsnair.sbank.model.ListOfValidation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DateTimeException
import java.time.LocalDate

class BankRegistrationViewModel: ViewModel() {

    private var isPanValid: Boolean = false
    private var isBirthDateValid: Boolean = false
    private var dateValidationStatus: String = ""

    private val isValidLivedata = MutableLiveData<ListOfValidation>()

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

        /*if (date_day == null || date_month == null || date_year == null){
            dateValidationStatus = "Date of birth cannot be blank"
            isBirthDateValid = false
        }*/

        if (date_day != null && date_month != null && date_year != null) {
            if (date_year > currentYear || (date_year == currentYear && date_month > currentMonth) ||
                (date_year == currentYear && date_month == currentMonth && date_day > currentDay)) {
                dateValidationStatus = "Date of birth cannot be in the future"
                isBirthDateValid = false
            } else {
                dateValidationStatus = "Valid Date of Birth"
                isBirthDateValid = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(!checkIfTheDayExist(day, month, year)){
                        dateValidationStatus = "Invalid Date of Birth"
                        isBirthDateValid = false
                    } else {
                        dateValidationStatus = "Valid Date of Birth"
                        isBirthDateValid = true
                    }
                }
            }
        }
        return dateValidationStatus
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkIfTheDayExist(day: String?, month: String?, year: String?): Boolean {
        try {
            val dateString = "$year-$month-$day"
            val date = LocalDate.parse(dateString)
            return true
        } catch (e: DateTimeException) {
            return false
        }
    }

    fun getBirthDateValidationStatus(): Boolean{
        return isBirthDateValid
    }

    // Implementing coroutine to check if the date with these parameters exist on a separate thread.
    // Didn't find a proper use-case to implement, so just using already obtained validations to be assigned onto livedata
    // and observed by the MainActivity.
    fun checkIfBothPanAndDateAreValidated(): LiveData<ListOfValidation>{

        GlobalScope.launch(Dispatchers.Default) {
            isValidLivedata.postValue(ListOfValidation(isPanValid, isBirthDateValid))
        }

        return isValidLivedata
    }

}