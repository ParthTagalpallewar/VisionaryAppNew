package com.reselling.visionary.ui.home.homeBookDetails.bookDetails

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentHomeBookDetailsBinding
import com.reselling.visionary.ui.home.homeBookDetails.HomeBookDetailsViewModel

class HomeBookDetails : Fragment(R.layout.fragment_home_book_details) {

    val binding: FragmentHomeBookDetailsBinding by viewBinding()
    val viewModel: HomeBookDetailsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.books.observe(viewLifecycleOwner) {
            binding.apply {
                bookDescription.text = it.bookDesc
                bookSellingPrize.text = it.sellingMark
                bookOriginalPrize.text = it.originalMark
            }
        }


    }
}