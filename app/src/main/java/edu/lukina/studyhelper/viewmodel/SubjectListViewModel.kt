package edu.lukina.studyhelper.viewmodel

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Application class from the Android framework, used to get the application context.
import android.app.Application
// Imports the AndroidViewModel class from AndroidX Lifecycle library.
// It's a ViewModel that is aware of the Application context.
import androidx.lifecycle.AndroidViewModel
// Imports the LiveData class from AndroidX Lifecycle library.
// LiveData is an observable data holder class that is lifecycle-aware.
import androidx.lifecycle.LiveData
// Imports the Subject data class from the 'model' package.
import edu.lukina.studyhelper.model.Subject
// Imports the StudyRepository class from the 'repo' package, which handles data operations.
import edu.lukina.studyhelper.repo.StudyRepository

// Declares the SubjectListViewModel class, which inherits from AndroidViewModel.
// It takes an 'application' instance as a constructor parameter.
class SubjectListViewModel(application: Application) : AndroidViewModel(application) {

    // Declares a private property 'studyRepo' and initializes it with the singleton instance of StudyRepository.
    // 'application.applicationContext' is used to get the global application context, preventing memory leaks.
    private val studyRepo = StudyRepository.getInstance(application.applicationContext)

    // Declares a public, read-only property 'subjectListLiveData' of type LiveData holding a list of Subjects.
    // It is initialized by calling the repository's getSubjects() method, which returns LiveData.
    val subjectListLiveData: LiveData<List<Subject>> = studyRepo.getSubjects()

    // Defines a public function 'addSubject' that takes a 'subject' object as a parameter.
    // It delegates the call to the 'addSubject' method of the 'studyRepo' to add the new subject.
    fun addSubject(subject: Subject) = studyRepo.addSubject(subject)

    // Defines a public function 'deleteSubject' that takes a 'subject' object as a parameter.
    // It delegates the call to the 'deleteSubject' method of the 'studyRepo' to delete the subject.
    fun deleteSubject(subject: Subject) = studyRepo.deleteSubject(subject)
}
