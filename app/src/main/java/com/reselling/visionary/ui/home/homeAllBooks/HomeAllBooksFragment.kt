package com.reselling.visionary.ui.home.homeAllBooks

import android.os.Bundle
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
import androidx.paging.LoadState
import com.reselling.visionary.R
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.databinding.FragmentHomeAllBooksBinding
import com.reselling.visionary.utils.searchQuery

private const val TAG = "HomeAllBooksFragment"

class HomeAllBooksFragment : Fragment(R.layout.fragment_home_all_books),
    AllBooksUserAdapter.OnItemClickListener {

    private val binding: FragmentHomeAllBooksBinding by viewBinding()
    private val viewModel: HomeAllBooksViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mAdapter = AllBooksUserAdapter(this)

        binding.apply {
            recyclerView.apply {
                setHasFixedSize(true)
                itemAnimator = null
                adapter = mAdapter.withLoadStateHeaderAndFooter(
                    header = BookLoadStateAdapter { mAdapter.retry() },
                    footer = BookLoadStateAdapter { mAdapter.retry() }
                )
            }

            buttonRetry.setOnClickListener { mAdapter.retry() }
        }

        viewModel.books.observe(viewLifecycleOwner) {
            it.observe(viewLifecycleOwner) { pagingBooks ->
                mAdapter.submitData(viewLifecycleOwner.lifecycle, pagingBooks)
            }

        }

        mAdapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    mAdapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }

        (requireContext() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.searchQuery { query ->
            binding.recyclerView.scrollToPosition(0)
            viewModel.searchPhotos(query)
        }

    }


    override fun onBookClicked(book: Books) {
        HomeAllBooksFragmentDirections.actionHomeAllBooksFragmentToHomeBookDetails(book).apply {
            findNavController().navigate(this)
        }
    }
}