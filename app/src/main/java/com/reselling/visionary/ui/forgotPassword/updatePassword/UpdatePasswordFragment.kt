package com.reselling.visionary.ui.forgotPassword.updatePassword

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
import com.reselling.visionary.databinding.FragmentUpdatePasswordBinding
import com.reselling.visionary.utils.internetExceptionString
import com.reselling.visionary.utils.snackBar
import kotlinx.coroutines.flow.collect

class UpdatePasswordFragment : Fragment(R.layout.fragment_update_password) {

    private val viewModel: UpdatePasswordViewModel by activityViewModels()
    private val binding: FragmentUpdatePasswordBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            cancel.setOnClickListener {

                findNavController().popBackStack(R.id.loginFragment, false)
            }

            edNewPass.apply {
                setText(viewModel.newPassword)
                addTextChangedListener {
                    viewModel.newPassword = it.toString()
                }
            }
            edConfirmPass.apply {
                setText(viewModel.confirmPassword)
                addTextChangedListener {
                    viewModel.confirmPassword = it.toString()
                }
            }

            Update.setOnClickListener {
                viewModel.updatePassword()
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.changePasswordEvents.collect {
                binding.progressBar.isVisible = it is UpdatePasswordViewModel.UpdatePasswordEvents.LoadingEvent

                when(it){

                    is UpdatePasswordViewModel.UpdatePasswordEvents.UpdatePasswordResponse-> {
                        viewModel.handleUpdateResponse(it.response)
                    }

                    is UpdatePasswordViewModel.UpdatePasswordEvents.ShowInvalidInputMessage-> {
                        requireView().snackBar(it.msg)
                    }
                    is UpdatePasswordViewModel.UpdatePasswordEvents.InternetProblem -> {
                        requireView().snackBar(internetExceptionString, "Turn On") { snackBar ->
                            try {
                                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                                snackBar.build().dismiss()
                            } catch (e: Exception) {
                            }
                        }
                    }
                    is UpdatePasswordViewModel.UpdatePasswordEvents.NavigateToSuccessFragment -> {
                        findNavController().navigate(R.id.action_updatePasswordFragment_to_successfulUpdatePasswordFragment,
                                null,
                                NavOptions.Builder().setPopUpTo(R.id.updatePasswordFragment, true).build())
                    }
                }
            }
        }


    }
}