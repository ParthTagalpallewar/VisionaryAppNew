package com.reselling.visionary.ui.home.homeBookDetails.sellerDetails

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.reselling.visionary.R
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.databinding.FragmentHomeSellerDetailsBinding
import com.reselling.visionary.ui.home.homeBookDetails.HomeBookDetailsViewModel
import com.reselling.visionary.utils.internetExceptionString
import com.reselling.visionary.utils.snackBar
import com.reselling.visionary.utils.visible

class HomeSellerDetails : Fragment(R.layout.fragment_home_seller_details),
    SellerBooksAdapter.OnItemClickListener {

    private val binding: FragmentHomeSellerDetailsBinding by viewBinding()
    val viewModel: HomeBookDetailsViewModel by  activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         viewModel.books.observe(viewLifecycleOwner){
            binding.bookLocation.text = it.address

             binding.mapsLayout.setOnClickListener {
                 findNavController().navigate(R.id.mapsFragment)
             }
        }






        val mAdapter = SellerBooksAdapter(this)

        binding.apply {
            recyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                hasFixedSize()
                adapter = mAdapter
            }


        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.seller.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        val seller = it.value.user
                        binding.apply {
                            sellerName.text = seller.name
                        }
                    }

                    is Resource.Failure -> {
                        requireView().snackBar(it.errorBody)
                    }

                    is Resource.NoInterException -> {
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
            viewModel.sellerBooks.observe(viewLifecycleOwner) {

                binding.addBookProgressBar.visible(false)
                when (it) {
                    is Resource.Success -> {
                        val books = it.value.books
                        binding.apply {
                            sellerBooksQty.text = books.size.toString()
                            mAdapter.submitList(books)
                        }
                    }

                    is Resource.Failure -> {
                        requireView().snackBar(it.errorBody)
                    }

                    is Resource.NoInterException -> {
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

    override fun onItemClick(book: Books) {

        val bundle = bundleOf("details_books_information" to book)
        findNavController().navigate(R.id.homeBookDetails, bundle, (NavOptions.Builder().setPopUpTo(R.id.homeSellerDetails,true)).build())
    }
}