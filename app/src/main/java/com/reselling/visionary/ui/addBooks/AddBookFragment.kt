package com.reselling.visionary.ui.addBooks

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentAddBookBinding
import com.reselling.visionary.utils.getCategory
import com.reselling.visionary.utils.internetExceptionString
import com.reselling.visionary.utils.snackBar
import com.reselling.visionary.utils.visible
import kotlinx.coroutines.flow.collect


//private const val TAG = "AddBookFragment"

class AddBookFragment : Fragment(R.layout.fragment_add_book) {

    val binding: FragmentAddBookBinding by viewBinding()
    val viewModel: AddBookViewModel by activityViewModels()


   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            homeLocationLayout.setOnClickListener {
                AddBookFragmentDirections.actionNavigationAddToGpsFragment().apply {
                    findNavController().navigate(this)
                }
            }
        }

        val requestMultiplePermissions =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    viewModel.handelPermissionResult()
                }

        val galleryImageUriRegistration = registerForActivityResult(AddBookViewModel.GetImageFromGalleryContract()) {
            viewModel.handelImageUri(it)
        }


        binding.apply {
            bookName.addTextChangedListener {
                viewModel.bookName = it.toString()
            }

            bookDescription.addTextChangedListener {
                viewModel.bookDescription = it.toString()
            }

            bookOriginalPrize.addTextChangedListener {
                viewModel.bookOriginalPrize = it.toString()
            }

            bookSellingPrize.addTextChangedListener {
                viewModel.bookSellingPrize = it.toString()
            }

            bookCategory.setItems(getCategory)
            bookCategory.setOnItemSelectedListener { _, _, _, item ->
                viewModel.bookCategory = item.toString()
            }

            bookImage.setOnClickListener {
                viewModel.addImageBtnClicked()
            }

            addBookBtn.setOnClickListener {
                viewModel.addBookBtnClicked()
            }
        }



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.addBookEvents.collect { events ->


                events.apply {

                   // binding.addBookProgressBar.isVisible =
                         //   this == AddBookViewModel.AddBookEvents.LoadingEvent

                    when (this) {

                        is AddBookViewModel.AddBookEvents.RequestPermission -> {
                            requestMultiplePermissions.launch(permissions)
                        }

                        is AddBookViewModel.AddBookEvents.RerequestPermission -> {
                            requireView().snackBar(
                                    "Please Allow Permissions To Add Book Image",
                                    "Ok"
                            ) {
                                viewModel.addImageBtnClicked()
                            }
                        }

                        is AddBookViewModel.AddBookEvents.AddBookImageEvent -> {
                          //  Log.e(TAG, "onViewCreated: receive Add image event")
                            galleryImageUriRegistration.launch(1)
                        }

                        is AddBookViewModel.AddBookEvents.ShowImage -> {
                            binding.apply {
                                addBookImageviewIcon.visible(false)
                                addBookTvImgAdd.visible(false)
                                bookImage.setImageURI(uri)
                            }
                        }

                        is AddBookViewModel.AddBookEvents.ShowInvalidInputMessage -> {
                            requireView().snackBar(msg)
                        }
                        is AddBookViewModel.AddBookEvents.InternetProblem -> {
                            requireView().snackBar(internetExceptionString, "Ok") {

                                try {
                                    startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                                } catch (e: Exception) {
                                }

                            }
                        }

                        is AddBookViewModel.AddBookEvents.AddedBookEvent -> {
                            requireView().snackBar("Book Added Successfully", "See Book") {
                                try {
                                    AddBookFragmentDirections.actionNavigationAddToUsersBooks().apply {
                                        findNavController().navigate(this)
                                    }
                                    findNavController().navigate(R.id.usersBooks)
                                } catch (e: Exception) {
                                }

                            }

                           binding.apply {
                               bookName.setText("")
                               bookImage.setImageURI("".toUri())
                               bookSellingPrize.setText("")
                               bookDescription.setText("")
                               bookOriginalPrize.setText("")
                               addBookImageviewIcon.visible()
                               addBookTvImgAdd.visible()
                           }

                        }

                        is AddBookViewModel.AddBookEvents.AddLocation -> {
                            binding.location.text = address
                        }

                    }

                }
            }

        }
    }


    override fun onStart() {
        super.onStart()
        viewModel.addLocation()
    }
}

