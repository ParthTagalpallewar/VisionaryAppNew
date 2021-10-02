package com.reselling.visionary.ui.home.baseHomeFragment

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
import com.reselling.visionary.ui.splash.SplashScreen
import com.reselling.visionary.utils.*
import kotlinx.coroutines.flow.collect

//private const val TAG = "BaseHomeFragment"

class BaseHomeFragment : Fragment(R.layout.fragment_base_home),
    HomeCategoryAdapter.CategoryClickListener, HomeBooksAdapter.onItemClicked {

    private val viewModel: HomeViewModel by activityViewModels()
    private val binding: FragmentBaseHomeBinding by viewBinding()
    private lateinit var mAdapter: HomeBooksAdapter
    private lateinit var mCategoryAdapter: HomeCategoryAdapter

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

        //category
        mCategoryAdapter.submitList(homeCategories)

        //user live location
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

                    is Resource.NoInterException -> {
                        var a = true
                        if (a) {
                            requireView().snackBar(internetExceptionString, "Turn On") { snackBar ->
                                try {
                                    startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                                    snackBar.build().dismiss()
                                    a = false
                                } catch (e: Exception) {
                                }
                            }
                        }

                    }

                }
            }

            viewModel.user.collect {
                binding.userLocation.text = it?.address
            }

        }

        //books
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

                    is Resource.NoInterException -> {
//                        Log.e(TAG, "onViewCreated: Observe No Internet")
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

    override fun onStart() {
        super.onStart()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userFromPrefManager.collect {
                when {
                    (it.location == UserNoLocation) or (it.location == "NO") -> {
                        BaseHomeFragmentDirections.actionHomeFragmentToGpsFragment().apply {
                            findNavController().navigate(this)
                        }
                    }

                }
            }
        }
    }



    override fun onPause() {
        super.onPause()
        viewModel.searchBook("")
    }



    override fun onBookClicked(book: Books) {
        BaseHomeFragmentDirections.actionHomeFragmentToHomeBookDetails(book).apply {
            findNavController().navigate(this)
        }
    }
    override fun onCategoryItemClicked(name: String) {
        BaseHomeFragmentDirections.actionHomeFragmentToCategoryBooks(name).apply {
            findNavController().navigate(this)
        }
    }

}