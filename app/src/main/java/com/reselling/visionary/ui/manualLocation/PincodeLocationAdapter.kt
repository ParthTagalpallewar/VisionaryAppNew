package com.reselling.visionary.ui.manualLocation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reselling.visionary.data.models.manualLocation.ManualLocation
import com.reselling.visionary.databinding.ItemLocationPincodeBinding

class PincodeLocationAdapter(private val onItemClickListener: OnItemClickListener) :
        ListAdapter<ManualLocation, PincodeLocationAdapter.PinCodeViewHolder>(DiffCallback()) {


    inner class PinCodeViewHolder(private val binding: ItemLocationPincodeBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        onItemClickListener.onItemClick(task)
                    }
                }
            }
        }

               fun  bindData(location: ManualLocation){
                   binding.apply {
                    city.text = location.Name
                    district.text = location.District
                   }
               }

    }

    interface OnItemClickListener {
        fun onItemClick(loc: ManualLocation)
    }

    class DiffCallback : DiffUtil.ItemCallback<ManualLocation>() {
        override fun areItemsTheSame(oldItem: ManualLocation, newItem: ManualLocation) =
                oldItem.Country == newItem.Country

        override fun areContentsTheSame(oldItem: ManualLocation, newItem: ManualLocation) =
                oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinCodeViewHolder {
        val binding = ItemLocationPincodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PinCodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PinCodeViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bindData(currentItem)
    }
}