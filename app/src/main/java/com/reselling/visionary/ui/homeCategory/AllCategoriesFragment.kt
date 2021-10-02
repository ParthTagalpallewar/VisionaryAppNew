package com.reselling.visionary.ui.homeCategory

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.reselling.visionary.R
import com.reselling.visionary.databinding.FragmentBooksCategoryBinding
import com.reselling.visionary.utils.allCategories
import com.reselling.visionary.utils.toast

class AllCategoriesFragment: Fragment(R.layout.fragment_books_category),AllBooksCategoryAdapter.CategoryClickListener {
    private val binding :FragmentBooksCategoryBinding by viewBinding()
    private val viewModel:CategoriesViewModels by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mAdapter = AllBooksCategoryAdapter(this)

        binding.apply {
            recyclerView.apply {
                hasFixedSize()
                adapter = mAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
            }
        }

        mAdapter.submitList(allCategories)
    }

    override fun onCategoryItemClicked(name: String) {
        AllCategoriesFragmentDirections.actionAllCategoriesFragmentToCategoryBooks(name).apply {
            findNavController().navigate(this)
        }
    }
}