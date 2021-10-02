package com.reselling.visionary.ui.forgotPasswordManual

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.databinding.FragmentForgotPasswordManualBinding
import com.reselling.visionary.ui.login.LoginFragmentDirections
import com.reselling.visionary.ui.signup.SignUpViewModel
import com.reselling.visionary.utils.internetExceptionString
import com.reselling.visionary.utils.snackBar
import kotlinx.coroutines.flow.collect

private const val TAG = "ForgotPasswordManualFra"
class ForgotPasswordManualFragment : Fragment(R.layout.fragment_forgot_password_manual) {
    val binding: FragmentForgotPasswordManualBinding by viewBinding()
    val viewModel: ForgotPasswordManualViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            etName.setText(viewModel.name)
            etMobile.setText(viewModel.phoneNumber)
            edEmail.setText(viewModel.email)
            ccpCountryCode.setDefaultCountryUsingNameCode(viewModel.countryCode)


            etName.addTextChangedListener {
                viewModel.name = it.toString()
            }

            etMobile.addTextChangedListener {
                viewModel.phoneNumber = it.toString()
            }


            edEmail.addTextChangedListener {
                viewModel.email = it.toString()
            }

            ccpCountryCode.setOnCountryChangeListener {
                viewModel.countryCode = ccpCountryCode.selectedCountryCode
            }

            btnSignup.setOnClickListener {
                viewModel.verifyUserBtnClicked()
            }


        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.forgotPasswordManualEvents.collect {
                binding.progressBar.isVisible = it is ForgotPasswordManualViewModel.ForgotPasswordManualEvents.LoadingEvent

                when (it) {
                    is ForgotPasswordManualViewModel.ForgotPasswordManualEvents.ShowInvalidInputMessage -> {
                        requireView().snackBar(it.msg)
                    }
                    is ForgotPasswordManualViewModel.ForgotPasswordManualEvents.ObserveForgotPasswordManualResponse -> {


                        when (it.response) {
                            is Resource.NoInterException -> {
                                requireView().snackBar(internetExceptionString, "Turn On") { snackBar ->
                                    try {
                                        startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                                        snackBar.build().dismiss()
                                    } catch (e: Exception) {
                                    }

                                }
                            }

                            is Resource.Failure -> {
                                requireView().snackBar(it.response.errorBody)
                            }
                            is Resource.Success -> {

                                if (it.response.value.user.id != "0") {
                                    viewModel.savePhoneNumberInPrefManager(it.response.value.user.phone)
                                    if (findNavController().currentDestination?.id == R.id.forgotPasswordManualFragment) {
                                        ForgotPasswordManualFragmentDirections.actionCategoryBooksToUpdatePasswordFrgment().apply {
                                            findNavController().navigate(this)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}