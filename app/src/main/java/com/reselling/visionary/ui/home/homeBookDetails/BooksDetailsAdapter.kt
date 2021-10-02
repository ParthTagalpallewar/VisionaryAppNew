package com.reselling.visionary.ui.home.homeBookDetails

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.reselling.visionary.ui.home.homeBookDetails.bookDetails.HomeBookDetails
import com.reselling.visionary.ui.home.homeBookDetails.sellerDetails.HomeSellerDetails

const val MY_HOME_BOOK_DETAILS = 0
const val MY_HOME_SELLER_DETAILS = 1

class BooksDetailsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {


    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        MY_HOME_BOOK_DETAILS to { HomeBookDetails() },
        MY_HOME_SELLER_DETAILS to { HomeSellerDetails() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}
