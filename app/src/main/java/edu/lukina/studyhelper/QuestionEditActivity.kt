package edu.lukina.studyhelper

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Bundle class, used for passing data between Android components.
import android.os.Bundle
// Imports the EditText widget class, used for user text input.
import android.widget.EditText
// Imports AppCompatActivity, a base class for activities that use the support library action bar features.
import androidx.appcompat.app.AppCompatActivity
// Imports the FloatingActionButton class from the Material Design library.
import com.google.android.material.floatingactionbutton.FloatingActionButton
// Imports the Question data class from your project's model package.
import edu.lukina.studyhelper.model.Question
// Imports ViewModelProvider, which provides ViewModels to a given scope (like an Activity or Fragment).
import androidx.lifecycle.ViewModelProvider
// Imports the QuestionDetailViewModel from your project's viewmodel package.
import edu.lukina.studyhelper.viewmodel.QuestionDetailViewModel
// Imports the Toolbar class for implementing a custom app bar.
import androidx.appcompat.widget.Toolbar

// Declares the QuestionEditActivity class, inheriting from AppCompatActivity to gain Activity functionality.
class QuestionEditActivity : AppCompatActivity() {

    // Declares a private, late-initialized property to hold the EditText for the question text.
    private lateinit var questionEditText: EditText
    // Declares a private, late-initialized property to hold the EditText for the answer text.
    private lateinit var answerEditText: EditText
    // Declares a private property to store the ID of the question being edited, initialized to 0.
    private var questionId = 0L
    // Declares a private, late-initialized property to hold the Question object being added or edited.
    private lateinit var question: Question

    // Declares a read-only property 'questionDetailViewModel' that is lazily initialized.
    // The ViewModel is retrieved using a ViewModelProvider, tying it to this activity's lifecycle.
    private val questionDetailViewModel: QuestionDetailViewModel by lazy {
        ViewModelProvider(this).get(QuestionDetailViewModel::class.java)
    }

    // Defines a companion object to hold constants for the class.
    companion object {
        // Defines a constant for the key used to pass the question ID in an Intent's extras.
        const val EXTRA_QUESTION_ID = "edu.lukina.studyhelper.question_id"
    }

    // Overrides the onCreate method, which is called when the activity is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Calls the superclass's implementation of onCreate.
        super.onCreate(savedInstanceState)
        // Sets the user interface layout for this activity from the specified XML resource file.
        setContentView(R.layout.activity_question_edit)

        // Finds the Toolbar view from the layout by its ID.
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // Sets the Toolbar as the activity's official app bar.
        setSupportActionBar(toolbar)

        // Enables the "Up" button (back arrow) on the app bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Finds and assigns the question EditText view from the layout by its ID.
        questionEditText = findViewById(R.id.question_edit_text)
        // Finds and assigns the answer EditText view from the layout by its ID.
        answerEditText = findViewById(R.id.answer_edit_text)

        // Finds the 'Save' button and sets a click listener to call the 'saveButtonClick' method.
        findViewById<FloatingActionButton>(R.id.save_button).setOnClickListener { saveButtonClick() }

        // Retrieves the question ID passed from QuestionActivity, with a default value of -1 if not found.
        questionId = intent.getLongExtra(EXTRA_QUESTION_ID, -1L)

        // Checks if a question ID was passed, determining whether to add a new question or edit an existing one.
        if (questionId == -1L) {
            // If no ID was passed, this is a new question.
            // Creates a new, empty Question object.
            question = Question()
            // Assigns the subject ID, passed from the previous activity, to the new question.
            question.subjectId = intent.getLongExtra(QuestionActivity.EXTRA_SUBJECT_ID, 0)

            // Sets the title on the app bar to "Add Question".
            supportActionBar?.title = getString(R.string.add_question)
        } else {
            // If an ID was passed, this is an existing question to be edited.
            // Tells the ViewModel to load the question data for the given ID.
            questionDetailViewModel.loadQuestion(questionId)
            // Observes the 'questionLiveData' for changes. The lambda is executed when the data is ready.
            questionDetailViewModel.questionLiveData.observe(this) { question ->
                // Assigns the loaded Question object to the activity's property. The '!!' asserts it's not null.
                this.question = question!!
                // Calls updateUI to populate the EditText fields with the loaded data.
                updateUI()
            }

            // Sets the title on the app bar to "Edit Question".
            supportActionBar?.title = getString(R.string.edit_question)
        }
    }

    // Overrides the method to handle clicks on the "Up" button (back arrow) in the app bar.
    override fun onSupportNavigateUp(): Boolean {
        // Triggers the standard back button behavior, finishing the current activity.
        onBackPressedDispatcher.onBackPressed()
        // Returns true to indicate that the event has been handled.
        return true
    }

    // Defines a private function to populate the UI fields with the data from the 'question' object.
    private fun updateUI() {
        // Sets the text of the question EditText to the question's text.
        questionEditText.setText(question.text)
        // Sets the text of the answer EditText to the question's answer.
        answerEditText.setText(question.answer)
    }

    // Defines a private function to be executed when the 'Save' button is clicked.
    private fun saveButtonClick() {
        // Updates the 'question' object's text with the current content of the question EditText.
        question.text = questionEditText.text.toString()
        // Updates the 'question' object's answer with the current content of the answer EditText.
        question.answer = answerEditText.text.toString()

        // Checks if this is a new question or an existing one.
        if (questionId == -1L) {
            // If it's a new question, tells the ViewModel to add it to the database.
            questionDetailViewModel.addQuestion(question)
        } else {
            // If it's an existing question, tells the ViewModel to update it in the database.
            questionDetailViewModel.updateQuestion(question)
        }

        // Sets the result of this activity to RESULT_OK, signaling success to the calling activity.
        setResult(RESULT_OK)
        // Closes this activity and returns to the previous one.
        finish()
    }
}
