package com.example.sieve_of_wisdom.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sieve_of_wisdom.Dialog.PurchaseDialog
import com.example.sieve_of_wisdom.data.model.Package
import com.example.sieve_of_wisdom.databinding.ItemTopicCardBinding
import com.example.sieve_of_wisdom.ui.viewmodel.PackageViewModel

class TopicCardAdapter(
    private val onUnlockClick: (Package) -> Unit
) :
    ListAdapter<Package, TopicCardAdapter.StoreViewHolder>(DIFF_CALLBACK) {

    // ViewHolder
    inner class StoreViewHolder(
        private val binding: ItemTopicCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Package) {
            binding.tvTitle.text = item.name
            binding.tvStars.text = item.price.toString()
            binding.tvQuestions.text = "${item.quantity} câu hỏi"
            binding.tvDuration.text = "${item.quantity * 3} giây"

            if (item.isUnlocked) {
                binding.btnUnlock.visibility = View.INVISIBLE
                binding.btnUnlock.isEnabled = false

                binding.tvBannerLocked.visibility = View.GONE
                binding.tvBannerUnLocked.visibility = View.VISIBLE
            } else {
                binding.btnUnlock.visibility = View.VISIBLE
                binding.btnUnlock.isEnabled = true

                binding.tvBannerUnLocked.visibility = View.GONE
                binding.tvBannerLocked.visibility = View.VISIBLE

                binding.btnUnlock.setOnClickListener {
                    onUnlockClick(item)
                }

            }
        }
    }


    // Create ViewHolder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoreViewHolder {

        val binding = ItemTopicCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return StoreViewHolder(binding)
    }

    // Bind data
    override fun onBindViewHolder(
        holder: StoreViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Package>() {

            // Is this the same Package?
            override fun areItemsTheSame(
                oldItem: Package,
                newItem: Package
            ): Boolean {
                return oldItem.name == newItem.name
            }

            // Did the Package's contents change?
            override fun areContentsTheSame(
                oldItem: Package,
                newItem: Package
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}