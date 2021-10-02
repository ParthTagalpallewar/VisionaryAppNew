package com.reselling.visionary.ui.home.baseHomeFragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.reselling.visionary.R
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.databinding.ItemHomeBooksBinding

class HomeBooksAdapter(val listener:onItemClicked) : ListAdapter<Books, HomeBooksAdapter.BookViewHolder>(DiffCallBack()) {

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemHomeBooksBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return BookViewHolder(binding)
    }

    inner class BookViewHolder(private val binding: ItemHomeBooksBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION){
                        val book = getItem(bindingAdapterPosition)
                        listener.onBookClicked(book)
                    }
                }
            }

        }



        @SuppressLint("SetTextI18n")
        fun bind(book: Books) {
            binding.apply {

                try {
                    Glide.with(itemView)
                            .load(book.imageUrl)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .error(R.drawable.home_bg_book)
                            .into(imageView)
                } catch (e: Exception) {
                }

                bookName.text = book.bookName

                sellingPrize.text = book.sellingMark

                originalPrize.apply {
                    text = book.originalMark
                    paint.isStrikeThruText = true
                }

                address.text = book.address
            }


        }
    }

    class DiffCallBack : DiffUtil.ItemCallback<Books>() {
        override fun areItemsTheSame(oldItem: Books, newItem: Books) =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Books, newItem: Books) =
                oldItem == newItem
    }

    interface onItemClicked{
        fun onBookClicked(book: Books)
    }


}