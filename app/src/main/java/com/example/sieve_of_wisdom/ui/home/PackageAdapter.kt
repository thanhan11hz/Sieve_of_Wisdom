package com.example.sieve_of_wisdom.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sieve_of_wisdom.data.model.Package
import com.example.sieve_of_wisdom.databinding.ItemQuizPackageBinding

class PackageAdapter(
    private val onPackageClick: (Package) -> Unit
) : ListAdapter<Package, PackageAdapter.PackageViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PackageViewHolder {

        val binding = ItemQuizPackageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PackageViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class PackageViewHolder(
        private val binding: ItemQuizPackageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pkg: Package) {

            binding.tvPackageTitle.text =
                pkg.name
        
            binding.tvPackageQuestions.text =
                "▣   30 câu hỏi"
        
            binding.tvPackageTime.text =
                "◷   60 giây"
        
            binding.btnPackageStart.text =
                "Bắt đầu"
        
            binding.btnPackageStart.setOnClickListener {
                onPackageClick(pkg)
            }
        
            binding.quizPackageCard.setOnClickListener {
                onPackageClick(pkg)
            }
        }
    }

    companion object {

        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Package>() {

                override fun areItemsTheSame(
                    oldItem: Package,
                    newItem: Package
                ): Boolean {
                    return oldItem.categoryId ==
                        newItem.categoryId
                }

                override fun areContentsTheSame(
                    oldItem: Package,
                    newItem: Package
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}