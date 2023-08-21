package com.stripe.aod.sampleapp.data

data class CreateCustomerParams(
    val email: String,
    val name: String,
    val phone: String
)

fun CreateCustomerParams.toMap(): Map<String, String> {
    return mapOf(
        "email" to email,
        "name" to name,
        "phone" to phone
    )
}
