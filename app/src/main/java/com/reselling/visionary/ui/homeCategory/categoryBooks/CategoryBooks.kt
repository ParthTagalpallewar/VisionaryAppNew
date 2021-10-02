package com.reselling.visionary.ui.homeCategory.categoryBooks

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.reselling.visionary.R
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.databinding.FragmentHomeCategoryBooksBinding
import com.reselling.visionary.utils.*
import kotlinx.android.synthetic.main.fragment_home_all_books.*

class CategoryBooks : Fragment(R.layout.fragment_home_category_books),
    CategoryBooksAdapter.OnItemClickListener {

    private val binding: FragmentHomeCategoryBooksBinding by viewBinding()
    private val viewModel: CategoryBooksViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryName = requireArguments().getString("selected_category")

        viewModel.setCategory(categoryName!!)

        val mAdapter = CategoryBooksAdapter(this)


        (context as AppCompatActivity).setSupportActionBar(toolbar)
        binding.apply {
            toolbar.title = categoryName

            recyclerView.apply {
                hasFixedSize()
                adapter = mAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
            }

        }

        viewModel.catgoryBooks.observe(viewLifecycleOwner) {
            binding.progressBar.visible(false)

            when (it) {
                is Resource.Success -> {

                    binding.textViewEmpty.isVisible =(it.value.books.size <= 0)
                    mAdapter.submitList(it.value.books)
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

        setHasOptionsMenu(true)

    }

    override fun onBookClicked(book: Books) {
        CategoryBooksDirections.actionCategoryBooksToHomeBookDetails(book).apply {
            findNavController().navigate(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView


        searchView.searchQuery { query ->
            binding.recyclerView.scrollToPosition(0)
            viewModel.searchBooks(query)
        }

    }


    override fun onPause() {
        super.onPause()
        viewModel.searchBooks("")
    }
}