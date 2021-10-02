package com.reselling.visionary.ui.homeCategory.categoryBooks


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.reselling.visionary.R
import com.reselling.visionary.data.models.books.Books
import com.reselling.visionary.databinding.ItemHomeAllBooksBinding

private const val TAG = "CategoryBooksAdapter"

class CategoryBooksAdapter(private val clickListener: OnItemClickListener) :
    ListAdapter<Books, CategoryBooksAdapter.BooksViewHolder>(BooksChecker()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        return BooksViewHolder(
            ItemHomeAllBooksBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
//            Log.e(TAG, "onBindViewHolder: ${currentItem.bookName}  positon${position} ")
        }
    }

    inner class BooksViewHolder(private val binding: ItemHomeAllBooksBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    val item = getItem(bindingAdapterPosition)
                    if (item != null) {
                        clickListener.onBookClicked(item)
                    }

                }
            }
        }

        fun bind(book: Books) {
            binding.apply {
                Glide.with(itemView)
                    .load(book.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.home_bg_book)
                    .into(imageView)

                bookName.text = book.bookName
                bookLocation.text = book.address

                sellingPrize.text = book.sellingMark

                originalPrize.apply {
                    text = book.originalMark
                    paint.isStrikeThruText = true
                }


            }
        }
    }

    interface OnItemClickListener {
        fun onBookClicked(book: Books)
    }


    class BooksChecker : DiffUtil.ItemCallback<Books>() {
        override fun areItemsTheSame(oldItem: Books, newItem: Books) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Books, newItem: Books) =
            oldItem == newItem
    }


}