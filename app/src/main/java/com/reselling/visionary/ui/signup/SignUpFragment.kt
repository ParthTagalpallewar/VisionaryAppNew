package com.reselling.visionary.ui.signup

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentSignupBinding
import com.reselling.visionary.ui.MainActivity
import com.reselling.visionary.utils.move
import com.reselling.visionary.utils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "SignUpFragment"

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_signup) {

    /*Creating Binding and viewModel using property delegates*/
    private val viewModel: SignUpViewModel by activityViewModels()
    private val binding: FragmentSignupBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            ccpCountryCode.setDefaultCountryUsingNameCode("IN")

            /*On Click of SingUp Button Send SignUp Request*/
            btnSignup.setOnClickListener {
                viewModel.signUpBtnClicked(
                    name = etName.text.toString(),
                    phoneNumber = etMobile.text.toString(),
                    password = etMobile.text.toString(),
                    countryCode = ccpCountryCode.selectedCountryCode,
                    email = edEmail.text.toString(),
                )
            }

            /*On Click of SingIn Button Go to SignUp Page*/
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

                    else -> {
                        Log.e(TAG, "onViewCreated: Else Branch of Observing Channel")
                    }

                }
            }
        }


    }
}