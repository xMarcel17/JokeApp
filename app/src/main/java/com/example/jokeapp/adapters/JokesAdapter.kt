package com.example.jokeapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jokeapp.R
import com.example.jokeapp.databinding.JokeListItemBinding
import com.example.jokeapp.model.Joke
import substringBeforeNthDelimiter

// Adapter for the list of jokes using ListAdapter class for simplified list updates
// The adapter takes a JokeClickListener interface as a parameter to handle the click events
class JokesAdapter(val listener: JokeClickListener): ListAdapter<Joke, JokesAdapter.ViewHolder>(JokesDiffCallback){
    // ViewHolder class for the adapter representing the individual items in the list
    inner class ViewHolder(private val binding: JokeListItemBinding): RecyclerView.ViewHolder(binding.root){
        // function to bind the data to the ViewHolder and set the click listener
        fun bind(joke: Joke) {
            // Set the category abbreviation
            binding.jokeCategoryAbbr.text = joke.category.substring(0, 2)
            // Set the short text for the joke item. When the joke is a two-part joke,
            // show the part of a setup and when it is a single-part joke,
            // show the part of the joke text
            binding.jokeShort.text =
                if (joke.type == getString(binding.root.context, R.string.twopart_joke_type)) {
                    // Get the first two words of the setup with extension function
                    // created in StringExtensions.kt
                    joke.setup?.substringBeforeNthDelimiter(' ', 2) + "..."
                } else {
                    // Get the first two words of the joke with extension function
                    // created in StringExtensions.kt
                    joke.joke?.substringBeforeNthDelimiter(' ', 2) + "..."
                }
            // Handle the visibility of the flag icon based on the flags set for the joke
            // - if any flag is set show the icon
            if (joke.flags.areNotSet())
                binding.flag.visibility = View.GONE
            else
                binding.flag.visibility = View.VISIBLE
            // Set the click listener for the item
            binding.root.setOnClickListener {
                listener.onJokeClick(joke)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = JokeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val joke = getItem(position)
        holder.bind(joke)
    }
    interface JokeClickListener {
        fun onJokeClick(joke: Joke)
    }

    
}
// DiffCallback for the ListAdapter to calculate the difference between the old and new items
object JokesDiffCallback: DiffUtil.ItemCallback<Joke>(){
    override fun areItemsTheSame(oldItem: Joke, newItem: Joke): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Joke, newItem: Joke): Boolean {
        return oldItem == newItem
    }

}
