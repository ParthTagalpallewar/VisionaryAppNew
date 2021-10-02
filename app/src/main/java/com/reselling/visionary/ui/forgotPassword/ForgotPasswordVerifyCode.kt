package com.reselling.visionary.ui.forgotPassword

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentForgotPasswordCodeVerificationBinding
import com.reselling.visionary.utils.internetExceptionString
import com.reselling.visionary.utils.snackBar
import kotlinx.coroutines.flow.collect

class ForgotPasswordVerifyCode : Fragment(R.layout.fragment_forgot_password_code_verification) {


    private val viewModel: ForgotPasswordViewModel by activityViewModels()
    private lateinit var binding: FragmentForgotPasswordCodeVerificationBinding

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentForgotPasswordCodeVerificationBinding.bind(view)


        binding.apply {

            etOtp.setText(viewModel.otp)
            tvEnterOtp.text =
                "Enter one time password send to \n ${viewModel.countryCode}${viewModel.phoneNumber}"

            etOtp.addTextChangedListener {
                viewModel.otp = it.toString()
            }

            cancelButton.setOnClickListener {
                findNavController().popBackStack(R.id.loginFragment, false)
            }

            btnVerifyCode.setOnClickListener {
                it.isEnabled = false
                viewModel.verifyCode()
            }


        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.changePasswordEvents.collect {

                binding.progressBar.isVisible =
                    (it is ForgotPasswordViewModel.ChangePasswordEvents.LoadingEvent)

                when (it) {
                    is ForgotPasswordViewModel.ChangePasswordEvents.ShowInvalidInputMessage -> {
                        binding.btnVerifyCode.isEnabled = true

                        if (getString(R.string.pinVerifyError).contains(it.msg)) {
                            requireView().snackBar("Pin Does Not Match")
                        } else {
                            //Log.d("TAG", it.msg)
                            requireView().snackBar(it.msg)
                        }


                    }

                    is ForgotPasswordViewModel.ChangePasswordEvents.NavigateToUpdatePasswordFragment -> {
                        findNavController().navigate(R.id.action_forgotPasswordVerifyCode_to_updatePasswordFragment,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.forgotPasswordVerifyCode, true)
                                .build()
                        )


                    }

                    is ForgotPasswordViewModel.ChangePasswordEvents.InternetProblem -> {
                        binding.btnVerifyCode.isEnabled = true
                        requireView().snackBar(internetExceptionString, "Turn On") { snackBar ->
                            try {
                                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                                snackBar.build().dismiss()
                            } catch (e: Exception) {
                            }

                        }
                    }


                }
            }
        }

    }
}