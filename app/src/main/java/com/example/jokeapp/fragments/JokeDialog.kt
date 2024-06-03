package com.example.jokeapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.jokeapp.R
import com.example.jokeapp.database.FavoriteJokesDatabase
import com.example.jokeapp.databinding.JokeDialogBinding
import com.example.jokeapp.model.Joke
import kotlinx.coroutines.launch

class JokeDialog(val joke: Joke) : DialogFragment() {
    private lateinit var binding: JokeDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = JokeDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.let {
            // Set the width of the dialog to 90% of the screen width
            // this way we can change the dialogs default width
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
            it.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT) // apply the width
            // Set the background of the dialog to be transparent
            it.setBackgroundDrawableResource(android.R.color.transparent)
        }
        binding.likeButton.setOnClickListener {
            // handle like button click
            saveToDatabase()
        }
        binding.dislikeButton.setOnClickListener {
            // handle dislike button click
            removeFromDatabase()
        }
        // show the joke in the dialog
        showJoke()
    }

    private fun showJoke() {
        // set the title of the joke.
        // A string template <string name="joke_title">%1$s: Joke %2$d</string>
        // with the category and id of the joke is used
        binding.jokeTitle.text = getString(R.string.joke_title, joke.category, joke.id)
        // check the type of the joke and set the text accordingly
        if(joke.type == getString(R.string.twopart_joke_type)) {
            // For two-part jokes, show the setup and delivery
            with(binding.jokeSetup){
                visibility = android.view.View.VISIBLE
                text = joke.setup
            }
            binding.joke.text = joke.delivery
        }
        else{
            // For single-part jokes, show the joke text only,
            // the joke setup is hidden by default
            binding.joke.text = joke.joke
        }
    }

    private fun saveToDatabase() {
        // Get access to the database DAO
        val databaseDao =
            FavoriteJokesDatabase.getDatabase(requireContext()).jokesDatabaseDao()
        // Launch a coroutine to insert the joke into the database
        lifecycleScope.launch {
            try {
                // try to insert the joke into the database and
                // store the row ID of the inserted joke in the variable
                val insertJoke = databaseDao.insertJoke(joke)
                // If the insertJoke value is -1, the joke is already in the database.
                // Show a Toast message
                // for both cases but with different messages
                if(insertJoke != -1L)
                    Toast.makeText(requireContext(),
                        getString(R.string.saved_to_favorites_msg), Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireContext(),
                        getString(R.string.already_in_favorites_msg),
                        Toast.LENGTH_SHORT).show()
            }
            catch (e: Exception){
                // If an exception occurs during the database operation,
                // show a Toast message with the error
                Toast.makeText(requireContext(),
                    getString(R.string.db_save_fail_msg, e.message), Toast.LENGTH_LONG).show()
            }
            // close the dialog
            dismiss()
        }
    }

    // Interface to handle the database delete operation
    interface JokeDialogListener {
        fun onDatabaseDelete()
    }
    private var listener: JokeDialogListener? = null
    fun setListener(listener: JokeDialogListener){
        this.listener = listener
    }

    private fun removeFromDatabase() {
        // Get access to the database DAO
        val databaseDao =
            FavoriteJokesDatabase.getDatabase(requireContext()).jokesDatabaseDao()
        // Launch a coroutine to delete the joke from the database
        lifecycleScope.launch {
            // call the deleteJoke function from the DAO and store the number of deleted jokes
            val deletedJokes = databaseDao.deleteJoke(joke)
            // Show a toast message based on the number of deleted jokes.
            // Value 0 means the joke was not in the database and the
            // Toast message will not be shown
            if(deletedJokes > 0) {
                Toast.makeText(requireContext(),
                    getString(R.string.deleted_from_favorites_msg), Toast.LENGTH_SHORT)
                    .show()
                // Call the onDatabaseDelete function to notify the listener
                // that the joke was deleted
                listener?.onDatabaseDelete()
            }
            // close the dialog
            dismiss()
        }
    }
}
