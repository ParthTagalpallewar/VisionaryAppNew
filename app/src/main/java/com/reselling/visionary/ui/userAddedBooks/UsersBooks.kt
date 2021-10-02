    package com.reselling.visionary.ui.userAddedBooks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.reselling.visionary.R
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.databinding.FragmentUserAddedBooksBinding
import com.reselling.visionary.utils.internetExceptionString
import com.reselling.visionary.utils.snackBar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "UsersBooks"

class UsersBooks : Fragment(R.layout.fragment_user_added_books),
    UsersBooksAdapter.OnItemClickListener {

    private val binding: FragmentUserAddedBooksBinding by viewBinding()
    private val viewModel: UserBooksViewModel by activityViewModels()
    private lateinit var mAdapter: UsersBooksAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = UsersBooksAdapter(this)

        setUpRecyclerView()

        setHasOptionsMenu(true)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.bookEvents.collect { events ->
                binding.progressBar.isVisible = (events == UserBooksViewModel.UserAddedBooksEvent.LoadingEvent)
                when (events) {
                    is UserBooksViewModel.UserAddedBooksEvent.ShowInvalidInputMessage -> {
                        requireView().snackBar(events.msg)
                    }

                    is UserBooksViewModel.UserAddedBooksEvent.ShowDataInRecyclerView -> {
                        val books = events.books
                        mAdapter.submitList(books)
                        if (books.isNotEmpty()){
                            binding.recyclerView. scrollToPosition(books.size)
                        }

                    }

                    is UserBooksViewModel.UserAddedBooksEvent.InternetProblem -> {
                        requireView().snackBar(internetExceptionString, "Ok") { snackBar ->
                            try {
                                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
                                snackBar.build().dismiss()
                            } catch (e: Exception) {
                            }

                        }
                    }

                    is UserBooksViewModel.UserAddedBooksEvent.BookDeletedSuccessEvent -> {
                        viewModel.getBooks()

                        requireView().snackBar("Book Deleted Successfully")


                    }

                }
            }
        }

    }


    private fun setUpRecyclerView() {
        // viewModel.getBooks()

        binding.apply {
            recyclerView.apply {
                adapter = mAdapter
                scrollToPosition(bottom)
                layoutManager = LinearLayoutManager(requireContext())
                hasFixedSize()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.books.observe(viewLifecycleOwner) {
                binding.booksTvNulldata.isVisible = it.size <= 0
                mAdapter.submitList(it)
            }
        }



    }


    @SuppressLint("SetTextI18n")
    override fun onItemClick(book: Books) {

        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.BottomSheetDialogueTheme)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_user_books)
        bottomSheetDialog.show()

        bottomSheetDialog.apply {

            //BookName
            findViewById<TextView>(R.id.bookName)?.text = book.bookName

            //Book Selling Prize
            findViewById<TextView>(R.id.sellingPrize)?.text = "₹"+book.bookSelling

            //Book Original Prize
            findViewById<TextView>(R.id.OriginalPrize)?.text = "₹"+book.bookOriginal

            //Book Description
            findViewById<TextView>(R.id.description)?.text = book.bookDesc

            //Location
            findViewById<TextView>(R.id.address)?.text = book.address

            //Delete Btn
            findViewById<ImageView>(R.id.deleteBtn)?.setOnClickListener {

                AlertDialog.Builder(requireContext())
                        .setTitle("Do Your Really Want to Delete Book")
                        .setPositiveButton("Yes") { dialogInterface, onclick ->
                            viewModel.deleteBook(book.id)
                            dismiss()
                        }.setNegativeButton("No") { Dinterface, onClicked ->
                            Dinterface.dismiss()
                            dismiss()
                        }.show()

            }
        }


    }

    override fun onStart() {
        super.onStart()
        viewModel.getBooks()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)


    }

}