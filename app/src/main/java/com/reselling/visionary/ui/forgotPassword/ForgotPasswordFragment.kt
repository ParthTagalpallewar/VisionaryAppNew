package com.reselling.visionary.ui.forgotPassword

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
import com.reselling.visionary.databinding.FragmentForgotPasswordBinding
import com.reselling.visionary.utils.internetExceptionString
import com.reselling.visionary.utils.snackBar
import kotlinx.coroutines.flow.collect

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {


    private val viewModel: ForgotPasswordViewModel by activityViewModels()
    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentForgotPasswordBinding.bind(view)
        binding.apply {


            edMobile.apply {
                setText(viewModel.phoneNumber)

                addTextChangedListener {
                    viewModel.phoneNumber = it.toString()
                }
            }

            back.setOnClickListener {
                findNavController().navigateUp()
            }
            countryCodePicker.apply {
                setDefaultCountryUsingNameCode(viewModel.countryCode)

                setOnCountryChangeListener {
                    viewModel.countryCode = selectedCountryCode
                }
            }

            nextBtn.setOnClickListener {
                viewModel.nextBtnClicked(requireActivity())
            }
        }



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.changePasswordEvents.collect {

                binding.progressBar.isVisible =
                        (it is ForgotPasswordViewModel.ChangePasswordEvents.LoadingEvent)

                when (it) {
                    is ForgotPasswordViewModel.ChangePasswordEvents.ShowInvalidInputMessage -> {
                        requireView().snackBar(it.msg)
                    }
                    is ForgotPasswordViewModel.ChangePasswordEvents.InternetProblem -> {
                        requireView().snackBar(internetExceptionString, "Turn On") { snackBar ->
                            try {
                                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                                snackBar.build().dismiss()
                            } catch (e: Exception) {
                            }

                        }
                    }

                    is ForgotPasswordViewModel.ChangePasswordEvents.NavigateToVerifyCodeFragment -> {

                        if(findNavController().currentDestination?.id == R.id.forgotPasswordFragment){
                            ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToForgotPasswordVerifyCode().apply {
                                findNavController().navigate(this)
                            }
                        }


                      /*  findNavController().navigate(R.id.action_forgotPasswordFragment_to_forgotPasswordVerifyCode,
                                null,
                                NavOptions.Builder().setPopUpTo(R.id.forgotPasswordFragment, true).build())*/

                    }
                    is ForgotPasswordViewModel.ChangePasswordEvents.NavigateToUpdatePasswordFragment -> {
                        ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToUpdatePasswordFragment().apply {
                            findNavController().navigate(R.id.action_forgotPasswordFragment_to_updatePasswordFragment, null,
                                    NavOptions.Builder().setPopUpTo(R.id.forgotPasswordFragment, false).build())
                        }

                    }


                }
            }
        }
    }
}