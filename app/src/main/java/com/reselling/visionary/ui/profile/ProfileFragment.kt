package com.reselling.visionary.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentProfileBinding
import com.reselling.visionary.ui.auth.AuthActivity
import com.reselling.visionary.utils.move
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    lateinit var binding: FragmentProfileBinding
    val viewModel: ProfileFragmentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        binding.logoutBtn.setOnClickListener {

            // alert dialog for logout conformation
            AlertDialog.Builder(requireContext())
                    .setTitle("Do Your Really Want to Logout?")
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.logOutUser() // on click of yes logout user
                    }.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss() // on click of no btn dismiss dialog
                    }.show()

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            // get user data form database and show
            viewModel.userData.collect{
                binding.apply {
                    userEmail.text = it.email

                    userNameTop.text = it.name
                    userNameBottom.text = it.name

                    userPhone.text = it.phone
                    userAddress.text = it.getUserAddress

                    userAddedBooks.text = it.country ?: "Not Found"
                }
            }


            viewModel.profileEvents.collect {
                when (it) {
                    is ProfileFragmentViewModel.ProfileFragmentEvents.NavigateToLoginFragment -> {

                        if (findNavController().currentDestination?.id == R.id.navigation_profile) {
                            requireActivity().move(AuthActivity::class.java)

                        }

                    }
                }
            }
        }
    }


}