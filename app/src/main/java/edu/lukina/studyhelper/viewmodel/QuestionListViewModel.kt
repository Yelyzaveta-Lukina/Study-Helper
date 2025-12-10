package edu.lukina.studyhelper.viewmodel

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Application class from the Android framework, which is the base class for maintaining global application state.
import android.app.Application
// Imports the AndroidViewModel class from AndroidX Lifecycle library, a ViewModel that is aware of the Application context.
import androidx.lifecycle.AndroidViewModel
// Imports the LiveData class, an observable data holder class that is lifecycle-aware.
import androidx.lifecycle.LiveData
// Imports the MutableLiveData class, which is a LiveData whose value can be changed.
import androidx.lifecycle.MutableLiveData
// Imports the switchMap transformation function for LiveData.
import androidx.lifecycle.switchMap
// Imports the Question data class from the 'model' package.
import edu.lukina.studyhelper.model.Question
// Imports the StudyRepository class from the 'repo' package, which handles data operations.
import edu.lukina.studyhelper.repo.StudyRepository

// Declares the QuestionListViewModel class, which inherits from AndroidViewModel.
// It takes an 'application' instance as a constructor parameter.
class QuestionListViewModel(application: Application) : AndroidViewModel(application) {

    // Declares a private property 'studyRepo'
    // and initializes it with the singleton instance of StudyRepository.
    private val studyRepo = StudyRepository.getInstance(application)

    // Declares a private property 'subjectIdLiveData' as MutableLiveData that holds a Long.
    // This will be used to trigger updates to the question list when the subject ID changes.
    private val subjectIdLiveData = MutableLiveData<Long>()

    // Declares a public, read-only LiveData property 'questionListLiveData' that holds a list of Questions.
    val questionListLiveData: LiveData<List<Question>> =
    // Uses switchMap to transform the 'subjectIdLiveData'.
        // Whenever 'subjectIdLiveData' changes, this block is executed.
        subjectIdLiveData.switchMap { subjectId ->
            // It switches to a new LiveData instance returned by the repository for the new subject ID.
            // This ensures the UI observes the correct question list
            // and avoids managing previous LiveData subscriptions.
            studyRepo.getQuestions(subjectId)
        }

    // Defines a public function to load questions for a given subject.
    fun loadQuestions(subjectId: Long) {
        // Sets the value of 'subjectIdLiveData',
        // which triggers the switchMap transformation above to fetch the new question list.
        subjectIdLiveData.value = subjectId
    }

    // Defines a public function to add a new question.
    // It delegates the call directly to the 'addQuestion' method of the 'studyRepo'.
    fun addQuestion(question: Question) = studyRepo.addQuestion(question)

    // Defines a public function to delete a question.
    // It delegates the call directly to the 'deleteQuestion' method of the 'studyRepo'.
    fun deleteQuestion(question: Question) = studyRepo.deleteQuestion(question)
}
