package com.reselling.visionary.ui.userAddedBooks

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.reselling.visionary.R
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.databinding.ItemUserBooksBinding

private const val TAG = "UsersBooksAdapter"

class UsersBooksAdapter(private val listener: OnItemClickListener) :
        ListAdapter<Books, UsersBooksAdapter.UserBooksViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBooksViewHolder {
        val binding = ItemUserBooksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserBooksViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserBooksViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class UserBooksViewHolder(private val binding: ItemUserBooksBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                relativeLayoutBackground.setOnClickListener {

                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {

                        val book = getItem(bindingAdapterPosition)
                        listener.onItemClick(book)
                    }
                }

            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(book: Books) {
            binding.apply {
                Glide.with(itemView.context).load(book.imageUrl).error(R.drawable.home_bg_book).into(bookImage)
                bookSellingPrize.text = "₹"+book.bookSelling
                bookName.text = book.bookName




            }


        }
    }

    interface OnItemClickListener {
        fun onItemClick(book: Books)
    }

    class DiffCallback : DiffUtil.ItemCallback<Books>() {
        override fun areItemsTheSame(oldItem: Books, newItem: Books) =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Books, newItem: Books) =
                oldItem == newItem
    }
}