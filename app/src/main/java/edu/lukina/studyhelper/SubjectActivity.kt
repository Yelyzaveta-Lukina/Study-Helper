package edu.lukina.studyhelper

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Intent class, used to start new activities or pass data.
import android.content.Intent
// Imports the Bundle class, used for passing data between Android components and saving instance state.
import android.os.Bundle
// Imports the LayoutInflater class, used to instantiate layout XML files
// into their corresponding View objects.
import android.view.LayoutInflater
// Imports the View class, the basic building block for user interface components.
import android.view.View
// Imports the ViewGroup class, a special view that can contain other views.
import android.view.ViewGroup
// Imports the TextView widget class.
import android.widget.TextView
// Imports the Toast class, used to display short, temporary messages to the user.
import android.widget.Toast
// Imports AppCompatActivity, a base class for activities that use the support library action bar features.
import androidx.appcompat.app.AppCompatActivity
// Imports GridLayoutManager, a RecyclerView layout manager that arranges items in a grid.
import androidx.recyclerview.widget.GridLayoutManager
// Imports RecyclerView, an advanced and flexible version of ListView for displaying large data sets.
import androidx.recyclerview.widget.RecyclerView
// Imports the FloatingActionButton class from the Material Design library.
import com.google.android.material.floatingactionbutton.FloatingActionButton
// Imports the Subject data class from your project's model package.
import edu.lukina.studyhelper.model.Subject
// Imports the SubjectListViewModel from your project's viewmodel package.
import edu.lukina.studyhelper.viewmodel.SubjectListViewModel
// Imports ViewModelProvider, which provides ViewModels to a given scope (like an Activity or Fragment).
import androidx.lifecycle.ViewModelProvider
// Imports ActionMode, which provides a contextual action mode for performing actions on selected items.
import android.view.ActionMode
// Imports the Menu class, which represents the options menu in the app bar.
import android.view.Menu
// Imports the MenuItem class, which represents an individual item within a Menu.
import android.view.MenuItem
// Imports the Color class for handling color values.
import android.graphics.Color
// Imports the Toolbar class for implementing a custom app bar.
import androidx.appcompat.widget.Toolbar
// Imports PreferenceManager, which provides access to the default SharedPreferences for the app.
import androidx.preference.PreferenceManager

// Defines an enum class to represent the available sorting options for the subject list.
enum class SubjectSortOrder {
    // Represents sorting subjects alphabetically.
    ALPHABETIC,
    // Represents sorting subjects with the newest ones first.
    NEW_FIRST,
    // Represents sorting subjects with the oldest ones first.
    OLD_FIRST
}

// Declares the SubjectActivity class, inheriting from AppCompatActivity
// and implementing the OnSubjectEnteredListener interface.
class SubjectActivity : AppCompatActivity(),
    SubjectDialogFragment.OnSubjectEnteredListener {

    // Declares a private property for the RecyclerView adapter, initialized with an empty mutable list.
    private var subjectAdapter = SubjectAdapter(mutableListOf())
    // Declares a private, late-initialized property to hold the RecyclerView for displaying subjects.
    private lateinit var subjectRecyclerView: RecyclerView
    // Declares a private, late-initialized property to hold an array of colors for the subjects.
    private lateinit var subjectColors: IntArray
    // Declares a private flag to control whether the UI should be updated when LiveData changes.
    private var loadSubjectList = true
    // Declares a private, late-initialized property to hold the currently selected subject.
    private lateinit var selectedSubject: Subject
    // Declares a private property to store the position of the selected subject in the RecyclerView.
    private var selectedSubjectPosition = RecyclerView.NO_POSITION
    // Declares a private, nullable property to hold the current ActionMode instance.
    private var actionMode: ActionMode? = null

    // Declares a read-only property 'subjectListViewModel' that is lazily initialized.
    // The ViewModel is retrieved using a ViewModelProvider, tying it to this activity's lifecycle.
    private val subjectListViewModel: SubjectListViewModel by lazy {
        ViewModelProvider(this).get(SubjectListViewModel::class.java)
    }

    // Overrides the onCreate method, which is called when the activity is first created.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Calls the superclass's implementation of onCreate.
        super.onCreate(savedInstanceState)
        // Sets the user interface layout for this activity from the specified XML resource file.
        setContentView(R.layout.activity_subject)

        // Finds the Toolbar view from the layout by its ID.
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // Sets the Toolbar as the activity's official app bar.
        setSupportActionBar(toolbar)

        // Initializes the subjectColors array from the resource file.
        subjectColors = resources.getIntArray(R.array.subjectColors)

        // Finds the FloatingActionButton and sets a click listener to call the 'addSubjectClick' method.
        findViewById<FloatingActionButton>(R.id.add_subject_button).setOnClickListener {
            addSubjectClick()
        }

        // Finds and assigns the RecyclerView view from the layout by its ID.
        subjectRecyclerView = findViewById(R.id.subject_recycler_view)
        // Sets the layout manager for the RecyclerView to a GridLayout with 2 columns.
        subjectRecyclerView.layoutManager = GridLayoutManager(applicationContext, 2)

        // Observes the 'subjectListLiveData' for changes. The lambda is executed when the data is ready.
        subjectListViewModel.subjectListLiveData.observe(
            this, { subjectList ->
                // Checks if the UI should be updated based on the 'loadSubjectList' flag.
                if (loadSubjectList) {
                    // Calls updateUI to refresh the screen with the new list of subjects.
                    updateUI(subjectList)
                }
            })
    }

    // Overrides the onResume method, which is called every time the activity comes into the foreground.
    override fun onResume() {
        // Calls the superclass's implementation of onResume.
        super.onResume()
        // Sets the flag to true to allow the LiveData observer to update the UI.
        loadSubjectList = true

        // Force the list to refresh with the latest sort order.
        // Gets the current list of subjects from the LiveData.
        val sortedList = subjectListViewModel.subjectListLiveData.value
        // Checks if the list is not null.
        if (sortedList != null) {
            // Manually calls updateUI to re-apply sorting from settings.
            updateUI(sortedList)
        }
    }

    // Defines a private function to get the current sort order setting from SharedPreferences.
    private fun getSettingsSortOrder(): SubjectSortOrder {

        // Gets the default SharedPreferences for the application.
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        // Retrieves the sort order preference string, defaulting to "alpha".
        val sortOrderPref = sharedPrefs.getString("subject_order", "alpha")
        // Returns the corresponding SubjectSortOrder enum value based on the preference string.
        return when (sortOrderPref) {
            "alpha" -> SubjectSortOrder.ALPHABETIC
            "new_first" -> SubjectSortOrder.NEW_FIRST
            else -> SubjectSortOrder.OLD_FIRST
        }
    }

    // Defines a private function to update the RecyclerView with a new list of subjects.
    private fun updateUI(subjectList: List<Subject>) {
        // Creates a new adapter with the provided subject list.
        subjectAdapter = SubjectAdapter(subjectList as MutableList<Subject>)
        // Sets the sort order on the new adapter based on the current settings.
        subjectAdapter.sortOrder = getSettingsSortOrder()
        // Attaches the new adapter to the RecyclerView.
        subjectRecyclerView.adapter = subjectAdapter
    }

    // Overrides the method from the OnSubjectEnteredListener interface,
    // called when a new subject is entered in the dialog.
    override fun onSubjectEntered(subjectText: String) {
        // Checks if the entered text is not empty.
        if (subjectText.isNotEmpty()) {
            // Creates a new Subject object with the entered text.
            val subject = Subject(0, subjectText)

            // Sets the flag to false to prevent the LiveData observer from overwriting the adapter.
            loadSubjectList = false

            // Tells the ViewModel to add the new subject to the database.
            subjectListViewModel.addSubject(subject)

            // Manually adds the new subject to the current adapter to update the UI immediately.
            subjectAdapter.addSubject(subject)
            // Displays a short Toast message to confirm the subject was added.
            Toast.makeText(this, "Added $subjectText", Toast.LENGTH_SHORT).show()
        }
    }

    // Defines a private function to handle the click on the "add subject" button.
    private fun addSubjectClick() {
        // Creates a new instance of the SubjectDialogFragment.
        val dialog = SubjectDialogFragment()
        // Shows the dialog fragment.
        dialog.show(supportFragmentManager, "subjectDialog")
    }

    // Declares a private inner class for the RecyclerView's ViewHolder.
    private inner class SubjectHolder(inflater: LayoutInflater, parent: ViewGroup?) :
    // Inherits from RecyclerView.ViewHolder and implements click listeners.
        RecyclerView.ViewHolder(inflater.inflate(R.layout.recycler_view_items, parent, false)),
        View.OnLongClickListener,
        View.OnClickListener {

        // Declares a private, nullable property to hold the Subject object for this item.
        private var subject: Subject? = null
        // Declares a private, read-only property for the TextView that displays the subject text.
        private val subjectTextView: TextView

        // The initialization block for the SubjectHolder.
        init {
            // Sets the click listener for the item view to this class.
            itemView.setOnClickListener(this)
            // Sets the long-click listener for the item view to this class.
            itemView.setOnLongClickListener(this)
            // Finds and assigns the TextView from the item's layout.
            subjectTextView = itemView.findViewById(R.id.subject_text_view)
        }

        // Defines a function to bind a Subject object to this ViewHolder.
        fun bind(subject: Subject, position: Int) {
            // Assigns the passed Subject object to the holder's property.
            this.subject = subject
            // Sets the text of the TextView to the subject's text.
            subjectTextView.text = subject.text


            // Checks if the current item is the one that is selected.
            if (selectedSubjectPosition == position) {
                // If selected, changes the background color to red to highlight it.
                subjectTextView.setBackgroundColor(Color.RED)
            } else {
                // If not selected, sets the background color based on the length of the subject text.
                val colorIndex = subject.text.length % subjectColors.size
                // Applies the color from the colors array.
                subjectTextView.setBackgroundColor(subjectColors[colorIndex])
            }
        }

        // Overrides the onClick method to handle short clicks on an item.
        override fun onClick(view: View) {
            // Creates an Intent to start QuestionActivity.
            val intent = Intent(this@SubjectActivity, QuestionActivity::class.java)
            // Puts the selected subject's ID into the Intent's extras.
            intent.putExtra(QuestionActivity.EXTRA_SUBJECT_ID, subject!!.id)
            // Puts the selected subject's text into the Intent's extras.
            intent.putExtra(QuestionActivity.EXTRA_SUBJECT_TEXT, subject!!.text)

            // Starts the QuestionActivity.
            startActivity(intent)
        }

        // Overrides the onLongClick method to handle long clicks on an item.
        override fun onLongClick(view: View): Boolean {
            // Checks if an action mode is already active.
            if (actionMode != null) {
                // If so, do not start a new one.
                return false
            }

            // Assigns the clicked subject to the selectedSubject property.
            selectedSubject = subject!!
            // Stores the position of the selected item.
            selectedSubjectPosition = absoluteAdapterPosition

            // Notifies the adapter that the selected item has changed to trigger a re-bind and redraw.
            subjectAdapter.notifyItemChanged(selectedSubjectPosition)

            // Shows the Contextual Action Bar (CAB).
            actionMode = this@SubjectActivity.startActionMode(actionModeCallback)
            // Returns true to indicate the long click was consumed.
            return true
        }
    }

    // Defines a private property for the ActionMode callback object.
    private val actionModeCallback = object : ActionMode.Callback {

        // Overrides the method called when the action mode is created.
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflates the context menu resource to populate the CAB.
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.context_menu, menu)
            // Returns true to indicate the CAB should be displayed.
            return true
        }

        // Overrides the method called to refresh an action mode's action menu.
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Returns false, indicating no update is needed.
            return false
        }

        // Overrides the method called when a CAB action item is clicked.
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            // Checks if the clicked item is the 'delete' action.
            if (item.itemId == R.id.delete) {
                // Prevents the LiveData observer from updating the UI.
                loadSubjectList = false

                // Tells the ViewModel to delete the selected subject from the database.
                subjectListViewModel.deleteSubject(selectedSubject)

                // Manually removes the subject from the adapter.
                subjectAdapter.removeSubject(selectedSubject)

                // Closes the CAB.
                mode.finish()
                // Returns true to indicate the click was handled.
                return true
            }

            // Returns false if the click was not handled.
            return false
        }

        // Overrides the method called when the action mode is destroyed.
        override fun onDestroyActionMode(mode: ActionMode) {
            // Resets the actionMode property to null.
            actionMode = null

            // Notifies the adapter to redraw the deselected item.
            subjectAdapter.notifyItemChanged(selectedSubjectPosition)
            // Resets the selected position.
            selectedSubjectPosition = RecyclerView.NO_POSITION
        }
    }

    // Declares a private inner class for the RecyclerView's adapter.
    private inner class SubjectAdapter(private val subjectList: MutableList<Subject>) :
        RecyclerView.Adapter<SubjectHolder>() {

        // Defines a property for the sort order with a custom setter.
        var sortOrder: SubjectSortOrder = SubjectSortOrder.ALPHABETIC
            set(value) {
                // Sorts the internal list based on the new sort order value.
                when (value) {
                    SubjectSortOrder.OLD_FIRST -> subjectList.sortBy { it.updateTime }
                    SubjectSortOrder.NEW_FIRST -> subjectList.sortByDescending { it.updateTime }
                    else -> subjectList.sortWith(
                        compareBy(
                            String.CASE_INSENSITIVE_ORDER,
                            { it.text })
                    )
                }
                // Updates the backing field with the new value.
                field = value
            }

        // Overrides the method to create a new ViewHolder.
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
            // Gets a LayoutInflater from the application context.
            val layoutInflater = LayoutInflater.from(applicationContext)
            // Creates and returns a new SubjectHolder instance.
            return SubjectHolder(layoutInflater, parent)
        }

        // Overrides the method to bind data to an existing ViewHolder.
        override fun onBindViewHolder(holder: SubjectHolder, position: Int) {
            // Calls the holder's bind method with the subject at the given position.
            holder.bind(subjectList[position], position)
        }

        // Overrides the method to get the total number of items in the list.
        override fun getItemCount(): Int {
            // Returns the size of the subject list.
            return subjectList.size
        }

        // Defines a function to add a subject to the adapter's list.
        fun addSubject(subject: Subject) {

            // Adds the new subject to the beginning of the list.
            subjectList.add(0, subject)

            // Notifies the adapter that an item was inserted at the first position.
            notifyItemInserted(0)

            // Scrolls the RecyclerView to the top to show the new item.
            subjectRecyclerView.scrollToPosition(0)
        }

        // Defines a function to remove a subject from the adapter's list.
        fun removeSubject(subject: Subject) {

            // Finds the index of the subject in the list.
            val index = subjectList.indexOf(subject)
            // Checks if the subject was found.
            if (index >= 0) {

                // Removes the subject from the list at the found index.
                subjectList.removeAt(index)

                // Notifies the adapter that an item was removed at that position.
                notifyItemRemoved(index)
            }
        }
    }

    // Overrides the method to create the options menu in the app bar.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflates the menu resource file to populate the menu items.
        menuInflater.inflate(R.menu.subject_menu, menu)
        // Returns true to display the menu.
        return true
    }

    // Overrides the method to handle clicks on menu items.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Checks if the clicked item is the 'settings' action.
        if (item.itemId == R.id.settings) {
            // Creates an Intent to launch SettingsActivity.
            val intent = Intent(this, SettingsActivity::class.java)
            // Starts the SettingsActivity.
            startActivity(intent)
            // Returns true to indicate the click was handled.
            return true
            // Checks if the clicked item is the 'import_questions' action.
        } else if (item.itemId == R.id.import_questions) {
            // Creates an Intent to launch ImportActivity.
            val intent = Intent(this, ImportActivity::class.java)
            // Starts the ImportActivity.
            startActivity(intent)
            // Returns true to indicate the click was handled.
            return true
        }

        // For any other menu item, delegates the call to the superclass.
        return super.onOptionsItemSelected(item)
    }
}
