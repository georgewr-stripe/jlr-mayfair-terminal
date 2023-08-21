package com.stripe.aod.sampleapp.data

data class CustomerInputValidation(
    var name: Boolean = false,
    var email: Boolean = false,
    var phone: Boolean = false
)

fun CustomerInputValidation.toMap(): Map<String, Boolean> {
    return mapOf(
        "name" to name,
        "email" to email,
        "phone" to phone,
    )
}