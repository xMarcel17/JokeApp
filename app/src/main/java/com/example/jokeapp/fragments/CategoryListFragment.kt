package com.example.jokeapp.fragments

import ApiService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.jokeapp.R
import com.example.jokeapp.adapters.CategoriesAdapter
import com.example.jokeapp.databinding.FragmentCategoryListBinding
import com.example.jokeapp.model.CategoriesResponse
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CategoryListFragment : Fragment(), Callback<CategoriesResponse>,
    CategoriesAdapter.CategoryClickListener {

    private lateinit var binding: FragmentCategoryListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up the RecyclerView with the CategoriesAdapter
        with(binding.categoriesRecyclerView) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = CategoriesAdapter()
            visibility = android.view.View.INVISIBLE
        }
        // Fetch the categories from the API when the Fragment is created
        binding.pendingIndicator.visibility = View.VISIBLE
        // Create a Retrofit instance and call the getCategories() function to fetch the categories
        val retrofit = RetrofitClient.getClient().create(ApiService::class.java)
        val call = retrofit.getCategories()
        // Enqueue the call to make the network request asynchronously and handle the response
        call.enqueue(this)
    }

    // Handle the response when it is successful
    override fun onResponse(call: Call<CategoriesResponse>, response:
    Response<CategoriesResponse>) {
        // Hide the pending indicator and show the RecyclerView
        binding.categoriesRecyclerView.visibility = View.VISIBLE
        binding.pendingIndicator.visibility = View.GONE
        // Check if the response is successful
        if (response.isSuccessful) {
            // Get the list of categories from the response
            val categoriesResponse = response.body()
            val categoriesList = handleCategoriesResponse(categoriesResponse)
            // Update the RecyclerView with the list of categories if it is not empty
            if (categoriesList.isNotEmpty()) {
                with((binding.categoriesRecyclerView.adapter as CategoriesAdapter)) {
                    submitList(categoriesList)
                    // Set the fragment as the click listener implementation for the categories
                    setOnCategoryClickListener(this@CategoryListFragment)
                }
            }
        } else {
            // Show an error message if the response is not successful
            Snackbar.make(
                binding.root,
                getString(R.string.retrofit_error_msg, response.message()),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun handleCategoriesResponse(categoriesResponse: CategoriesResponse?): List<String>
    {
        // Make sure the categories are not null
        categoriesResponse?.categories?.let {
            // Map the categories to show the aliases for the "Misc" category.
            // The misc category is accessible through Miscellaneous path
            // map is a method that transforms the list of categories to a new list
            val mappedListCopy = it.map { category ->
                if (category == "Misc") {
                    // Get the alias for the "Misc" category from the response
                    // find is a method that returns the first element matching
                    // the predicate. If no element is found, it returns null. We use the
                    // elvis operator ?: to return the original category if the alias is null
                    categoriesResponse.categoryAliases.find {
                            categoryAlias -> categoryAlias.resolved == category }?.alias
                        ?: category
                } else {
                    // Return the category as is if it is not "Misc"
                    category
                }
            }.toMutableList() // Convert the list to a mutable list so we can add the
                              // "Favorites" category
            mappedListCopy.add(getString(R.string.favorites))
            return mappedListCopy
        }
        // Return an empty list if the categories are null
        return emptyList()
    }


    // handle the failure of the network request
    override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
        binding.pendingIndicator.visibility = View.GONE
        Snackbar.make(
            binding.root,
            getString(R.string.retrofit_error_msg, t.message),
            Snackbar.LENGTH_LONG
        ).show()
    }

    // Navigate to the JokeListFragment when a category is clicked
    override fun onCategoryClick(category: String) {
        // Create the action to navigate to the JokeListFragment with the selected category
        // The action is created using the generated directions from the navigation graph
        val action =
            CategoryListFragmentDirections.actionCategoryListFragmentToJokeListFragment(category)
        // Navigate to the JokeListFragment
        findNavController().navigate(action)
    }

}