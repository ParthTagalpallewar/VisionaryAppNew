package com.reselling.visionary.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentLoginBinding
import com.reselling.visionary.ui.MainActivity
import com.reselling.visionary.utils.move
import com.reselling.visionary.utils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

private const val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()
    private val binding: FragmentLoginBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            ccpCountryCode.setDefaultCountryUsingNameCode("IN")


            //sign Btn click kele ki method call hoil
            btnSingin.setOnClickListener {
                viewModel.signInBtnClicked(
                    phoneNumber = edMobile.text.toString(),
                    countryCode = ccpCountryCode.selectedCountryCode,
                    password = edPassword.text.toString()
                )
            }

            /*Move to SignUp Screen of Click of SingUp Button*/
            btnSignup.setOnClickListener {
                LoginFragmentDirections.actionLoginFragmentToSignUpFragment().apply {
                    findNavController().navigate(this)
                }
            }

            txtForgot.setOnClickListener {
                /* LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment().apply {
                     findNavController().navigate(this)
                 }*/

                LoginFragmentDirections.actionLoginFragmentToForgotPasswordManualFragment().apply {
                    findNavController().navigate(this)
                }
            }


        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { event ->

                binding.progressBar.isVisible =
                    event is LoginViewModel.LoginFragmentEvent.LoadingEvent

                when (event) {

                    is LoginViewModel.LoginFragmentEvent.ShowInvalidInputMessage -> {
                        requireView().snackBar(event.msg)
                    }

                    is LoginViewModel.LoginFragmentEvent.NavigateToMainFragment -> {
                        requireActivity().move(MainActivity::class.java)
                    }

                    else -> {
                        Log.e(TAG, "onViewCreated: Else of Login Fragment is Called")
                    }
                }
            }
        }

    }


}