package edu.lukina.studyhelper

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Bundle class, used for passing data between Android components.
import android.os.Bundle
// Imports the Menu class, which represents the options menu in the app bar.
import android.view.Menu
// Imports the MenuItem class, which represents an individual item within a Menu.
import android.view.MenuItem
// Imports the View class, the basic building block for user interface components.
import android.view.View
// Imports the ViewGroup class, a special view that can contain other views.
import android.view.ViewGroup
// Imports the Button widget class.
import android.widget.Button
// Imports the TextView widget class.
import android.widget.TextView
// Imports AppCompatActivity, a base class for activities
// that use the support library action bar features.
import androidx.appcompat.app.AppCompatActivity
// Imports the Question data class from your project's model package.
import edu.lukina.studyhelper.model.Question
// Imports the Subject data class from your project's model package.
import edu.lukina.studyhelper.model.Subject
// Imports the QuestionListViewModel from your project's viewmodel package.
import edu.lukina.studyhelper.viewmodel.QuestionListViewModel
// Imports ViewModelProvider, which provides ViewModels to a given scope (like an Activity or Fragment).
import androidx.lifecycle.ViewModelProvider
// Imports the Activity class.
import android.app.Activity
// Imports the Intent class, used to start new activities or pass data.
import android.content.Intent
// Imports the Toast class, used to display short, temporary messages to the user.
import android.widget.Toast
// Imports ActivityResultContracts, which defines the standard contracts for starting activities for a result.
import androidx.activity.result.contract.ActivityResultContracts
// Imports the Toolbar class for implementing a custom app bar.
import androidx.appcompat.widget.Toolbar
// Imports the Snackbar class from the Material Design library for showing brief messages with an optional action.
import com.google.android.material.snackbar.Snackbar

// Declares the QuestionActivity class, inheriting from AppCompatActivity to gain Activity functionality.
class QuestionActivity : AppCompatActivity() {

    // Declares a private, late-initialized property to hold the current Subject object.
    private lateinit var subject: Subject
    // Declares a private, late-initialized property to hold the list of questions for the current subject.
    private lateinit var questionList: List<Question>
    // Declares a private, late-initialized property for the "Answer:" label TextView.
    private lateinit var answerLabelTextView: TextView
    // Declares a private, late-initialized property for the TextView that displays the answer text.
    private lateinit var answerTextView: TextView
    // Declares a private, late-initialized property for the "Show Answer" / "Hide Answer" button.
    private lateinit var answerButton: Button
    // Declares a private, late-initialized property for the TextView that displays the question text.
    private lateinit var questionTextView: TextView
    // Declares a private, late-initialized property for the layout that is visible when there are questions.
    private lateinit var showQuestionLayout: ViewGroup
    // Declares a private, late-initialized property for the layout that is visible when there are no questions.
    private lateinit var noQuestionLayout: ViewGroup
    // Declares a private property to keep track of the index of the currently displayed question.
    private var currentQuestionIndex = 0
    // Declares a private, late-initialized property to temporarily store a question after it has been deleted,
    // for the Undo feature.
    private lateinit var deletedQuestion: Question

    // Declares a read-only property 'questionListViewModel' that is lazily initialized.
    // The ViewModel is retrieved using a ViewModelProvider, tying it to this activity's lifecycle.
    private val questionListViewModel: QuestionListViewModel by lazy {
        ViewModelProvider(this).get(QuestionListViewModel::class.java)
    }

    // Registers a callback for an activity result, specifically for when a new question is added.
    private val addQuestionResultLauncher = registerForActivityResult(
        // Specifies the contract for starting an activity and expecting a result back.
        ActivityResultContracts.StartActivityForResult()) { result ->
        // Checks if the result returned from the launched activity is 'RESULT_OK'.
        if (result.resultCode == Activity.RESULT_OK) {

            // Sets the current index to the last position in the list to display the newly added question.
            currentQuestionIndex = questionList.size

            // Shows a Toast message to confirm that the question was added successfully.
            Toast.makeText(this, R.string.question_added, Toast.LENGTH_SHORT).show()
        }
    }

    // Defines a companion object to hold constants for the class.
    companion object {
        // Defines a constant for the key used to pass the subject ID in an Intent's extras.
        const val EXTRA_SUBJECT_ID = "edu.lukina.studyhelper.subject_id"
        // Defines a constant for the key used to pass the subject text in an Intent's extras.
        const val EXTRA_SUBJECT_TEXT = "edu.lukina.studyhelper.subject_text"
    }

    // Overrides the onCreate method, which is called when the activity is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Calls the superclass's implementation of onCreate.
        super.onCreate(savedInstanceState)
        // Sets the user interface layout for this activity from the specified XML resource file.
        setContentView(R.layout.activity_question)

        // Finds the Toolbar view from the layout by its ID.
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // Sets the Toolbar as the activity's official app bar.
        setSupportActionBar(toolbar)

        // Enables the "Up" button (back arrow) on the app bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Finds and assigns the question TextView from the layout by its ID.
        questionTextView = findViewById(R.id.question_text_view)
        // Finds and assigns the answer label TextView from the layout by its ID.
        answerLabelTextView = findViewById(R.id.answer_label_text_view)
        // Finds and assigns the answer TextView from the layout by its ID.
        answerTextView = findViewById(R.id.answer_text_view)
        // Finds and assigns the answer Button from the layout by its ID.
        answerButton = findViewById(R.id.answer_button)
        // Finds and assigns the layout for displaying a question from the layout by its ID.
        showQuestionLayout = findViewById(R.id.show_question_layout)
        // Finds and assigns the layout for when there are no questions from the layout by its ID.
        noQuestionLayout = findViewById(R.id.no_question_layout)

        // Sets a click listener for the answer button to call the 'toggleAnswerVisibility' method.
        answerButton.setOnClickListener { toggleAnswerVisibility() }
        // Finds the "Add Question" button and sets a click listener to call the 'addQuestion' method.
        findViewById<Button>(R.id.add_question_button).setOnClickListener { addQuestion() }

        // Retrieves the subject ID passed from SubjectActivity via the Intent.
        val subjectId = intent.getLongExtra(EXTRA_SUBJECT_ID, 0)
        // Retrieves the subject text passed from SubjectActivity via the Intent.
        val subjectText = intent.getStringExtra(EXTRA_SUBJECT_TEXT)
        // Creates a new Subject object using the ID and text retrieved from the Intent.
        subject = Subject(subjectId, subjectText!!)

        // Initializes the question list as empty before the data is loaded.
        questionList = emptyList()
        // Tells the ViewModel to start loading the questions for the given subject ID.
        questionListViewModel.loadQuestions(subjectId)
        // Observes the 'questionListLiveData' for changes. The lambda is executed when the data is ready.
        questionListViewModel.questionListLiveData.observe(this) { questionList ->
            // Updates the activity's question list with the new data from the ViewModel.
            this.questionList = questionList
            // Calls updateUI to refresh the screen with the new list of questions.
            updateUI()
        }
    }

    // Overrides the method to handle clicks on the "Up" button (back arrow) in the app bar.
    override fun onSupportNavigateUp(): Boolean {
        // Triggers the standard back button behavior, finishing the current activity.
        onBackPressedDispatcher.onBackPressed()
        // Returns true to indicate that the event has been handled.
        return true
    }

    // Defines a private function to update the user interface based on the current state.
    private fun updateUI() {
        // Calls showQuestion to display the question at the current index.
        showQuestion(currentQuestionIndex)

        // Checks if the question list is empty.
        if (questionList.isEmpty()) {
            // If empty, updates the app bar title to show "0 of 0".
            updateAppBarTitle()
            // And displays the "no questions" layout.
            displayQuestion(false)
        } else {
            // If not empty, displays the main question layout.
            displayQuestion(true)
        }
    }

    // Overrides the method to create the options menu in the app bar.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflates the menu resource file to populate the menu items.
        menuInflater.inflate(R.menu.question_menu, menu)
        // Returns true to display the menu.
        return true
    }

    // Overrides the method to handle clicks on menu items.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Uses a 'when' expression to determine which menu item was clicked based on its ID.
        return when (item.itemId) {
            // Handles the home/up button click.
            android.R.id.home -> {
                // Calls the onSupportNavigateUp method to handle the back navigation.
                onSupportNavigateUp()
                // Returns true to indicate the event was handled.
                true
            }
            // Handles the 'previous' menu item click.
            R.id.previous -> {
                // Shows the previous question in the list.
                showQuestion(currentQuestionIndex - 1)
                // Returns true to indicate the event was handled.
                true
            }
            // Handles the 'next' menu item click.
            R.id.next -> {
                // Shows the next question in the list.
                showQuestion(currentQuestionIndex + 1)
                // Returns true to indicate the event was handled.
                true
            }
            // Handles the 'add' menu item click.
            R.id.add -> {
                // Calls the function to start the add question process.
                addQuestion()
                // Returns true to indicate the event was handled.
                true
            }
            // Handles the 'edit' menu item click.
            R.id.edit -> {
                // Calls the function to start the edit question process.
                editQuestion()
                // Returns true to indicate the event was handled.
                true
            }
            // Handles the 'delete' menu item click.
            R.id.delete -> {
                // Calls the function to delete the current question.
                deleteQuestion()
                // Returns true to indicate the event was handled.
                true
            }
            // For any other menu item, delegates the call to the superclass.
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Defines a private function to switch between the main question layout and the "no questions" layout.
    private fun displayQuestion(display: Boolean) {
        // Checks if the question layout should be displayed.
        if (display) {
            // If true, makes the main question layout visible.
            showQuestionLayout.visibility = View.VISIBLE
            // And makes the "no questions" layout gone (invisible and takes up no space).
            noQuestionLayout.visibility = View.GONE
        } else {
            // If false, makes the main question layout gone.
            showQuestionLayout.visibility = View.GONE
            // And makes the "no questions" layout visible.
            noQuestionLayout.visibility = View.VISIBLE
        }
    }

    // Defines a private function to update the title in the app bar.
    private fun updateAppBarTitle() {
        // Creates a formatted string showing the subject name and current question number.
        val title = resources.getString(R.string.question_number, subject.text,
            currentQuestionIndex + 1, questionList.size)
        // Sets the created string as the title of the app bar.
        supportActionBar?.title = title
    }

    // Defines a private function to start the QuestionEditActivity to add a new question.
    private fun addQuestion() {
        // Creates an Intent to launch QuestionEditActivity.
        val intent = Intent(this, QuestionEditActivity::class.java)
        // Puts the current subject's ID into the Intent's extras.
        intent.putExtra(EXTRA_SUBJECT_ID, subject.id)
        // Launches the activity using the previously registered result launcher.
        addQuestionResultLauncher.launch(intent)
    }

    // Registers a callback for an activity result, specifically for when a question is edited.
    private val editQuestionResultLauncher = registerForActivityResult(
        // Specifies the contract for starting an activity and expecting a result back.
        ActivityResultContracts.StartActivityForResult()) { result ->
        // Checks if the result returned from the launched activity is 'RESULT_OK'.
        if (result.resultCode == Activity.RESULT_OK) {
            // Shows a Toast message to confirm that the question was updated successfully.
            Toast.makeText(this, R.string.question_updated, Toast.LENGTH_SHORT).show()
        }
    }

    // Defines a private function to start the QuestionEditActivity to edit the current question.
    private fun editQuestion() {
        // Checks if there are questions and the current index is valid.
        if (questionList.isNotEmpty() && currentQuestionIndex >= 0) {
            // Creates an Intent to launch QuestionEditActivity.
            val intent = Intent(this, QuestionEditActivity::class.java)
            // Puts the ID of the current question into the Intent's extras.
            intent.putExtra(QuestionEditActivity.EXTRA_QUESTION_ID, questionList[currentQuestionIndex].id)
            // Launches the activity using the previously registered result launcher for editing.
            editQuestionResultLauncher.launch(intent)
        }
    }

    // Defines a private function to delete the currently displayed question.
    private fun deleteQuestion() {
        // Checks if there are questions and the current index is valid.
        if (questionList.isNotEmpty() && currentQuestionIndex >= 0) {
            // Gets the question object at the current index.
            val question = questionList[currentQuestionIndex]
            // Tells the ViewModel to delete the question from the database.
            questionListViewModel.deleteQuestion(question)

            // Saves the deleted question object in case the user wants to undo the action.
            deletedQuestion = question

            // Creates and configures a Snackbar to show a "Question deleted" message.
            val snackbar = Snackbar.make(
                // Specifies the root view for the Snackbar to attach to.
                findViewById(R.id.coordinator_layout),
                // Sets the message text.
                R.string.question_deleted, Snackbar.LENGTH_LONG
            )
            // Sets an action on the Snackbar, providing an "Undo" button.
            snackbar.setAction(R.string.undo) {
                // If "Undo" is clicked, tell the ViewModel to add the deleted question back to the database.
                questionListViewModel.addQuestion(deletedQuestion)
            }
            // Displays the configured Snackbar to the user.
            snackbar.show()
        }
    }

    // Defines a private function to display a question at a specific index.
    private fun showQuestion(questionIndex: Int) {
        // Checks if the question list is not empty.
        if (questionList.isNotEmpty()) {
            // Creates a variable to hold the new index, allowing for wrapping around the list.
            var newQuestionIndex = questionIndex

            // If the index is less than 0, wrap around to the end of the list.
            if (questionIndex < 0) {
                newQuestionIndex = questionList.size - 1
            } // If the index is out of bounds (greater than or equal to size), wrap around to the beginning.
            else if (questionIndex >= questionList.size) {
                newQuestionIndex = 0
            }

            // Updates the current question index to the new, validated index.
            currentQuestionIndex = newQuestionIndex
            // Updates the app bar title to reflect the new question number.
            updateAppBarTitle()

            // Gets the question object at the new current index.
            val question = questionList[currentQuestionIndex]
            // Sets the question text in the corresponding TextView.
            questionTextView.text = question.text
            // Sets the answer text in the corresponding TextView.
            answerTextView.text = question.answer
            // Hides the answer TextView by default when showing a new question.
            answerTextView.visibility = View.INVISIBLE
            // Hides the answer label TextView by default.
            answerLabelTextView.visibility = View.INVISIBLE
            // Resets the button text to "Show Answer".
            answerButton.setText(R.string.show_answer)

        } else {
            // If the question list is empty, sets the index to -1 to indicate no question is selected.
            currentQuestionIndex = -1
            // Updates the app bar title to show "0 of 0".
            updateAppBarTitle()
        }
    }

    // Defines a private function to toggle the visibility of the answer.
    private fun toggleAnswerVisibility() {
        // Checks if the answer TextView is currently visible.
        if (answerTextView.visibility == View.VISIBLE) {
            // If visible, changes the button text to "Show Answer".
            answerButton.setText(R.string.show_answer)
            // And hides the answer TextView.
            answerTextView.visibility = View.INVISIBLE
            // And hides the answer label TextView.
            answerLabelTextView.visibility = View.INVISIBLE
        } else {
            // If hidden, changes the button text to "Hide Answer".
            answerButton.setText(R.string.hide_answer)
            // And makes the answer TextView visible.
            answerTextView.visibility = View.VISIBLE
            // And makes the answer label TextView visible.
            answerLabelTextView.visibility = View.VISIBLE
        }
    }
}

