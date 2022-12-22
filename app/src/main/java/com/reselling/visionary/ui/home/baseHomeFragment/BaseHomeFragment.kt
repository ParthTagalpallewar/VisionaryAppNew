package com.reselling.visionary.ui.home.baseHomeFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.reselling.visionary.R
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.databinding.FragmentBaseHomeBinding
import com.reselling.visionary.utils.*
import kotlinx.coroutines.flow.collect

//private const val TAG = "BaseHomeFragment"

class BaseHomeFragment : Fragment(R.layout.fragment_base_home),
    HomeCategoryAdapter.CategoryClickListener, HomeBooksAdapter.onItemClicked {

    private val viewModel: HomeViewModel by activityViewModels()
    private val binding: FragmentBaseHomeBinding by viewBinding()

    private lateinit var mAdapter: HomeBooksAdapter // recycler adapter for showing books
    private lateinit var mCategoryAdapter: HomeCategoryAdapter //recycler adapter for showing categories

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = HomeBooksAdapter(this)
        mCategoryAdapter = HomeCategoryAdapter(this)

        binding.apply {

            categoryViewAll.setOnClickListener {
                BaseHomeFragmentDirections.actionHomeFragmentToAllCategoriesFragment().apply {
                    findNavController().navigate(this)
                }
            }

            userLocationRelative.setOnClickListener {
                BaseHomeFragmentDirections.actionHomeFragmentToGpsFragment().apply {
                    findNavController().navigate(this)
                }
            }

            booksViewAll.setOnClickListener {
                BaseHomeFragmentDirections.actionHomeFragmentToHomeAllBooksFragment().apply {
                    findNavController().navigate(this)
                }
            }

            booksRecyclerView.apply {
                hasFixedSize()
                adapter = mAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            categoryRecyclerView.apply {
                hasFixedSize()
                adapter = mCategoryAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
            }


            searchView.search { query ->
                viewModel.searchBook(query)
            }


        }

        //adding categories in categories recycler view
        mCategoryAdapter.submitList(homeCategories)

        // live data of all books
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.booksLiveData.observe(viewLifecycleOwner) {

                binding.progressBar.visible(false)

                when (it) {
                    is Resource.Success -> {
                        binding.noBooksTextView.isVisible = it.value.books.size <= 0
                        mAdapter.submitList(it.value.books)

                    }

                    is Resource.Failure -> {
                        requireView().snackBar(it.errorBody)
                    }

                }
            }

            // getting user address from root and setting it on address textView
            viewModel.user.collect { user ->
                binding.userLocation.text = user.getUserAddress
            }

        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.searchBook("")
    }

    // handle click of book item of book recycler view
    override fun onBookClicked(book: Books) {
        BaseHomeFragmentDirections.actionHomeFragmentToHomeBookDetails(book).apply {
            findNavController().navigate(this)
        }
    }


    // handle click of category item of category recycler view
    override fun onCategoryItemClicked(name: String) {
        BaseHomeFragmentDirections.actionHomeFragmentToCategoryBooks(name).apply {
            findNavController().navigate(this)
        }
    }

}