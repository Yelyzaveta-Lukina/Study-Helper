package edu.lukina.studyhelper.repo

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Context class from the Android framework, used to access application-specific resources and classes.
import android.content.Context
// Imports the Room class, which is the main access point for creating a Room database instance.
import androidx.room.Room
// Imports the LiveData class from AndroidX Lifecycle, a data holder that can be observed for changes.
import androidx.lifecycle.LiveData
// Imports the MutableLiveData class, a LiveData subclass that allows its value to be changed.
import androidx.lifecycle.MutableLiveData
// Imports the VolleyError class, which represents an error that occurred during a Volley network request.
import com.android.volley.VolleyError
// Imports the Question data class from the 'model' package.
import edu.lukina.studyhelper.model.Question
// Imports the Subject data class from the 'model' package.
import edu.lukina.studyhelper.model.Subject
// Imports the Executors class, used to create and manage thread pools for running tasks in the background.
import java.util.concurrent.Executors

// Declares the StudyRepository class with a private constructor to enforce the Singleton pattern.
class StudyRepository private constructor(context: Context) {

    // Creates a single-threaded executor to run all database write operations on a background thread.
    private val executor = Executors.newSingleThreadExecutor()

    // Declares a public MutableLiveData property to hold the name of a subject after its questions have been imported.
    var importedSubject = MutableLiveData<String>()
    // Declares a public MutableLiveData property to hold the list of subjects fetched from the web API.
    var fetchedSubjectList = MutableLiveData<List<Subject>>()

    // Declares and initializes a private property 'studyFetcher' to handle web API requests.
    private val studyFetcher: StudyFetcher = StudyFetcher(context.applicationContext)

    // Defines a function that initiates fetching all subjects from the web API.
    fun fetchSubjects() = studyFetcher.fetchSubjects(fetchListener)
    // Defines a function that initiates fetching all questions for a specific subject from the web API.
    fun fetchQuestions(subject: Subject) = studyFetcher.fetchQuestions(subject, fetchListener)

    // Defines a private listener object to handle callbacks from the StudyFetcher.
    private val fetchListener = object : StudyFetcher.OnStudyDataReceivedListener {
        // Overrides the function to be called when subjects are successfully received from the web API.
        override fun onSubjectsReceived(subjectList: List<Subject>) {
            // Updates the value of 'fetchedSubjectList' LiveData, notifying any observers on the main thread.
            fetchedSubjectList.value = subjectList
        }

        // Overrides the function to be called when questions are successfully received from the web API.
        override fun onQuestionsReceived(subject: Subject, questionList: List<Question>) {
            // Submits a new task to the background thread executor.
            executor.execute {
                // Iterates through each question received from the web API.
                for (question in questionList) {
                    // Assigns the parent subject's ID to the question's foreign key property.
                    question.subjectId = subject.id

                    // Calls the DAO's addQuestion method directly to add the question to the database.
                    question.id = questionDao.addQuestion(question)
                }

                // Safely posts a value to the LiveData from a background thread.
                importedSubject.postValue(subject.text)
            }
        }

        // Overrides the function to be called when a network error occurs.
        override fun onErrorResponse(error: VolleyError) {
            // Prints the stack trace of the error to the log for debugging.
            error.printStackTrace()
        }
    }

    // Defines a companion object, which allows for static-like members in Kotlin.
    companion object {
        // Declares a private, nullable, static-like variable to hold the single instance of StudyRepository.
        private var instance: StudyRepository? = null

        // Defines a public, static-like function to get the single instance of the repository.
        @Synchronized
        // It's synchronized to be thread-safe, ensuring only one instance is created.
        fun getInstance(context: Context): StudyRepository {
            // Checks if the 'instance' variable is null (has not been created yet).
            if (instance == null) {
                // If the instance is null, create a new StudyRepository, passing the application context.
                instance = StudyRepository(context)
            }
            // Returns the non-null instance of StudyRepository. The '!!' asserts that 'instance' is not null here.
            return instance!!
        }
    }

    // Declares a private property 'database' that holds the Room database instance.
    private val database : StudyDatabase = Room.databaseBuilder(
        // Passes the application context to avoid memory leaks.
        context.applicationContext,
        // Specifies the RoomDatabase class to be built.
        StudyDatabase::class.java,
        // Sets the name of the database file.
        "study.db"
    )
        // Creates and initializes the database instance based on the configuration above.
        .build()

    // Declares a private property 'subjectDao' and initializes it by getting the DAO from the database instance.
    private val subjectDao = database.subjectDao()
    // Declares a private property 'questionDao' and initializes it by getting the DAO from the database instance.
    private val questionDao = database.questionDao()

    // --- LiveData Queries (Room automatically runs these on a background thread) ---
    // Defines a public function to get a single subject by its ID, returning it as LiveData.
    fun getSubject(subjectId: Long): LiveData<Subject?> = subjectDao.getSubject(subjectId)
    // Defines a public function to get a list of all subjects, returning it as LiveData.
    fun getSubjects(): LiveData<List<Subject>> = subjectDao.getSubjects()
    // Defines a public function to get a single question by its ID, returning it as LiveData.
    fun getQuestion(questionId: Long): LiveData<Question?> = questionDao.getQuestion(questionId)
    // Defines a public function to get all questions for a specific subject ID, returning it as LiveData.
    fun getQuestions(subjectId: Long): LiveData<List<Question>> = questionDao.getQuestions(subjectId)

    // --- Write Operations (Must be manually moved to a background thread) ---
    // Defines a public function to add a new subject to the database.
    fun addSubject(subject: Subject) {
        // Submits the database operation to the background thread executor.
        executor.execute {
            // Calls the DAO to add the subject and updates the object's ID with the newly generated one.
            subject.id = subjectDao.addSubject(subject)
        }
    }

    // Defines a public function to delete a subject from the database.
    fun deleteSubject(subject: Subject) {
        // Submits the database operation to the background thread executor.
        executor.execute {
            // Calls the DAO to delete the specified subject.
            subjectDao.deleteSubject(subject)
        }
    }

    // Defines a public function to add a new question to the database.
    fun addQuestion(question: Question) {
        // Submits the database operation to the background thread executor.
        executor.execute {
            // Calls the DAO to add the question and updates the object's ID with the newly generated one.
            question.id = questionDao.addQuestion(question)
        }
    }

    // Defines a public function to update an existing question in the database.
    fun updateQuestion(question: Question) {
        // Submits the database operation to the background thread executor.
        executor.execute {
            // Calls the DAO to update the specified question.
            questionDao.updateQuestion(question)
        }
    }

    // Defines a public function to delete a question from the database.
    fun deleteQuestion(question: Question) {
        // Submits the database operation to the background thread executor.
        executor.execute {
            // Calls the DAO to delete the specified question.
            questionDao.deleteQuestion(question)
        }
    }
}
