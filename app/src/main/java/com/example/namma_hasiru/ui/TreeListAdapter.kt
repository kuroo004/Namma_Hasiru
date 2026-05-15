package com.example.namma_hasiru.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.namma_hasiru.data.TreeEntry
import com.example.namma_hasiru.databinding.ItemTreeBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TreeListAdapter(private val onTreeClick: (TreeEntry) -> Unit) :
    ListAdapter<TreeEntry, TreeListAdapter.TreeViewHolder>(TreeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        val binding = ItemTreeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TreeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TreeViewHolder(private val binding: ItemTreeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tree: TreeEntry) {
            binding.tvTreeSpecies.text = tree.speciesName
            binding.tvTreeStatus.text = "Status: ${tree.healthStatus}"
            
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            binding.tvTreeDate.text = "Planted: ${sdf.format(Date(tree.datePlanted))}"

            if (tree.day1PhotoUri.isNotEmpty()) {
                Glide.with(binding.ivTreeThumbnail.context)
                    .load(File(tree.day1PhotoUri))
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .centerCrop()
                    .into(binding.ivTreeThumbnail)
            } else {
                binding.ivTreeThumbnail.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            binding.root.setOnClickListener { onTreeClick(tree) }
        }
    }

    class TreeDiffCallback : DiffUtil.ItemCallback<TreeEntry>() {
        override fun areItemsTheSame(oldItem: TreeEntry, newItem: TreeEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TreeEntry, newItem: TreeEntry): Boolean {
            return oldItem == newItem
        }
    }
}
