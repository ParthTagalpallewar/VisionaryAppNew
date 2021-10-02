package com.reselling.visionary.ui.home.homeBookDetails

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.reselling.visionary.R
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.data.models.userModel.User
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.databinding.FragmentHomeDetailsBaseBinding
import com.reselling.visionary.utils.*

private const val TAG = "HomeDetailsBase"

class HomeDetailsBase : Fragment(R.layout.fragment_home_details_base) {

    private val binding: FragmentHomeDetailsBaseBinding by viewBinding()
    private val viewModel: HomeBookDetailsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val book: Books = arguments?.getParcelable<Books>("details_books_information") as Books

        viewModel.books.postValue(book)
        viewModel.getSeller(book.userId)
        viewModel.getSellerBooks(book.userId)

        binding.apply {


            (context as AppCompatActivity).setSupportActionBar(toolbar)

            viewPager.adapter = BooksDetailsAdapter(this@HomeDetailsBase)

            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = this@HomeDetailsBase.getTabTitle(position)
            }.attach()


            Glide
                .with(requireView())
                .load(book.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.home_bg_book)
                .into(bookImage)


            bookTitle.text = book.bookName
            bookCategory.text = book.bookCatagory


        }

        viewModel.seller.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val seller = it.value.user
                    binding.apply {
                        contactSeller(seller)
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

    private fun contactSeller(seller: User) {

        binding.speedDial.apply {
            addActionItem(
                SpeedDialActionItem.Builder(R.id.menu_contact_gmail, R.drawable.gmail)
                    .setLabel(getString(R.string.ContactGmail))
                    .setTheme(R.style.SpeedDial_fabColor)
                    .create()
            )

            addActionItem(
                SpeedDialActionItem.Builder(R.id.menu_contact_phone, R.drawable.ic_phone)
                    .setLabel(getString(R.string.ContactPhone))
                    .setTheme(R.style.SpeedDial_fabColor)
                    .create()
            )

            addActionItem(
                SpeedDialActionItem.Builder(R.id.menu_contact_whatsapp, R.drawable.whatsapp)
                    .setLabel(getString(R.string.ContactWhatsApp))
                    .setTheme(R.style.SpeedDial_fabColor)
                    .create()
            )


            setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
                when (actionItem.id) {
                    R.id.menu_contact_whatsapp -> {
                        requireContext().sendUserToWhatsApp(seller.phone)
                    }
                    R.id.menu_contact_phone -> {
                        requireContext().sendUserToContact(seller.phone)
                    }
                    R.id.menu_contact_gmail -> {
                        requireContext().sendUserToMail(seller.email)
                    }
                    else -> {
                        requireView().snackBar("Invalid Option Selected")
                    }
                }

                this.close()
                return@OnActionSelectedListener true

            })
        }


    }
}
