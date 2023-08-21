package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.data.CreateCustomerParams
import com.stripe.aod.sampleapp.data.CreatePaymentParams
import com.stripe.aod.sampleapp.databinding.FragmentCustomerBinding
import com.stripe.aod.sampleapp.model.CheckoutViewModel
import com.stripe.aod.sampleapp.model.CustomerViewModel
import com.stripe.aod.sampleapp.utils.hideKeyboard
import com.stripe.aod.sampleapp.utils.launchAndRepeatWithViewLifecycle
import com.stripe.aod.sampleapp.utils.navOptions
import com.stripe.aod.sampleapp.utils.setThrottleClickListener
import com.stripe.stripeterminal.external.models.PaymentIntentStatus


class CustomerFragment : Fragment(R.layout.fragment_customer) {
    private val checkoutViewModel by viewModels<CheckoutViewModel>()
    private val customerViewModel by viewModels<CustomerViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewBinding = FragmentCustomerBinding.bind(view)


        class PhoneNumberWatcher : PhoneNumberFormattingTextWatcher("GB") {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                customerViewModel.setPhone(s)
            }
        }

        viewBinding.phoneInputEdit.addTextChangedListener(
            PhoneNumberWatcher()
        )

        viewBinding.back.setThrottleClickListener {
            findNavController().navigateUp()
        }

        viewBinding.emailInputEdit.doAfterTextChanged {
            customerViewModel.setEmail(it)
        }


        viewBinding.nameInputEdit.doAfterTextChanged {
            customerViewModel.setName(it)
        }

        launchAndRepeatWithViewLifecycle {
            customerViewModel.formIsValid.collect { valid ->
                viewBinding.customerCreate.isEnabled = valid
            }

        }


        viewBinding.customerCreate.setThrottleClickListener {
            viewBinding.customerCreate.isEnabled = false
            viewBinding.emailInputEdit.hideKeyboard()
            viewBinding.nameInputEdit.hideKeyboard()
            viewBinding.phoneInputEdit.hideKeyboard()

            checkoutViewModel.createCustomer(CreateCustomerParams(
                name = customerViewModel.name.value,
                email = customerViewModel.email.value,
                phone = customerViewModel.phone.value
            ), onSuccess = { customerId ->

                checkoutViewModel.createPaymentIntent(CreatePaymentParams(
                    amount = 500000,
                    customer = customerId,
                    currency = "gbp",
                    description = "Range Rover EV Reservation",
                ), onFailure = { message ->
                    viewBinding.customerCreate.isEnabled = true

                    Snackbar.make(
                        viewBinding.customerCreate, message.value.ifEmpty {
                            getString(R.string.error_fail_to_create_payment_intent)
                        }, Snackbar.LENGTH_SHORT
                    ).show()
                })

            }, onFailure = { message ->
                viewBinding.customerCreate.isEnabled = true

                Snackbar.make(
                    viewBinding.customerCreate, message.value.ifEmpty {
                        getString(R.string.error_fail_to_create_customer)
                    }, Snackbar.LENGTH_SHORT
                ).show()
            })
        }

        launchAndRepeatWithViewLifecycle {
            checkoutViewModel.currentPaymentIntent.collect { paymentIntent ->
                paymentIntent?.takeIf {
                    it.status == PaymentIntentStatus.REQUIRES_CAPTURE
                }?.let {
                    findNavController().navigate(
                        CustomerFragmentDirections.actionCustomerFragmentToSuccessFragment(
                            customerViewModel.name.value
                        ),
                        navOptions()
                    )
                }
            }
        }
    }
}
