package com.stripe.aod.sampleapp.model

import android.telephony.PhoneNumberUtils
import android.text.Editable
import androidx.lifecycle.ViewModel
import com.stripe.aod.sampleapp.data.CustomerInputValidation
import com.stripe.aod.sampleapp.data.toMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class CustomerViewModel : ViewModel() {

    private val emailRegex = "^[A-Za-z\\d+_.-]+@[A-Za-z\\d.-]+\$"


    private val _name: MutableStateFlow<String> = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _phone: MutableStateFlow<String> = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _validInputs: MutableStateFlow<CustomerInputValidation> =
        MutableStateFlow(CustomerInputValidation())
    private val validInputs: StateFlow<CustomerInputValidation> = _validInputs.asStateFlow()

    private val _formIsValid: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val formIsValid: StateFlow<Boolean> = _formIsValid.asStateFlow()

    fun setName(value: Editable?) {
        _validInputs.update {
            _validInputs.value.copy(
                name = (value?.toString()?.length ?: 0) > 2
            )
        }
        _name.update { value.toString() }
        checkValid()
    }

    fun setEmail(value: Editable?) {
        _validInputs.update {
            _validInputs.value.copy(
                email = value?.toString()?.matches(emailRegex.toRegex()) ?: false
            )
        }
        _email.update { value.toString() }
        checkValid()
    }

    fun setPhone(value: Editable?) {
        if (!value?.toString().isNullOrEmpty()) {
            val formatted = PhoneNumberUtils.formatNumberToE164(value.toString(), "GB")
            _validInputs.update {
                _validInputs.value.copy(
                    phone = PhoneNumberUtils.isGlobalPhoneNumber(formatted)
                )
            }
            println(formatted)
            if (!formatted.isNullOrEmpty()) {
                _phone.update { formatted }
            }
            checkValid()
        }

    }


    private fun checkValid() {
        val isValid = !validInputs.value.toMap().containsValue(false)
        println(validInputs.value.toMap())
        _formIsValid.update { isValid }

    }


}