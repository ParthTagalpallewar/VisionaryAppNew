package com.reselling.visionary.ui.verifyCode

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
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentCodeVerificationBinding
import com.reselling.visionary.ui.MainActivity
import com.reselling.visionary.ui.signup.SignUpViewModel
import com.reselling.visionary.utils.internetExceptionString
import com.reselling.visionary.utils.move
import com.reselling.visionary.utils.snackBar
import com.reselling.visionary.utils.visible
import kotlinx.coroutines.flow.collect

private const val TAG = "VerifyCodeFragment"

class VerifyCodeFragment : Fragment(R.layout.fragment_code_verification) {

    private lateinit var binding: FragmentCodeVerificationBinding
    private val viewModel: SignUpViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCodeVerificationBinding.bind(view)

        binding.apply {


        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.signUpEvent.collect {

                binding.progressBar.isVisible =
                    (it is SignUpViewModel.SignUpFragmentEvents.LoadingEvent)

                when (it) {
                    is SignUpViewModel.SignUpFragmentEvents.ShowInvalidInputMessage -> {
                        binding.btnVerifyCode.isEnabled = true
                        requireView().snackBar(it.msg)
                    }

                    is SignUpViewModel.SignUpFragmentEvents.NavigateToHomeFragment -> {
                        requireActivity().move(MainActivity::class.java)

                    }

                    is SignUpViewModel.SignUpFragmentEvents.HideResendBtn -> {

                        binding.btnResendCode.visible(false)
                    }

                    is SignUpViewModel.SignUpFragmentEvents.ObserveSignUpResponse -> {
                        viewModel.handelSignUpResponse(it.response)
                    }

                    is SignUpViewModel.SignUpFragmentEvents.InternetProblem -> {
                        requireView().snackBar(internetExceptionString, "Ok") { snackBar ->
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

