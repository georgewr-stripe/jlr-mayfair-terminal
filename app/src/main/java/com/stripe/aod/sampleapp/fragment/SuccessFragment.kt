package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentSuccessBinding
import com.stripe.aod.sampleapp.utils.navOptions
import com.stripe.aod.sampleapp.utils.setThrottleClickListener

class SuccessFragment : Fragment(R.layout.fragment_success) {
    private val args: SuccessFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentSuccessBinding.bind(view)

        viewBinding.successMessage.text =
            getString(R.string.success_message, args.name.split(" ")[0]).uppercase()

        viewBinding.successScreen.setThrottleClickListener {
            findNavController().navigate(
                R.id.action_successFragment_to_homeFragment,
                null,
                navOptions()
            )
        }

//        viewBinding.receiptPrint.isEnabled = false
//        viewBinding.receiptSms.isEnabled = false
//        viewBinding.receiptEmail.setThrottleClickListener {
//            findNavController().navigate(
//                ReceiptFragmentDirections.actionReceiptFragmentToEmailFragment(
//                    args.paymentIntentID
//                ),
//                navOptions()
//            )
//        }
//
//        viewBinding.receiptSkip.setOnClickListener {
//            backToHome()
//        }
    }
}
