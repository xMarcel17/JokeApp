package com.example.jokeapp.fragments

import ApiService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jokeapp.R
import com.example.jokeapp.adapters.JokesAdapter
import com.example.jokeapp.database.FavoriteJokesDatabase
import com.example.jokeapp.databinding.FragmentJokeListBinding
import com.example.jokeapp.model.Joke
import com.example.jokeapp.model.JokesResponse
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class JokeListFragment : Fragment(), Callback<JokesResponse>, JokesAdapter.JokeClickListener {

    private lateinit var binding: FragmentJokeListBinding
    // ignore the error with the JokeListFragmentArgs if it is shown -
    // try to invalidate the cache and restart the IDE
    private val args: JokeListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJokeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        // Hide the pending indicator
        binding.pendingIndicator.visibility = View.INVISIBLE
        // Check if the category is "Favorites"
        if (args.category == getString(R.string.favorites)) {
            // hide the query cardview - it is not needed for the favorites category
            binding.queryCardview.visibility = View.GONE
            // get jokes from the database
            getJokesFromDB()
        } else {
            // Set the click listener for the get jokes button
            binding.getJokesButton.setOnClickListener {
                // call the getJokesFromAPI function with the category as the parameter to get
                // the jokes for provided category and other query parameters
                getJokesFromAPI(args.category)
            }
        }
    }

    private fun resolveBlackList(): String? {
        // Resolve the blacklist part of the query based on the selected chips.
        // If no chips are selected, return null (this query part is omitted).
        // Otherwise, create a string builder that will hold the selected flags separated by commas.
        if (binding.blacklistGroup.checkedChipIds.isEmpty()) return null
        val stringBuilder = StringBuilder()
        for (id in binding.blacklistGroup.checkedChipIds) {
            val chip = binding.root.findViewById<Chip>(id)
            stringBuilder.append("${chip.text},")
        }
        // Return the string with the trailing comma removed
        return stringBuilder.trim(',').toString()
    }

    private fun resolveAmount(): Int {
        // Resolve the amount part of the query based on the selected chip.
        // The corresponding amount is stored in the text attribute of the chip.
        // We get the selected chip id with the checkedChipId property of the ChipGroup and
        // then get the text of the chip with the id. We enclose the operation in a try-catch block
        // to handle the case when the checkedChipId is not set or the text cannot be converted to
        // an integer. If exceptions occur, we return the default value of 4.
        return try {
            binding.root.findViewById<Chip>(binding.amountGroup.checkedChipId).text.toString()
                .toInt()
        } catch (e: Exception) {
            4
        }
    }

    private fun resolveType(): String? {
        // Resolve the type part of the query based on the selected chips
        // If both chips are selected the type part of query should be omitted
        // Otherwise, return the selected type
        return if (binding.typeSingleChip.isChecked && binding.typeTwopartChip.isChecked)
            null
        else {
            if (binding.typeSingleChip.isChecked)
                getString(R.string.single_joke_type)
            else
                getString(R.string.twopart_joke_type)
        }
    }

    private fun getJokesFromAPI(category: String) {
        // Get the blacklist, amount, and type from the selected chips
        val blacklist = resolveBlackList()
        val amount = resolveAmount()
        val type = resolveType()
        // Create an instance of the ApiService interface to make the network call
        val apiCall = RetrofitClient.getClient().create(ApiService::class.java)
            .getJoke(category, amount, blacklist, type)
        // Show the pending indicator and disable the get jokes button while the network call is in progress
        binding.pendingIndicator.visibility = View.VISIBLE
        binding.getJokesButton.isEnabled = false
        // Enqueue the call to make the network request asynchronously and handle the response
        apiCall.enqueue(this)
    }

    private fun getJokesFromDB() {
        // Show the pending indicator
        binding.pendingIndicator.visibility = View.VISIBLE
        // Get the database instance and its DAO
        val databaseDao = FavoriteJokesDatabase.getDatabase(requireContext()).jokesDatabaseDao()
        // Use the lifecycleScope to launch a coroutine that will get the
        // favorite jokes from the database
        // in asynchronous manner. LifecycleScope is a part of the
        // AndroidX lifecycle library that provides a coroutine scope tied to
        // the lifecycle of the fragment or activity.
        lifecycleScope.launch {
            // Get the list of favorite jokes from the database
            val favJokes = databaseDao.getFavoriteJokes()
            // Submit the list of favorite jokes to the adapter
            (binding.jokesRecyclerView.adapter as JokesAdapter).submitList(favJokes)
            // Hide the pending indicator
            binding.pendingIndicator.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        // setup recyclerview
        with(binding.jokesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            // Create an instance of the JokesAdapter and set it as the adapter for the RecyclerView
            // The JokeListFragment is set as the click listener for the jokes
            adapter = JokesAdapter(this@JokeListFragment)
        }
    }

    override fun onResponse(call: Call<JokesResponse>, response: Response<JokesResponse>) {
        // Hide the pending indicator and enable the get jokes button again
        binding.pendingIndicator.visibility = View.GONE
        binding.getJokesButton.isEnabled = true
        if (response.isSuccessful) {
            // Get the response body - JokesResponse object
            val jokesResponse = response.body()
            // Check if the response is not null
            jokesResponse?.let {
                // Check if there is no error in the response
                if (!jokesResponse.error) {
                    // Submit the list of jokes to the adapter
                    (binding.jokesRecyclerView.adapter as JokesAdapter).submitList(
                        jokesResponse.jokes
                    )
                    // Show a snackbar with the number of jokes received
                    // if it is different from the requested amount
                    if (resolveAmount() != jokesResponse.jokes?.size) {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.jokes_number_info,jokesResponse.jokes?.size),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // Show a snackbar with the additional info from the response in case
                    // of an error set to true
                    Snackbar.make(
                        binding.root, jokesResponse.additionalInfo.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            // Show an error message if the response is not successful
            binding.getJokesButton.isEnabled = true
            Snackbar.make(
                binding.root, getString(R.string.retrofit_error_msg, response.message()),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onFailure(call: Call<JokesResponse>, t: Throwable) {
        // Handle the failure of the network request
        binding.pendingIndicator.visibility = View.GONE
        binding.getJokesButton.isEnabled = true
        Snackbar.make(
            binding.root,
            getString(R.string.retrofit_error_msg, t.message),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onJokeClick(joke: Joke) {
        // show JokeDialog with the selected joke
        val dialog = JokeDialog(joke)
        dialog.show(parentFragmentManager, "joke_dialog")
        // if the category is Favorites we add the implementation of the "Dislike" behavior
        // in the JokeDialog. This way we can update the displayed the list each time a "dislike"
        // is chosen while displaying Favorites.
        if (args.category == getString(R.string.favorites)) {
            dialog.setListener(object : JokeDialog.JokeDialogListener {
                override fun onDatabaseDelete() {
                    getJokesFromDB()
                }
            })
        }
    }
}