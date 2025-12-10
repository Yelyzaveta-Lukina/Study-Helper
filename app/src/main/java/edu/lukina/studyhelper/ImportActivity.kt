package edu.lukina.studyhelper

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Bundle class, used for passing data between Android components.
import android.os.Bundle
// Imports the View class, the basic building block for user interface components.
import android.view.View
// Imports the widget classes, including Button, LinearLayout, and CheckBox.
import android.widget.*
// Imports AppCompatActivity, a base class for activities that use the support library action bar features.
import androidx.appcompat.app.AppCompatActivity
// Imports the Toolbar class for implementing a custom app bar.
import androidx.appcompat.widget.Toolbar
// Imports the 'children' extension property, which provides an easy way to iterate over a ViewGroup's child views.
import androidx.core.view.children
// Imports ViewModelProvider, which provides ViewModels to a given scope (like an Activity or Fragment).
import androidx.lifecycle.ViewModelProvider
// Imports the Subject data class from your project's model package.
import edu.lukina.studyhelper.model.Subject
// Imports the ImportViewModel from your project's viewmodel package.
import edu.lukina.studyhelper.viewmodel.ImportViewModel

// Declares the ImportActivity class, inheriting from AppCompatActivity to gain Activity functionality.
class ImportActivity : AppCompatActivity() {

    // Declares a private, late-initialized property to hold the LinearLayout container for subject checkboxes.
    private lateinit var subjectLayoutContainer: LinearLayout
    // Declares a private, late-initialized property for the ProgressBar shown during loading.
    private lateinit var loadingProgressBar: ProgressBar

    // Declares a read-only property 'importViewModel' that is lazily initialized.
    // The ViewModel is retrieved using a ViewModelProvider, tying it to this activity's lifecycle.
    private val importViewModel: ImportViewModel by lazy {
        ViewModelProvider(this).get(ImportViewModel::class.java)
    }

    // Overrides the onCreate method, which is called when the activity is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Calls the superclass's implementation of onCreate.
        super.onCreate(savedInstanceState)
        // Sets the user interface layout for this activity from the specified XML resource file.
        setContentView(R.layout.activity_import)

        // Finds the Toolbar view from the layout by its ID.
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // Sets the Toolbar as the activity's official app bar.
        setSupportActionBar(toolbar)

        // Enables the "Up" button (back arrow) on the app bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Sets the title text displayed on the app bar.
        supportActionBar?.title = getString(R.string.import_questions)

        // Finds and assigns the LinearLayout view from the layout by its ID.
        subjectLayoutContainer = findViewById(R.id.subject_layout)

        // Finds the 'Import' button and sets a click listener to call the 'importButtonClick' method.
        findViewById<Button>(R.id.import_button).setOnClickListener { importButtonClick() }

        // Finds and assigns the ProgressBar view from the layout by its ID.
        loadingProgressBar = findViewById(R.id.loading_progress_bar)
        // Sets the progress bar's visibility to VISIBLE, making it appear on the screen.
        loadingProgressBar.visibility = View.VISIBLE

        // Calls the ViewModel to start fetching the list of subjects from the web API.
        importViewModel.fetchSubjects()
        // Observes the 'fetchedSubjectList' LiveData for changes. The lambda is executed when the data is ready.
        importViewModel.fetchedSubjectList.observe(this, { subjectList: List<Subject> ->

            // Hides the progress bar by setting its visibility to GONE.
            loadingProgressBar.visibility = View.GONE

            // Iterates through the list of Subject objects received from the API.
            for (subject in subjectList) {
                // Creates a new CheckBox instance within the context of this Activity for proper theming.
                val checkBox = CheckBox(this)
                // Sets the font size for the checkbox text.
                checkBox.textSize = 24f
                // Sets the text of the checkbox to the subject's name.
                checkBox.text = subject.text
                // Attaches the full Subject object to the CheckBox's tag property for later retrieval.
                checkBox.tag = subject
                // Adds the newly created CheckBox to the LinearLayout container.
                subjectLayoutContainer.addView(checkBox)
            }
        })

        // Observes the 'importedSubject' LiveData.
        // The lambda is executed when questions for a subject are successfully imported.
        importViewModel.importedSubject.observe(this, {
            // Displays a short Toast message to the user confirming the successful import.
            Toast.makeText(applicationContext, "$it imported successfully",
                Toast.LENGTH_SHORT).show()
        })
    }

    // Overrides the method to handle clicks on the "Up" button (back arrow) in the app bar.
    override fun onSupportNavigateUp(): Boolean {
        // Triggers the standard back button behavior, finishing the current activity.
        onBackPressedDispatcher.onBackPressed()
        // Returns true to indicate that the event has been handled.
        return true
    }

    // Defines a private function to be executed when the 'Import' button is clicked.
    private fun importButtonClick() {

        // Iterates through all the child views within the 'subjectLayoutContainer'.
        for (child in subjectLayoutContainer.children) {
            // Casts the current child view to a CheckBox.
            val checkBox = child as CheckBox
            // Checks if the checkbox is currently checked by the user.
            if (checkBox.isChecked) {
                // Retrieves the full Subject object that was stored in the checkbox's tag property.
                val subject = checkBox.tag as Subject

                // Calls the ViewModel's 'addSubject' method to start the import process for this subject.
                importViewModel.addSubject(subject)
            }
        }
    }
}
