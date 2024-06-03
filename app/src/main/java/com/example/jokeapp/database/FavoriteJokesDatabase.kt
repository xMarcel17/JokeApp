package com.example.jokeapp.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.jokeapp.model.Joke

// Room Database class to store favorite jokes
// The database class should be abstract and extend RoomDatabase
// The class should be annotated with @Database and include the entities (Joke:class)
// and the version and exportSchema parameters. exportSchema can be used to export your
// database's schema (defines how data is organized within a relational database)
// information into a JSON file at compile time
@Database(entities = [Joke::class], version = 1, exportSchema = false)
abstract class FavoriteJokesDatabase : RoomDatabase() {
    // Abstract function to return the DAO (Data Access Object) for the database
    // to perform operations
    abstract fun jokesDatabaseDao(): JokesDatabaseDao
    // Companion object to create a singleton instance of the database
    companion object {
        // Volatile annotation to make sure the writes and reads to the INSTANCE
        // variable are atomic, meaning that the value of the variable is always up-to-date
        @Volatile
        private var INSTANCE: FavoriteJokesDatabase? = null
        fun getDatabase(context: Context): FavoriteJokesDatabase {
            // Return the instance of the database if it is already created,
            // synchronized to make sure only one thread can access the
            // database instance at a time
            return INSTANCE ?: synchronized(this) {
                // Create the database instance with the Room.databaseBuilder method.
                // Specify the context, the database class, and the name of the database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteJokesDatabase::class.java,
                    "jokes_database"
                ).build()
                // Assign the instance to the INSTANCE variable and return the instance
                INSTANCE = instance
                instance
            }
        }
    }
}

// Data Access Object (DAO) interface to define the database operations
@Dao
interface JokesDatabaseDao {
    // Query to get all favorite jokes from the database
    @Query("SELECT * FROM jokes")
    suspend fun getFavoriteJokes(): List<Joke>
    // Insert a joke into the database with the OnConflictStrategy.IGNORE strategy to
    // ignore the insert operation if the joke already exists. The function returns the
    // row ID of the inserted joke. If the joke already exists, the function returns -1
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertJoke(joke: Joke): Long
    // Delete a joke from the database.
    // The function returns the number of rows affected by the delete operation
    @Delete
    suspend fun deleteJoke(joke: Joke): Int
}