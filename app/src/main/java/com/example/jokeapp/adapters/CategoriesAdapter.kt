package com.example.jokeapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jokeapp.R
import com.example.jokeapp.databinding.CategoryListItemBinding

// Adapter for the list of categories using ListAdapter class for simplified list updates
class CategoriesAdapter: ListAdapter<String, CategoriesAdapter.ViewHolder>(CategoriesDiffCallback){
    // ViewHolder class for the adapter representing the individual items in the list
    inner class ViewHolder(private val binding: CategoryListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(category: String){
            if (category == getString(binding.root.context, R.string.favorites)){
               // TODO
            } else {
              // TODO
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    // Bind the data to the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }
    // Define a listener for the category click event
    private var onCategoryClickListener: CategoryClickListener? = null
    // Method to set the listener
    fun setOnCategoryClickListener(listener: CategoryClickListener){
        onCategoryClickListener = listener
    }
    // Definition of the interface for the category click listener
    interface CategoryClickListener {
        fun onCategoryClick(category: String)
    }
}
// DiffCallback for the ListAdapter to calculate the difference between the old and new items
// to handle updates efficiently
object CategoriesDiffCallback: DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}