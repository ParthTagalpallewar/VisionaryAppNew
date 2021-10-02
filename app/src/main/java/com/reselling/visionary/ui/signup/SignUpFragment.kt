package com.reselling.visionary.ui.signup

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentSignupBinding
import com.reselling.visionary.ui.MainActivity
import com.reselling.visionary.utils.internetExceptionString
import com.reselling.visionary.utils.move
import com.reselling.visionary.utils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "SignUpFragment"

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private val viewModel: SignUpViewModel by activityViewModels()
    private val binding: FragmentSignupBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            etName.setText(viewModel.name)
            etMobile.setText(viewModel.phoneNumber)
            etPassword.setText(viewModel.password)
            edEmail.setText(viewModel.email)
            ccpCountryCode.setDefaultCountryUsingNameCode(viewModel.countryCode)


            etName.addTextChangedListener {
                viewModel.name = it.toString()
            }

            etMobile.addTextChangedListener {
                viewModel.phoneNumber = it.toString()
            }

            etPassword.addTextChangedListener {
                viewModel.password = it.toString()
            }

            edEmail.addTextChangedListener {
                viewModel.email = it.toString()
            }

            ccpCountryCode.setOnCountryChangeListener {
                viewModel.countryCode = ccpCountryCode.selectedCountryCode
            }

            btnSignup.setOnClickListener {
                viewModel.signUpBtnClicked()
            }

            signInTv.setOnClickListener {

                findNavController().navigate(
                    R.id.loginFragment, null,
                    NavOptions.Builder().setPopUpTo(R.id.signUpFragment, true).build()
                )
            }


        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.signUpEvent.collect {

                binding.progressBar.isVisible =
                    (it is SignUpViewModel.SignUpFragmentEvents.LoadingEvent)

                when (it) {
                    is SignUpViewModel.SignUpFragmentEvents.ShowInvalidInputMessage -> {
                        requireView().snackBar(it.msg)
                    }

                    is SignUpViewModel.SignUpFragmentEvents.NavigateToHomeFragment -> {
                        requireView().snackBar("Account Created Successfully")

                        viewLifecycleOwner.lifecycleScope.launch { delay(500) }

                        requireActivity().move(MainActivity::class.java)

                    }

                    is SignUpViewModel.SignUpFragmentEvents.NavigateToVerifyCodeFragment -> {
                        SignUpFragmentDirections.actionSignUpFragmentToVerifyCodeFragment().apply {
                            findNavController().navigate(this)
                        }


                    }
                    is SignUpViewModel.SignUpFragmentEvents.ObserveSignUpResponse -> {
                        viewModel.handelSignUpResponse(it.response)
                    }

                    is SignUpViewModel.SignUpFragmentEvents.InternetProblem -> {
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