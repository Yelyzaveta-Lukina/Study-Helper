package edu.lukina.studyhelper.repo

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports all the necessary annotations and classes from the AndroidX Room persistence library.
// This includes @Dao, @Query, @Insert, @Update, @Delete, etc.
import androidx.room.*
// Imports the Subject data class from the 'model' package.
// This is the data entity that this DAO will interact with.
import edu.lukina.studyhelper.model.Subject
// Imports the LiveData class from the AndroidX Lifecycle library.
import androidx.lifecycle.LiveData


// The @Dao annotation marks this interface as a Data Access Object.
// DAOs are the main component of Room and are responsible for defining the methods that access the database.
@Dao
// Declares an interface named SubjectDao. Interfaces are abstract and contain method definitions for Room to implement.
interface SubjectDao {

    // The @Query annotation indicates that this method performs a database query.
    // The SQL query is provided as a string parameter.
    // "SELECT * FROM Subject WHERE id = :id" selects all columns from the Subject table
    // where the 'id' matches the 'id' parameter.
    @Query("SELECT * FROM Subject WHERE id = :id")
    // Defines a function to get a single subject by its unique ID. It returns a LiveData of a Subject object.
    fun getSubject(id: Long): LiveData<Subject?>

    // The @Query annotation specifies the SQL to be executed for this method.
    // "SELECT * FROM Subject ORDER BY text COLLATE NOCASE" selects all subjects
    // and orders them by their 'text' field, ignoring case.
    @Query("SELECT * FROM Subject ORDER BY text COLLATE NOCASE")
    // Defines a function to get a list of all subjects. It returns a LiveData of a list of Subject objects.
    fun getSubjects(): LiveData<List<Subject>>

    // The @Insert annotation marks this as a method for inserting data into the database.
    // 'onConflict = OnConflictStrategy.REPLACE' specifies
    // that if a subject with the same primary key already exists, it should be replaced.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Defines a function to add a new subject.
    // It takes a Subject object as a parameter and returns the 'rowId' of the newly inserted item as a Long.
    fun addSubject(subject: Subject): Long

    // The @Update annotation marks this as a method for updating an existing entry in the database.
    // Room uses the primary key of the passed object to find the entry to update.
    @Update
    // Defines a function to update a subject. It takes the updated Subject object as a parameter.
    fun updateSubject(subject: Subject)

    // The @Delete annotation marks this as a method for deleting an entry from the database.
    // Room uses the primary key of the passed object to find the entry to delete.
    @Delete
    // Defines a function to delete a subject. It takes the Subject object to be deleted as a parameter.
    fun deleteSubject(subject: Subject)
}
