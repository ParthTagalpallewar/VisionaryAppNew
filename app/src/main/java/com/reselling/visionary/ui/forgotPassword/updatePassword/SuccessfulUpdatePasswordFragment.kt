package com.reselling.visionary.ui.forgotPassword.updatePassword

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentUpdatePasswordBinding
import com.reselling.visionary.databinding.FragmentUpdatePasswordSuccessfulBinding

class SuccessfulUpdatePasswordFragment : Fragment(R.layout.fragment_update_password_successful) {

    private val binding: FragmentUpdatePasswordSuccessfulBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            Update.setOnClickListener {
                findNavController().navigate(R.id.action_successfulUpdatePasswordFragment_to_loginFragment,
                        null,
                        NavOptions.Builder().setPopUpTo(R.id.successfulUpdatePasswordFragment, true).build())
            }
        }
    }

}
