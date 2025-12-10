package edu.lukina.studyhelper.repo

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Database annotation from the AndroidX Room library, used to configure the database class.
import androidx.room.Database
// Imports the RoomDatabase class from the AndroidX Room library,
// which is the base class for all Room databases.
import androidx.room.RoomDatabase
// Imports the Question data class from the 'model' package, defining it as a database entity.
import edu.lukina.studyhelper.model.Question
// Imports the Subject data class from the 'model' package, defining it as a database entity.
import edu.lukina.studyhelper.model.Subject

// The @Database annotation marks this class as a Room database.
// 'entities' lists all the data classes that will be treated as tables in the database.
// 'version' specifies the database version, which must be incremented during schema migrations.
@Database(entities = [Question::class, Subject::class], version = 1)
// Declares an abstract class named StudyDatabase that inherits from RoomDatabase.
// Room will create an implementation of this class. 'abstract' is required for Room database classes.
abstract class StudyDatabase : RoomDatabase() {

    // Declares an abstract function that returns an instance of QuestionDao.
    // Room will generate the body for this method to provide access to the Question DAO.
    abstract fun questionDao(): QuestionDao
    // Declares an abstract function that returns an instance of SubjectDao.
    // Room will generate the body for this method to provide access to the Subject DAO.
    abstract fun subjectDao(): SubjectDao
}
