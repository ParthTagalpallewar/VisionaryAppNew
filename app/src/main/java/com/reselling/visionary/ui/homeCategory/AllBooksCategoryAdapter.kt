package com.reselling.visionary.ui.homeCategory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reselling.visionary.data.models.category.CategoryModel
import com.reselling.visionary.databinding.ItemHomeCategoryBinding

class AllBooksCategoryAdapter(private val listener: CategoryClickListener) :
        ListAdapter<CategoryModel, AllBooksCategoryAdapter.HomeCategoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryViewHolder {
        val binding = ItemHomeCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeCategoryViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class HomeCategoryViewHolder(private val binding: ItemHomeCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.categoryName.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {

                    val categoryName = getItem(bindingAdapterPosition)
                    listener.onCategoryItemClicked(categoryName.name)
                }
            }


        }

        fun bind(category: CategoryModel) {
            binding.apply {
                categoryName.text = category.name

                binding.relativeLayoutBackground.background.colorFilter = category.convertColor(itemView.context)

            }
        }
    }

    interface CategoryClickListener {
        fun onCategoryItemClicked(name: String)
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryModel>() {
        override fun areItemsTheSame(oldItem: CategoryModel, newItem: CategoryModel) =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CategoryModel, newItem: CategoryModel) =
                oldItem == newItem
    }


}