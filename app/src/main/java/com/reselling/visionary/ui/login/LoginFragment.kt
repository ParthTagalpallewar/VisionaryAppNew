package com.reselling.visionary.ui.login

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentLoginBinding
import com.reselling.visionary.ui.MainActivity
import com.reselling.visionary.utils.internetExceptionString
import com.reselling.visionary.utils.move
import com.reselling.visionary.utils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private val binding: FragmentLoginBinding by viewBinding()

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart: Welcome to LoginFragment ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val currentDestination = findNavController().currentDestination?.id

            binding.apply {


                edPassword.addTextChangedListener {
                    viewModel.password = it.toString()
                }

                edPassword.setText(viewModel.password)
                edMobile.setText(viewModel.phoneNumber)
                ccpCountryCode.setDefaultCountryUsingNameCode(viewModel.countryCode)


                txtForgot.setOnClickListener {
                    viewModel.forgotPasswordButttonClicked()
                }

                //saving mob no in viewModel
                edMobile.addTextChangedListener {
                    viewModel.phoneNumber = it.toString()
                }

                ccpCountryCode.setOnCountryChangeListener {
                    viewModel.countryCode = ccpCountryCode.selectedCountryCode
                }

                //sign Btn click kele ki method call hoil
                btnSingin.setOnClickListener {
                    viewModel.signInBtnClicked()
                }

                btnSignup.setOnClickListener {
                    viewModel.signUpTextViewClick()
                }


            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.loginEvent.collect {
                    binding.progressBar.isVisible =
                        it is LoginViewModel.LoginFragmentEvent.LoadingEvent

                    when (it) {

                        is LoginViewModel.LoginFragmentEvent.ShowInvalidInputMessage -> {

                            requireView().snackBar(it.msg)
                        }

                        is LoginViewModel.LoginFragmentEvent.NavigateToMainFragment -> {

                            requireActivity().move(MainActivity::class.java)


                        }


                        is LoginViewModel.LoginFragmentEvent.NavigateToForgetPasswordFragment -> {
                            if (currentDestination == R.id.loginFragment) {
                                LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
                                    .apply {
                                        findNavController().navigate(this)
                                    }
                            }
                        }

                        is LoginViewModel.LoginFragmentEvent.NavigateToSignUpFragment -> {
                            if (currentDestination == R.id.loginFragment) {
                                LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
                                    .apply {
                                        findNavController().navigate(this)
                                    }
                            }
                        }
                        is LoginViewModel.LoginFragmentEvent.NavigateToForgetPasswordManualFragment -> {
                            if (currentDestination == R.id.loginFragment) {
                                LoginFragmentDirections.actionLoginFragmentToForgotPasswordManualFragment()
                                    .apply {
                                        findNavController().navigate(this)
                                    }
                            }
                        }

                        is LoginViewModel.LoginFragmentEvent.InternetProblem -> {
                            requireView().snackBar(internetExceptionString, "Turn On") { snackBar ->
                                try {
                                    startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                                    snackBar.build().dismiss()
                                } catch (e: Exception) {
                                }

                            }
                        }

                        is LoginViewModel.LoginFragmentEvent.ObserveLoginResponse -> {
                            viewModel.handelLoginResponse(it.response)
                        }


                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "onViewCreated: ${e.message}")
        }


    }


}