package edu.lukina.studyhelper.repo

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports all the necessary annotations and classes from the AndroidX Room persistence library.
import androidx.room.*
// Imports the Question data class from the 'model' package.
// This is the data entity that this DAO will interact with.
import edu.lukina.studyhelper.model.Question
// Imports the LiveData class from the AndroidX Lifecycle library.
import androidx.lifecycle.LiveData


// The @Dao annotation marks this interface as a Data Access Object.
// DAOs are responsible for defining the methods that access the database.
@Dao
// Declares an interface named QuestionDao. Room will generate an implementation of this interface.
interface QuestionDao {
    // The @Query annotation indicates that this method performs a database read operation.
    // "SELECT * FROM Question WHERE id = :id" is the SQL query to select a question by its primary key.
    @Query("SELECT * FROM Question WHERE id = :id")
    // Defines a function to get a single Question by its unique ID.
    // It returns a nullable Question (Question?) because a question with the given ID might not exist.
    fun getQuestion(id: Long): LiveData<Question?>

    // The @Query annotation specifies the SQL to be executed for this method.
    // "SELECT * FROM Question WHERE subject_id = :subjectId ORDER BY id"
    // selects all questions belonging to a specific subject, ordered by their ID.
    @Query("SELECT * FROM Question WHERE subject_id = :subjectId ORDER BY id")
    // Defines a function to get a list of all questions associated with a given 'subjectId'.
    // It returns an immutable List of Question objects.
    fun getQuestions(subjectId: Long): LiveData<List<Question>>

    // The @Insert annotation marks this as a method for inserting a new entry into the database.
    // 'onConflict = OnConflictStrategy.REPLACE' specifies
    // that if a question with the same primary key already exists, it should be replaced.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Defines a function to add a new question. It takes a Question object as a parameter.
    // It returns the 'rowId' of the newly inserted item as a Long.
    fun addQuestion(question: Question): Long

    // The @Update annotation marks this as a method for updating an existing entry.
    // Room uses the primary key of the passed 'question' object to find and update the corresponding row.
    @Update
    // Defines a function to update a question. It takes the updated Question object as a parameter.
    fun updateQuestion(question: Question)

    // The @Delete annotation marks this as a method for deleting an entry from the database.
    // Room uses the primary key of the passed 'question' object to find and delete the corresponding row.
    @Delete
    // Defines a function to delete a question. It takes the Question object to be deleted as a parameter.
    fun deleteQuestion(question: Question)
}
