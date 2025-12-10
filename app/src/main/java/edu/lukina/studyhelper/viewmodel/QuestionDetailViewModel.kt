package edu.lukina.studyhelper.viewmodel

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Application class, which is the base class for maintaining global application state.
import android.app.Application
// Imports AndroidViewModel, a ViewModel subclass that is aware of the Application context.
import androidx.lifecycle.AndroidViewModel
// Imports the LiveData class, a data holder that can be observed for changes.
import androidx.lifecycle.LiveData
// Imports the MutableLiveData class, a LiveData subclass that allows its value to be changed.
import androidx.lifecycle.MutableLiveData
// Imports the switchMap transformation function,
// which applies a function to the value of LiveData and returns a new LiveData object.
import androidx.lifecycle.switchMap
// Imports the Question data class from your project's model package.
import edu.lukina.studyhelper.model.Question
// Imports the StudyRepository singleton from your project's repo package.
import edu.lukina.studyhelper.repo.StudyRepository

// Declares the QuestionDetailViewModel class, inheriting from AndroidViewModel
// to get access to the Application context.
class QuestionDetailViewModel(application: Application) : AndroidViewModel(application) {

    // Gets the singleton instance of StudyRepository, which manages all data operations.
    private val studyRepo = StudyRepository.getInstance(application)

    // Declares a private MutableLiveData to hold the ID of the question currently being observed.
    private val questionIdLiveData = MutableLiveData<Long>()

    // Declares a public LiveData that will hold the Question object.
    val questionLiveData: LiveData<Question?> =
        // Uses a switchMap transformation on questionIdLiveData.
        questionIdLiveData.switchMap { questionId ->
            // Whenever questionIdLiveData changes, this block is executed.
            // It switches the observation to the LiveData returned by getQuestion().
            studyRepo.getQuestion(questionId)
        }

    // Defines a function to load a specific question by its ID.
    fun loadQuestion(questionId: Long) {
        // Sets the value of questionIdLiveData, which triggers the switchMap to fetch the new question data.
        questionIdLiveData.value = questionId
    }

    // Defines a function to add a new question to the database. It delegates the call to the repository.
    fun addQuestion(question: Question) = studyRepo.addQuestion(question)

    // Defines a function to update an existing question in the database.
    // It delegates the call to the repository.
    fun updateQuestion(question: Question) = studyRepo.updateQuestion(question)
}
