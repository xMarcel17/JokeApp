import com.example.jokeapp.model.CategoriesResponse
import com.example.jokeapp.model.JokesResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// The RetrofitClient object is a singleton class that creates a Retrofit// instance to access the API
object RetrofitClient {
    // Base URL for the API
    private const val BASE_URL = "https://v2.jokeapi.dev/"
    // Retrofit instance to access the API
    private var retrofit: Retrofit? = null

    // Function to build (if not already built) and return the Retrofit instance
    fun getClient(): Retrofit {

        // If retrofit is null, create a new instance
    if (retrofit == null) {
        // Create a Moshi instance for JSON parsing KotlinJsonAdapterFactory is used to
        // automatically generate the adapter for the data classes
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()  // Create a Retrofit instance with the BASE_URL and Moshi converter// using the builder pattern
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
// Return the Retrofit instance
    return retrofit!!
}
}

// The ApiService interface defines the endpoints for the API
interface ApiService {
    // GET request to fetch the Categories from the API. Call is used to make the network request// asynchronously and return the response in a callback
    @GET("categories")
    fun getCategories(): Call<CategoriesResponse>
    // GET request to fetch the Jokes from the API based on the
// category, amount and optional blacklist flags, and type fields (marked with ? operator)// Category is part of the URL path (@Path annotation), amount is a query (@Query) parameter,// and blacklist flags and type are optional query parameters.
// @Query parameters are used to pass
// key-value pairs in the URL query string e.g. ?amount=value1&type=value2// The response is either a list of Jokes or an Error wrapped in a JokesResponse object // retrieved asynchronously with a Call
// Examples:
// https://v2.jokeapi.dev/joke/Programming?type=single&amount=2
// https://v2.jokeapi.dev/joke/Miscellaneous?type=twopart&amount=2
    @GET("joke/{category}")
    fun getJoke(
        @Path("category") category: String,
        @Query("amount") amount: Int,
        @Query("blacklistFlags") flags: String? = null,
        @Query("type") type: String? = null
    ): Call<JokesResponse>
}