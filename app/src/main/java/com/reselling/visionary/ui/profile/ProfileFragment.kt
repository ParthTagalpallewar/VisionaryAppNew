package com.reselling.visionary.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentProfileBinding
import com.reselling.visionary.ui.auth.AuthActivity
import com.reselling.visionary.utils.move
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import okhttp3.internal.notifyAll

private const val TAG = "ProfileFragment"

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    lateinit var binding: FragmentProfileBinding
    val viewModel: ProfileFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        binding.logoutBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                    .setTitle("Do Your Really Want to Logout?")
                    .setPositiveButton("Yes") { dialogInterface, onclick ->
                        viewModel.logOutUser()
                    }.setNegativeButton("No") { Dinterface, onClicked ->
                        Dinterface.dismiss()
                    }.show()

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userData.observe(viewLifecycleOwner) {
                binding.apply {
                    userEmail.text = it.email

                    userNameTop.text = it.name
                    userNameBottom.text = it.name

                    userPhone.text = it.phone
                    userAddress.text = it.address

                    userAddedBooks.text = it.country
                }
            }


            viewModel.profileEvents.collect {
                when (it) {
                    is ProfileFragmentViewModel.ProfileFragmentEvents.NavigateToLoginFragment -> {

                        if (findNavController().currentDestination?.id == R.id.navigation_profile) {
                            requireActivity().move(AuthActivity::class.java)
                            /*findNavController().navigate(R.id.action_navigation_profile_to_loginFragment,
                                    null,
                                    NavOptions.Builder().setPopUpTo(R.id.navigation_profile, true).build())*/
                        }

                    }
                }
            }
        }
    }


}