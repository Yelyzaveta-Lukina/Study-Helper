package edu.lukina.studyhelper.viewmodel

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Application class, which is the base class for maintaining global application state.
import android.app.Application
// Imports AndroidViewModel, a ViewModel subclass that is aware of the Application context.
import androidx.lifecycle.AndroidViewModel
// Imports the Subject data class from your project's model package.
import edu.lukina.studyhelper.model.Subject
// Imports the StudyRepository singleton from your project's repo package.
import edu.lukina.studyhelper.repo.StudyRepository

// Declares the ImportViewModel class, which inherits from AndroidViewModel,
// giving it access to the Application context.
class ImportViewModel(application: Application) : AndroidViewModel(application) {

    // Gets the singleton instance of StudyRepository, passing the application context.
    private val studyRepo = StudyRepository.getInstance(application)

    // Exposes the importedSubject LiveData from the repository so the UI can observe it for changes.
    // This will notify the UI when a subject's questions have been fully imported.
    var importedSubject = studyRepo.importedSubject
    // Exposes the fetchedSubjectList LiveData from the repository for the UI to observe.
    // This will notify the UI when the list of subjects from the web API is ready.
    var fetchedSubjectList = studyRepo.fetchedSubjectList

    // Defines a function to handle the process of importing a new subject.
    fun addSubject(subject: Subject) {
        // First, adds the Subject object to the local Room database via the repository.
        studyRepo.addSubject(subject)
        // After the subject is saved (and now has a valid ID),
        // it tells the repository to fetch its questions from the web API.
        studyRepo.fetchQuestions(subject)
    }

    // Defines a function to initiate fetching the list of all available subjects from the web API.
    // It delegates the call directly to the repository's fetchSubjects method.
    fun fetchSubjects() = studyRepo.fetchSubjects()
}
