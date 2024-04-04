package com.example.jokeapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jokeapp.databinding.JokeListItemBinding
import com.example.jokeapp.model.Joke

// Adapter for the list of jokes using ListAdapter class for simplified list updates
// The adapter takes a JokeClickListener interface as a parameter to handle the click events
class JokesAdapter(val listener: JokeClickListener): ListAdapter<Joke, JokesAdapter.ViewHolder>(JokesDiffCallback){
    // ViewHolder class for the adapter representing the individual items in the list
    inner class ViewHolder(private val binding: JokeListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(joke: Joke){

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
