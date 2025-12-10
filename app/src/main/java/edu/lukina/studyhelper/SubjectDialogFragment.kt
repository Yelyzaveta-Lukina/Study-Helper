package edu.lukina.studyhelper

/**
 * Created by Yelyzaveta Lukina on 09/30/2025.
 */

// Imports the Dialog class, the base class for dialogs.
import android.app.Dialog
// Imports the Context class, which provides access to application-specific resources and classes.
import android.content.Context
// Imports the Bundle class, used for passing data between Android components.
import android.os.Bundle
// Imports the InputType class, which defines the type of content for an EditText.
import android.text.InputType
// Imports the EditText widget class, used for user text input.
import android.widget.EditText
// Imports AlertDialog, a subclass of Dialog that can show a title, up to three buttons,
// a list of selectable items, or a custom layout.
import androidx.appcompat.app.AlertDialog
// Imports DialogFragment, a fragment that displays a dialog window.
import androidx.fragment.app.DialogFragment

// Declares the SubjectDialogFragment class, inheriting from DialogFragment.
class SubjectDialogFragment: DialogFragment() {

    // Defines an interface that the hosting Activity must implement to receive the entered subject text.
    interface OnSubjectEnteredListener {
        // Declares a function signature for the callback method.
        fun onSubjectEntered(subjectText: String)
    }

    // Declares a private, late-initialized property to hold a reference to the listener (the hosting Activity).
    private lateinit var listener: OnSubjectEnteredListener

    // Overrides the method to create the dialog that the fragment will show.
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Creates a new EditText programmatically for the user to type in the subject name.
        val subjectEditText = EditText(requireActivity())
        // Sets the input type of the EditText to plain text.
        subjectEditText.inputType = InputType.TYPE_CLASS_TEXT
        // Restricts the EditText to a single line of input.
        subjectEditText.maxLines = 1
        // Returns a new AlertDialog built by the AlertDialog.Builder.
        return AlertDialog.Builder(requireActivity())
            // Sets the title of the dialog.
            .setTitle(R.string.subject)
            // Sets the custom view of the dialog to be the EditText we just created.
            .setView(subjectEditText)
            // Sets the positive button (e.g., "Create") and its click listener.
            .setPositiveButton(R.string.create) { dialog, whichButton ->
                // When the positive button is clicked, this lambda is executed.
                // Gets the text from the EditText and converts it to a string.
                val subject = subjectEditText.text.toString()
                // Calls the listener's onSubjectEntered method, passing the trimmed subject text.
                listener.onSubjectEntered(subject.trim())
            }
            // Sets the negative button (e.g., "Cancel") and a null listener, which simply dismisses the dialog.
            .setNegativeButton(R.string.cancel, null)
            // Creates the AlertDialog from the builder configuration.
            .create()
    }

    // Overrides the onAttach method, which is called when the fragment is first attached to its context (the Activity).
    override fun onAttach(context: Context) {
        // Calls the superclass's implementation of onAttach.
        super.onAttach(context)
        // Casts the context (the hosting Activity) to the OnSubjectEnteredListener interface
        // and assigns it to the listener property.
        listener = context as OnSubjectEnteredListener
    }
}
